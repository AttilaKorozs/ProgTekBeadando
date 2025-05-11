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

| Tábla           | Mezők                                                                                           |
| --------------- |-------------------------------------------------------------------------------------------------|
| **Feed**        | `id` (PK), `name`, `url`, `refresh_interval_min`                                                |
| **Article**     | `id` (PK), `feed_id` (FK→Feed.id), `title`, `link`, `publication_date`, `content`               |
| **UserArticle** | `user` (FK→User.username), `article_id` (FK→Article.id), `is_favorite`, `is_read`, `updated_at` |
| **User** | `username`(PK), `password_hash`, `email`,`created_at`                                                           |

-- (1) Feed tábla
CREATE TABLE `Feed` (
`id` INT AUTO_INCREMENT PRIMARY KEY,
`name` VARCHAR(255) NOT NULL,
`url` VARCHAR(512) NOT NULL,
`refresh_interval_min` INT NOT NULL,
UNIQUE KEY `uq_feed_url` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- (2) Article tábla
CREATE TABLE `Article` (
`id` INT AUTO_INCREMENT PRIMARY KEY,
`feed_id` INT NOT NULL,
`title` VARCHAR(512) NOT NULL,
`link` VARCHAR(1024) NOT NULL,
`publication_date` DATETIME NOT NULL,
`content` TEXT,
UNIQUE KEY `uq_article_link` (`link`),
KEY `idx_article_feed` (`feed_id`),
KEY `idx_article_pubdate` (`publication_date`),
CONSTRAINT `fk_article_feed`
FOREIGN KEY (`feed_id`) REFERENCES `Feed` (`id`)
ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- (3) User tábla
CREATE TABLE `User` (
`username` VARCHAR(100) NOT NULL PRIMARY KEY,
`password_hash` VARCHAR(255) NOT NULL,
`email` VARCHAR(255) NOT NULL,
`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
UNIQUE KEY `uq_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- (4) UserArticle tábla
CREATE TABLE `UserArticle` (
`user` VARCHAR(100) NOT NULL,
`article_id` INT NOT NULL,
`is_favorite` TINYINT(1) DEFAULT 0,
`is_read` TINYINT(1) DEFAULT 0,
`updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`user`,`article_id`),
CONSTRAINT `fk_userarticle_user`
FOREIGN KEY (`user`) REFERENCES `User` (`username`)
ON DELETE CASCADE,
CONSTRAINT `fk_userarticle_article`
FOREIGN KEY (`article_id`) REFERENCES `Article` (`id`)
ON DELETE CASCADE,
KEY `idx_ua_favorite` (`is_favorite`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


---

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
