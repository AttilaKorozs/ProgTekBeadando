## Nagyvonalú rendszerterv



### 1. Projekt áttekintése

Az ötlet egy olyan asztali alkalmazás megvalósítása, amely lehetővé teszi a felhasználók számára, hogy bármely nyilvános RSS-forrást (feedet) egyszerűen felvegyenek, és valós időben megtekintsék a friss cikkeket. A cél, hogy a program ne csak adatmegjelenítő legyen: a felhasználó válogathat, szűrhet téma, cím vagy dátum alapján, elmentheti kedvenc cikkeket, valamint jelezheti, mely tartalmakat olvasta már. Minden, a feedekkel és a cikkekkel kapcsolatos információ – például cím, link, publikálási dátum és tartalom – egy MariaDB adatbázisban tárolódik.

A fejlesztés során a kiindulási pont az volt, hogy az alkalmazás ne csak működőképes, de könnyen bővíthető és karbantartható is legyen. Ezért a SOLID elveket, különösen az egyetlen felelősség (SRP), a nyitottság-zártság (OCP) és az interfészre szeparálás (ISP) pontokat tartottuk szem előtt.

---

### 2. Architektúra és rétegek

A szoftver felépítését öt logikai réteg köré szervezzük. Ez segít abban, hogy a felület, az üzleti szabályok, az adatbázis-kezelés és a segédfunkciók világosan elkülönüljenek egymástól. Az alábbi részletes leírás után bemutatjuk a projekt fizikai mappastruktúráját is, hogy átlátható legyen, hol találhatók a forráskód egyes részei.

**1. Presentation Layer (Megjelenítési réteg)**\
Ebben a rétegben találhatók a JavaFX és FXML segítségével megírt felhasználói képernyők és azok vezérlői. Minden ablak vagy párbeszédpanel egy-egy FXML fájl és a hozzá tartozó `Controller` osztály formájában jelenik meg. A kontrollerek végzik a felhasználói események validálását és továbbítását a Service réteg felé.

**2. Service Layer (Szolgáltatási réteg)**\
Az alkalmazás „szíve”: itt futnak a feedek letöltéséért, adatbázisba mentéséért, cikkek szűréséért és kedvenc-státusz kezeléséért felelős folyamatok. Minden olyan üzleti logika, ami nem közvetlenül a UI-hoz vagy az adatbázishoz kötődik, a Service rétegben található.

**3. Data Access Layer (DAO – Adatkezelési réteg)**\
A DAO osztályok (`FeedDAO`, `ArticleDAO`, `UserDAO`, `UserArticleDAO`) végzik a MariaDB-hez való kapcsolódást JDBC-n keresztül, és kezelik a CRUD műveleteket (Create, Read, Update, Delete). Ha később más adatbázisra váltanánk, csak ezt a réteget kellene módosítani.

**4. Model Layer (Adatmodell réteg)**\
Az entitásosztályok (`Feed`, `Article`, `User`, `UserArticle`) a mögöttes adatbázis-szerkezet Java reprezentációi. Ezek csak a mezőket és azok típusait tartalmazzák.

**5. Utility & Adapter réteg**\
A harmadik féltől származó ROME könyvtárat az RSS XML feldolgozására használjuk, de a `RssParser` adapter osztály gondoskodik róla, hogy a `SyndFeed` és `SyndEntry` típusokat a saját adatmodellre (`Feed`, `Article`) alakítsa. Ide tartozik a `NotificationUtil` is, mely a JavaFX-alapú értesítések megjelenítését végzi.

---

#### Projekt fizikai mappastruktúra

A következő mappastruktúra mutatja be, hogyan szervezzük a forráskódot a `src` alatt. Ezzel könnyen megtalálhatók a rétegek és komponensek.



