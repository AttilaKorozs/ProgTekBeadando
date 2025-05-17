## Nagyvonalú rendszerterv


### 1. Célkitűzés és funkciók

* **RSS feed kezelés**: tetszőleges nyilvános RSS-források felvétele, eltávolítása, frissítése.
* **Cikkek megjelenítése**: valós idejű lista, részletek (cím, dátum, tartalom) megtekintése.
* **Szűrés**: cím, dátum, kulcsszó szerinti keresés és szeparált megjelenítés.
* **Kedvencek & olvasottak**: tetszőleges cikk „kedvenc” vagy „olvasott” státuszra markolása.
* **Adatbázis**: MariaDB-ben tároljuk a feedeket, cikkeket és felhasználói státuszokat.
* **Cross-cutting**: Centralizált loggolás (SLF4J+Logback), unit-tesztek (JUnit), GitHub repo és issue-követés.

---

### 2. Rétegek és felelősségmegosztás

```
src/
├─ main/
│  ├─ java/org/rssreader/
│  │  ├─ app/           ← Belépési pont, alkalmazás‐szint indítás
│  │  │  └─ App.java
│  │  ├─ controller/    ← JavaFX Controller osztályok
│  │  │  ├─ MainController.java
│  │  │  ├─ FeedController.java
│  │  │  └─ ArticleController.java
│  │  ├─ service/       ← Üzleti logika, tranzakciók, validáció
│  │  │  ├─ FeedService.java
│  │  │  └─ ArticleService.java
│  │  ├─ dao/           ← JDBC DAO-k MariaDB-hez
│  │  │  ├─ FeedDAO.java
│  │  │  ├─ ArticleDAO.java
│  │  │  └─ UserArticleDAO.java
│  │  ├─ model/         ← Egyszerű POJO entitások
│  │  │  ├─ Feed.java
│  │  │  ├─ Article.java
│  │  │  └─ UserArticle.java
│  │  └─ util/          ← Segédfüggvények, log, adapterek
│  │     ├─ RssParser.java
│  │     ├─ LogUtil.java
│  │     └─ NotificationUtil.java
│  └─ resources/
│     ├─ fxml/
│     │  ├─ main.fxml
│     │  ├─ feed.fxml
│     │  └─ article.fxml
│     └─ application.properties
└─ test/                ← JUnit tesztek
   ├─ dao/
   │  ├─ FeedDAOTest.java
   │  └─ ArticleDAOTest.java
   └─ service/
      ├─ FeedServiceTest.java
      └─ ArticleServiceTest.java
```

---

### 3. Adatbázismodell
Az adatbázisod jelenleg négy táblából áll, az alábbi szerkezettel:

---

#### Feed

* **Mezők**

  * `url` VARCHAR(512) – a feed egyedi azonosítója (primer kulcs)
  * `name` VARCHAR(255) – a feed megjelenítendő neve
  * `refresh_interval_min` INT(11) – frissítés gyakorisága percekben

* **Primér kulcs**: `url`

---

#### Article

* **Mezők**

  * `id` INT(11) AUTO\_INCREMENT – a cikk egyedi száma (primer kulcs)
  * `feed_url` VARCHAR(512) – hivatkozás a `Feed.url`-re (idegen kulcs)
  * `title` VARCHAR(512) – a cikk címe
  * `link` VARCHAR(1024) – a cikk webcíme
  * `publication_date` DATETIME – megjelenés dátuma/időpontja
  * `content` TEXT – a cikk teljes szövege

* **Primér kulcs**: `id`

* **Idegen kulcs**:

  * `feed_url` → `Feed(url)` (ON DELETE CASCADE javasolt)

---

#### User

* **Mezők**

  * `username` VARCHAR(100) – a felhasználó azonosítója (primer kulcs)
  * `password_hash` VARCHAR(255) – titkosított jelszó (bcrypt hash)
  * `email` VARCHAR(255) – e-mail cím
  * `created_at` DATETIME – regisztráció időpontja (alapértelmezett: CURRENT\_TIMESTAMP)

* **Primér kulcs**: `username`

* **Egyedi index**: `email`

---

#### UserArticle

* **Mezők**

  * `user` VARCHAR(100) – hivatkozás a `User.username`-re (idegen kulcs)
  * `article_id` INT(11) – hivatkozás az `Article.id`-re (idegen kulcs)
  * `is_favorite` TINYINT(1) DEFAULT 0 – kedvenc státusz
  * `is_read` TINYINT(1) DEFAULT 0 – olvasott státusz
  * `updated_at` DATETIME DEFAULT CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP – utolsó módosítás időpontja

* **Kompozit primér kulcs**: (`user`, `article_id`)

* **Idegen kulcsok**:

  * `user` → `User(username)`
  * `article_id` → `Article(id)`

---

#### Kapcsolatok összefoglalva

* **Feed (1) ↔ Article (N)**

  * Minden cikkhez tartozik egy feed a `feed_url` mezőn keresztül.
