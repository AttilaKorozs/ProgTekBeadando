## Nagyvonalú rendszerterv

### Készítők

* Borbás Péter
* Korózs Attila



A program a programtervezési technológiák tantárgy projektfeladataként készült.
Létrehoztunk egy JavaFX használatával készült asztali alkalmazást, amely lehetővé teszi a felhasználók számára RSS-hírcsatornák olvasását, kezelését és szűrését. A hírcsatornák és cikkek adatai egy MariaDB adatbázisban tárolódnak.

### 1. Célkitűzés és funkciók

* **RSS feed kezelés**: tetszőleges nyilvános RSS-források felvétele, eltávolítása, frissítése.
* **Cikkek megjelenítése**: valós idejű lista, részletek (cím, dátum, tartalom) megtekintése.
* **Szűrés**: cím, dátum, kulcsszó szerinti keresés és szeparált megjelenítés.
* **Kedvencek & olvasottak**: tetszőleges cikk „kedvenc” vagy „olvasott” státuszra markolása.
* **Adatbázis**: MariaDB-ben tároljuk a feedeket, cikkeket és felhasználói státuszokat.
* **Cross-cutting**: Centralizált loggolás (SLF4J+Logback), unit-tesztek (JUnit), GitHub repo és issue-követés.

---


### 2. Adatbázismodell

![EKKE_ProgTekBejadando](https://github.com/user-attachments/assets/5b65fa02-27b0-49a2-a7bd-2d1ef0d64ebe)



### 3. Rétegzett architektúra

* **Controller (JavaFX)**: UI események kezelése, szolgáltatások meghívása (pl. `ArticleController`, `FeedController`, `LoginController`)
* **Service**: üzleti logika, tranzakciók, validáció (pl. `FeedService`, `ArticleService`)
* **DAO**: adatbázis CRUD műveletek (pl. `FeedDAO`, `ArticleDAO`, `UserArticleDAO`, `UserDAO`)
* **Model**: egyszerű POJO-k (`Feed`, `Article`, `User`, `UserArticle`)

---

### 4. Tervezési minták

#### 4.1 Strategy

* `FilterStrategy<T>` + `TitleFilter`, `KeywordFilter`, `DateFilter` — runtime szűrés bővíthetően (OCP)

#### 4.2 Decorator

* `ArticleComponent` + `BasicArticleComponent` + `CachedFavoriteDecorator` + `CachedReadDecorator` — státusz- és vizuális kiterjesztés (ISP kompromisszum)


#### 4.3 DAO

* `*DAO` osztályok — SQL elkülönítése, mockolhatóság, cserélhetőség (pl. REST-re)



---

### 5. Aszinkron feldolgozás és UI

* **JavaFX Task** a feed-ek letöltéséhez (`loadArticles` háttérszálon)
* **Platform.runLater** hibakezeléskor
* **Cache + diff**: csak az új cikkek mentése, státuszok egy lekérdezéssel (statusMap)

---

### 6. Cross-cutting aspektusok

* **Loggolás**: SLF4J + Logback (`LogUtil`) minden rétegben, `INFO`/`ERROR`
* **Unit-tesztek**: JUnit DAO-n, Service-en (mockolt DAO)
* **CI/CD & repo**: GitHub main védett, feature-ágak, konvenciós commit
* **Konfiguráció**: `application.properties`/`db.properties` (DB URL, hitelesítés, intervallum)

---

### 7. Hibakezelés és UX

* **Hálózati hiba** → JavaFX `Alert` (`setOnFailed`)
* **Űrlapvalidáció** a bejelentkezésnél/registrációnál (üres mezők, jelszó hossza)
* **UI visszajelzés**: gombok letiltása (`btnApply.setDisable`) betöltés alatt

---

### 8. Biztonság

* **Jelszóhash**: BCrypt (`UserDAO`)
* **SQL injection elleni védelem**: PreparedStatement minden DAO-ban
* **Session-kezelés**: singleton `Session.getCurrentUser()`

---

### 9. Deployment és üzemeltetés

* **Függőségek**: Maven (JavaFX, ROME, MariaDB driver, JUnit)
* **Adatbázis**: MariaDB, script az inicializáláshoz (táblák, indexek)
* **Logback konfiguráció**: roll-over, szintek, fájlba írás
* **JAR csomag**: JavaFX modulokkal, `App.java` főosztállyal

---

### 10. Fő komponensek és felelősségek

| Komponens                                                                       | Felelősség                                                              |
| ------------------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| **App.java**                                                                    | Alkalmazás indítása, JavaFX init, `login.fxml` betöltése                |
| **MainController.java**                                                         | Navigáció (Feed / Article nézet)                                        |
| **FeedController.java**                                                         | Feed CRUD, UI-dialogok (`Dialog<Feed>`)                                 |
| **ArticleController.java**                                                      | Cikklista, diff + adatbázis-mentés, dekorátorok, szűrés, státusz-commit |
| **FeedService.java**                                                            | RSS letöltés, DAO-hívások, stratégiák alkalmazása                       |
| **ArticleService.java**                                                         | DAO-hívások kedvenc/olvasott státuszra, dekorátorok összeállítása       |
| **UserDAO**, **FeedDAO**, **ArticleDAO**, **UserArticleDAO**                    | JDBC CRUD műveletek, PreparedStatement, transzakciókezelés              |
| **RssParser.java**                                                              | Adapter: ROME → `Article` POJO                                          |
| **FilterStrategy**                                                              | Szűrési logika interfésze (Strategy minta)                              |
| **BasicArticleComponent**, **CachedFavoriteDecorator**, **CachedReadDecorator** | Decorator lánc a státusz és vizuális kiterjesztéshez                    |

---

### 11. Megjegyzések

* **SOLID**: SRP minden osztálynál, OCP a stratégiáknál és dekorátoroknál, ISP részben, DIP refaktorálható
* **Tesztelhetőség & CI**: mockolt DAO, JUnit, GitHub Actions (opcionális)
* **Bővíthetőség**: új filter, dekorátor, DAO-implementáció minimális módosítással beépíthető