```
├─ pom.xml                             # Maven projekt fájl (függőségek, build konfiguráció)
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ org/
│  │  │     └─ rrsreader/              # Maven groupId szerinti csomag
│  │  │        ├─ App.java             # Belépési pont
│  │  │        ├─ controller/
│  │  │        │  ├─ MainController.java
│  │  │        │  ├─ FeedController.java
│  │  │        │  └─ ArticleController.java
│  │  │        ├─ model/
│  │  │        │  ├─ Feed.java
│  │  │        │  ├─ Article.java
│  │  │        │  ├─ User.java
│  │  │        │  └─ UserArticle.java
│  │  │        ├─ service/
│  │  │        │  ├─ FeedService.java
│  │  │        │  └─ ArticleService.java
│  │  │        ├─ dao/
│  │  │        │  ├─ FeedDAO.java
│  │  │        │  ├─ ArticleDAO.java
│  │  │        │  ├─ UserDAO.java
│  │  │        │  └─ UserArticleDAO.java
│  │  │        └─ util/
│  │  │           ├─ RssParser.java
│  │  │           └─ NotificationUtil.java
│  │  └─ resources/
│  │     ├─ fxml/
│  │     │  ├─ main.fxml
│  │     │  ├─ feed.fxml
│  │     │  └─ article.fxml
│  │     └─ application.properties
│  └─ test/
│     ├─ java/
│     │  └─ org/
│     │     └─ rrsreader/
│     │        ├─ dao/
│     │        │  ├─ FeedDAOTest.java
│     │        │  └─ ArticleDAOTest.java
│     │        └─ service/
│     │           └─ FilterStrategyTest.java
│     └─ resources/
```


### 3. Adatbázismodell és táblák

A MariaDB adatbázisban az alábbi négy tábla található, melyek a feedek, cikkek, felhasználók és a felhasználói-cikk kapcsolatok kezeléséért felelősek.

#### 3.1 Feed tábla

| Oszlop neve            | Típus                   | Leírás                                      |
| ---------------------- | ----------------------- | ------------------------------------------- |
| `id`                   | `INT AUTO_INCREMENT`    | A feed egyedi azonosítója (Primer kulcs)    |
| `name`                 | `VARCHAR(255) NOT NULL` | A feed megjelenítendő neve                  |
| `url`                  | `VARCHAR(512) NOT NULL` | Az RSS feed URL címe                        |
| `refresh_interval_min` | `INT NOT NULL`          | Automatikus frissítés gyakorisága percekben |

#### 3.2 Article tábla

| Oszlop neve        | Típus                    | Leírás                                       |
| ------------------ | ------------------------ | -------------------------------------------- |
| `id`               | `INT AUTO_INCREMENT`     | A cikk egyedi azonosítója (Primer kulcs)     |
| `feed_id`          | `INT NOT NULL`           | Hivatkozás a `Feed.id` mezőre (Idegen kulcs) |
| `title`            | `VARCHAR(512) NOT NULL`  | A cikk címe                                  |
| `link`             | `VARCHAR(1024) NOT NULL` | A cikk webcíme                               |
| `publication_date` | `DATETIME NOT NULL`      | A cikk megjelenésének dátuma és időpontja    |
| `content`          | `TEXT`                   | A cikk teljes szöveges tartalma              |

#### 3.3 User tábla

| Oszlop neve     | Típus                                | Leírás                           |
| --------------- | ------------------------------------ | -------------------------------- |
| `id`            | `INT AUTO_INCREMENT`                 | A felhasználó egyedi azonosítója |
| `username`      | `VARCHAR(100) NOT NULL`              | Bejelentkezési név               |
| `password_hash` | `VARCHAR(255) NOT NULL`              | Titkosított jelszó (bcrypt hash) |
| `email`         | `VARCHAR(255) NOT NULL`              | E-mail cím                       |
| `created_at`    | `DATETIME DEFAULT CURRENT_TIMESTAMP` | Regisztráció időpontja           |

#### 3.4 UserArticle tábla