* **User (1) ↔ Article (N) – many-to-many UserArticle**

  * A `UserArticle` tábla kezeli a felhasználók kedvencei (`is_favorite`) és olvasott státuszait (`is_read`).


### 4. Tervezési minták

#### 4.1 Strategy (Szűrési sablonok)

* **Miért?** A cikkek szűrését többféleképpen végezhetjük (cím, dátum, kulcsszó), és bővíthető legyen.
* **Megvalósítás**:

  ```java
  public interface FilterStrategy {
      List<Article> filter(List<Article> articles, String criterion);
  }
  public class TitleFilter implements FilterStrategy { … }
  public class DateFilter implements FilterStrategy { … }
  public class KeywordFilter implements FilterStrategy { … }
  ```
* A `FeedService` konstruktorában vagy setterrel kapja meg a használandó stratégiát.

#### 4.2 Adapter (RSS‐parsing)

* **Miért?** A ROME `SyndFeed`/`SyndEntry` típusait saját modellünkre kell alakítani.
* **Megvalósítás**:

  ```java
  public class RssParser {
      public List<Article> parse(SyndFeed feed) {
          return feed.getEntries().stream()
              .map(this::toArticle)
              .collect(Collectors.toList());
      }
      private Article toArticle(SyndEntry entry) { … }
  }
  ```
* Így később más XML/RSS könyvtárra váltáskor csak ez az osztály módosul.

#### 4.3 Decorator (Kedvenc/olvasott kiegészítés)

* **Miért?** Az alap `Article` osztály ne tartalmazzon felhasználói állapotot, viszont a UI-ban dinamikusan kell bővíteni.
* **Megvalósítás**:

  ```java
  public interface ArticleComponent {
      String getTitle();
      String getContent();
      // …
  }
  public class ArticleDecorator implements ArticleComponent {
      protected final ArticleComponent wrapped;
      public ArticleDecorator(ArticleComponent wrapped) { this.wrapped = wrapped; }
      // delegate alap metódusok…
  }
  public class FavoriteDecorator extends ArticleDecorator {
      private boolean isFav;
      public FavoriteDecorator(ArticleComponent wrapped, boolean isFav) {
          super(wrapped); this.isFav = isFav;
      }
      // plusz getIsFavorite(), esetleg ikon-kijelzés a UI-ban
  }
  public class ReadDecorator extends ArticleDecorator { … }
  ```
* A `ArticleService` visszaadhat lista helyett dekortozott komponenseket, amelyeket a Controller közvetlenül bind-elhet.

---

### 5. Cross-cutting aspektusok

* **Loggolás**: `LogUtil.getLogger(…)` minden rétegben, `INFO`/`ERROR` szintek.
* **Unit-tesztek**: DAO (mock DB vagy in-memory MariaDB), Service (mockolt DAO).
* **Repository & Issue-követés**: GitHub, `main` védett, feature-ágak, konvenciós commit-üzenetek.
* **Configuration**: `application.properties` log, DB-URL, refresh-interval alapérték.

---

### 6. Fő komponensek és felelősségek

| Komponens                  | Felelősség                                                                               |
| -------------------------- | ---------------------------------------------------------------------------------------- |
| **App.java**               | Alkalmazás indítása, JavaFX init, `main.fxml` betöltése                                  |
| **MainController.java**    | Szülőablak, menük, navigáció más nézetek (Feed/Article) között                           |
| **FeedController.java**    | Feed‐lista megjelenítése, új feed hozzáadása, törlése, frissítés indítása                |
| **ArticleController.java** | Cikklista, részletek panel, szűrők kezelése, kedvencek/olvasottak jelölése               |
| **FeedService.java**       | Letöltés (RssParser + HTTP), adatbázis mentés (FeedDAO + ArticleDAO), szűrők alkalmazása |
| **ArticleService.java**    | Kedvenc/olvasott státusz frissítése (UserArticleDAO), dekorátorok létrehozása            |
| **FeedDAO/ArticleDAO**     | CRUD műveletek MariaDB‐vel JDBC segítségével                                             |
| **UserArticleDAO**         | Kedvenc/olvasott státuszok lekérdezése, mentése                                          |
| **RssParser.java**         | Adapter: `SyndFeed` → `Article`                                                          |
| **FavoriteDecorator**      | Dinamikus kiterjesztés kedvenc státusszal                                                |
| **ReadDecorator**          | Dinamikus kiterjesztés olvasott státusszal                                               |

---

### 7. Megjegyzések

* **SOLID**: minden osztálynak egy egyértelmű feladata van (SRP), új szűrés/dekorátor bővíthető OCP, interfészekkel (ISP).
* **Tesztelhetőség**: DAO és Service réteg tiszta interfészekkel, könnyen mockolható.
* **Bővíthetőség**: később új szűrési stratégia vagy új dekorátor beemelése minimális kódmódosítással lehetséges.

Ezzel a tervvel a projekt minden kötelező eleme (ablakos JavaFX, MariaDB, SOLID, három tervezési minta, log, unit-tesztek, verziókezelés, dokumentáció) teljesül, és világos útmutatót ad lépésenkénti megvalósításhoz.