| Oszlop neve   | Típus                                                            | Leírás                                               |
| ------------- | ---------------------------------------------------------------- | ---------------------------------------------------- |
| `user_id`     | `INT NOT NULL`                                                   | Hivatkozás a `User.id` mezőre (Idegen kulcs)         |
| `article_id`  | `INT NOT NULL`                                                   | Hivatkozás az `Article.id` mezőre (Idegen kulcs)     |
| `is_favorite` | `TINYINT(1) DEFAULT 0`                                           | Jelzi, ha a felhasználó kedvencként jelölte a cikket |
| `is_read`     | `TINYINT(1) DEFAULT 0`                                           | Jelzi, ha a felhasználó olvasottnak jelölte a cikket |
| `updated_at`  | `DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Az utolsó státuszváltozás időpontja                  |

**Kapcsolatok és indexek**

- **Feed → Article**: Egy feedhez több cikk tartozhat.
- **User → Article**: A `UserArticle` tábla kezeli a kedvencek és olvasotti státuszokat.
- A `user_id` és `article_id` mezőkön közös primer kulcs vagy egyedi index biztosítja a sok-sok kapcsolat kezelését.
- Idegen kulcsokkal garantáljuk a referenciális integritást.

---

### 4. Tervezési minták alkalmazása

A fejlesztés során öt jól indokolt tervezési mintát használunk:

- **Strategy**: A cikkek szűrésére különböző algoritmusokat (például cím-, dátum- vagy kulcsszó alapú szűrés) implementálunk külön osztályokban, amelyek egy közös interface-t valósítanak meg. Így bármikor bővíthető a szűrés új módszerekkel anélkül, hogy a már meglévő kódot módosítani kellene.

- **Decorator**: Az `Article` objektumokat dinamikusan egészítjük ki kedvenc-státusszal anélkül, hogy az eredeti modell osztályát módosítanánk. Ez biztosítja az egy felelősség elvét, hiszen az alapmodell tiszta marad.

- **Command**: A feedekhez kapcsolódó műveleteket (hozzáadás, törlés, módosítás) mind parancsobjektumként kezeljük, amelyek `execute()` és `undo()` metódusokat kínálnak. Ez egyszerűsíti az undo/redo funkció megvalósítását, és az interfészek leszűkítésével betartja a szükségesség elvét.

- **Adapter**: A ROME által biztosított RSS-objektumokat egy adapter osztályon keresztül alakítjuk át a mi belső adatmodellünkre. Így akkor sem kell a teljes kódot átírni, ha később más RSS-kezelő könyvtárat választunk.

- **Template Method**: A feed-frissítés általános folyamata (kapcsolat nyitása, XML letöltése, adatbázis-mentés, értesítés) sablonosítottan fut végig, csak a lépések sorrendje és részletei változnak, ha szükséges.

Emellett a JavaFX architektúrája is követi az MVC/MVVM elvet, mely magyarázza a FXML és Controller rétegek szétválasztását.

---

### 5. Fő komponensek és felelősségek

- **App.java**\
  A program belépési pontja. Elindítja a JavaFX alkalmazási keretrendszert, betölti a kezdőképernyő FXML-jét, és megjeleníti az első ablakot.

- **MainController.java**\
  A főablak logikájáért felelős osztály. Kezeli a menüket, a feed-listát és az ablakokon belüli navigációt.

- **FeedService.java**\
  Végzi az RSS-fejek háttérben futó frissítését, értesíti a Service réteget és az adatbázist, valamint koordinálja a szűrés és a kedvencek kezelését.

- **DAO osztályok** (`FeedDAO`, `ArticleDAO`, `UserDAO`, `UserArticleDAO`)\
  Az adatbázissal való közvetlen kommunikációt, a rekordok lekérdezését, mentését, módosítását és törlését valósítják meg.

- **RssParser.java**\
  Adapterként működik: a ROME könyvtár típusrendszerét a saját `Feed` és `Article` modellünkre alakítja.

- **NotificationUtil.java**\
  A JavaFX felugró üzenetek megjelenítését segíti elő, amikor új cikk érkezik, vagy hiba történik.

- **FilterStrategy és implementációi**\
  Lehetővé teszik, hogy a felhasználó által kiválasztott szűrési mód (például cím szerinti vagy dátum szerinti) könnyen cserélhető legyen futásidőben.

- **CommandManager.java**\
  A parancsobjektumok sorba állításáért, végrehajtásáért és visszavonásáért felelős komponens.

---

### 6. Részletes ütemterv (4 hét)

A projekt összesen 80 munkaórára (2 fejlesztő × 4 hét × heti 10 óra) lett tervezve. A következőkben heti bontásban, feladatokra és óraszámokra lebontva látható a tervezett ütem:

#### 1. hét (20 óra összesen)

- **1. nap (4 óra)**
  - GitHub repository létrehozása, `main` ág védelme, alap `.gitignore` és `README.md` elkészítése (1 óra)
  - Maven projekt inicializálása, függőségek hozzáadása (JavaFX, MariaDB JDBC, ROME) (1 óra)
  - Egységes kódstílus beállítása (formatter, lint) (1 óra)
  - Alap projektstruktúra létrehozása `src/main/java` és `src/main/resources` mappákkal (1 óra)
- **2. nap (6 óra)**
  - MariaDB adatbázis indítása, tábla-sémák SQL szkriptek megírása (`Feed`, `Article`, `User`, `UserArticle`) (3 óra)
  - `application.properties` fájl és `DatabaseManager` osztály elkészítése, JDBC-kapcsolat tesztelése (3 óra)
- **3. nap (5 óra)**
  - JavaFX belépési pont (`App.java`) és első FXML (`main.fxml`) létrehozása (2 óra)
  - `MainController` váz osztály megírása, eseménykezelő sablon (3 óra)
- **4. nap (5 óra)**
  - Login képernyő (`login.fxml` és `LoginController`), egyszerű felhasználói bejelentkezés stub implementáció (2 óra)
  - Projekt build és futtatás végigpróbálása, rövid demo (3 óra)

#### 2. hét (20 óra összesen)

- **1. nap (4 óra)**
  - ROME dependency integrálása, alap RSS letöltés teszt implementálása (`RssParser` teszt) (2 óra)
  - `FeedService` osztály létrehozása, háttérszálas frissítés tervezése (2 óra)
- **2. nap (6 óra)**
  - `FeedDAO` és `ArticleDAO` CRUD műveletek megvalósítása (3 óra)
  - RSS-adatok mentése adatbázisba: `RssParser` → DAO integráció (3 óra)
- **3. nap (5 óra)**
  - `FeedController` és `feed.fxml` megírása: feed lista megjelenítése, új feed hozzáadása (3 óra)
  - Üres tesztkeret létrehozása DAO osztályokhoz (JUnit) (2 óra)
- **4. nap (5 óra)**
  - Automatikus frissítés triggerelése UI-ról (gomb + időzítő beállítása) (3 óra)
  - Rövid közös kódreview és hibajavítás (2 óra)

#### 3. hét (20 óra összesen)

- **1. nap (5 óra)**
  - `FilterStrategy` interface definiálása, cím alapú szűrés implementálása (3 óra)
  - Dátum szerinti szűrő írása és integrálása (2 óra)
- **2. nap (5 óra)**
  - Kedvenc-státusz kezelés `Decorator` mintával: `FavoriteArticleDecorator` osztály (3 óra)
  - UI elemek hozzáadása a kedvenc ikonhoz (2 óra)
- **3. nap (5 óra)**
  - `Command` parancsobjektumok létrehozása (`AddFeedCommand`, `DeleteFeedCommand`) (3 óra)
  - `CommandManager` váz osztály és undo/redo logika (2 óra)
- **4. nap (5 óra)**
  - `ArticleController` és `article.fxml`: cikkek listázása, részletek megjelenítése (3 óra)
  - Közös kódreview és integrációs teszt (2 óra)

#### 4. hét (20 óra összesen)

- **1. nap (4 óra)**
  - `NotificationUtil` implementálása: felugró értesítés új cikk esetén (2 óra)
  - Offline mentés (cikk tartalom tárolása és visszatöltése) (2 óra)
- **2. nap (5 óra)**
  - Unit tesztek írása DAO-k és szűrési logika számára (3 óra)
  - `CommandManager` undo/redo tesztelése (2 óra)
- **3. nap (5 óra)**
  - Hibakezelés finomhangolása, exception kezelés DAO és Service rétegben (3 óra)
  - Integrációs tesztek futtatása, regressziós teszt (2 óra)
- **4. nap (6 óra)**
  - Projekt dokumentáció véglegesítése: rendszerterv, ER-diagram, használati útmutató (3 óra)
  - Kód tisztítás, végső kódreview, `main` ágba merge és kiadásra előkészítés (3 óra)

---

####



### 7. Feladatkövetés és verziókezelés

A projekt kódját GitHub-on tároljuk. A `main` ágat védetté tesszük, ide csak átgondolt és tesztelt változtatás kerülhet be Pull Requesten keresztül. Minden sprinthez külön feature-ágat indítunk, az ág neve tartalmazza az issue számát és a rövid leírást, például:

```
feature/23-add-login-screen
hotfix/42-fix-auth-bug
```

A feladatokat GitHub Issues-ben kezeljük. Minden issue tartalmaz:

- Rövid, de beszédes címet (pl. `#23 Add login page`)
- Részletes leírást a tünetekről, a várt viselkedésről és a becsült munkaórákról
- Kapcsolódó pull requesteket és commitokat

A commit üzenetek követik a következő konvenciót, és hivatkoznak az issue-ra:

```
feat(#23): Add login form with validation
fix(#42): Prevent null pointer in authentication service
test(#23): Add unit tests for LoginService
docs: Update README with setup instructions
```

Ezek a prefixek jelzik a változtatás típusát, és elősegítik az automatikus kiadáskezelést is.

Ez a folyamat áttekinthetővé és követhetővé teszi a szoftverfejlesztés minden lépését, segítve a csapatot a hatékony együttműködésben.



