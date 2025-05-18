-- --------------------------------------------------------
-- Gazdagép:                     192.168.2.233
-- Szerver verzió:               10.5.19-MariaDB - MariaDB Server
-- Szerver OS:                   Linux
-- HeidiSQL Verzió:              12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Adatbázis struktúra mentése a EKKE_ProgTekBejadando.
CREATE DATABASE IF NOT EXISTS `EKKE_ProgTekBejadando` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin */;
USE `EKKE_ProgTekBejadando`;

-- Struktúra mentése tábla EKKE_ProgTekBejadando. Article
CREATE TABLE IF NOT EXISTS `Article` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `feed_url` varchar(512) NOT NULL DEFAULT '0',
  `title` varchar(512) NOT NULL DEFAULT '0',
  `link` varchar(1024) NOT NULL DEFAULT '0',
  `publication_date` datetime NOT NULL,
  `content` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_Article_Feed` (`feed_url`),
  CONSTRAINT `FK_Article_Feed` FOREIGN KEY (`feed_url`) REFERENCES `Feed` (`url`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Tábla adatainak mentése EKKE_ProgTekBejadando.Article: ~21 rows (hozzávetőleg)
REPLACE INTO `Article` (`id`, `feed_url`, `title`, `link`, `publication_date`, `content`) VALUES
	(41, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Switch 2-re tarthat a Red Dead Redemption 2?', 'https://prohardver.hu/hir/switch_2-re_tarthat_a_red_dead_redemption_2.html', '2025-05-18 11:30:00', 'A szóbeszédek szerint a Rockstar nextgen frissítéssel készül a játékhoz PS5-re és Xbox Series gépekhez.'),
	(42, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Elden Ring Nightreign - Bemutatkozik a Guardian', 'https://prohardver.hu/hir/elden_ring_nightreign_bemutatkozik_a_guardian.html', '2025-05-18 10:30:00', 'A kooperatív mellékág premierje kevesebb mint két hét múlva várható.'),
	(43, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Dune Awakening - Középpontban a The Trooper', 'https://prohardver.hu/hir/dune_awakening_kozeppontban_a_the_trooper.html', '2025-05-17 12:30:00', 'A PC-s teljes kiadásra már nem kell sokat várni, jövő hónapban debütál a túlélőjáték.'),
	(44, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Befellegzett a Call of Duty Warzone Mobile-nak', 'https://prohardver.hu/hir/befellegzett_a_call_of_duty_warzone_mobile-nak.html', '2025-05-17 11:30:00', 'A játék nem kap több tartalmi támogatást, de a szervereket egyelőre nem lövik le.'),
	(45, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'MindsEye - Érkezett két rövid játékmenet részlet', 'https://prohardver.hu/hir/mindseye_erkezett_ket_rovid_jatekmenet_reszlet.html', '2025-05-17 10:30:00', 'Ha minden a tervek szerint alakul, jövő hónapban befut a teljes kiadás.'),
	(46, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Elég kompakt lett az MSI legfrissebb, Blackwell architektúrás VGA-ja', 'https://prohardver.hu/hir/msi_geforce_rtx_5060_ti_16g_inspire_2x_oc_vga.html', '2025-05-16 19:03:00', 'A szóban forgó GeForce RTX 5060 Ti nem csupán letisztult vonalvezetést kapott, de környezetünk fényszennyezését sem kívánja növelni.'),
	(47, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Dual Mode-os, Mini LED-es IPS monitor jött az MSI műhelyéből', 'https://prohardver.hu/hir/msi_mpg_274urdfw_e16m_dual_mode-os_monitor.html', '2025-05-16 17:56:00', 'Az először még januárban megvillantott, KVM switch-es modell dúskál az AI funkciókban, és még egyedi hangulatfények is vannak rajta.'),
	(48, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Felújította prémium rendszerchipjét a Qualcomm', 'https://prohardver.hu/hir/qualcomm_snapdragon_7_gen_4_premium_soc.html', '2025-05-16 16:36:00', 'A Snapdragon 7 Gen 4 az AI-ra fókuszál, de a processzormagok is frissültek.'),
	(49, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'A kedvezőbb eligazodás érdekében kiegészíti márkajelzéseit az ARM', 'https://prohardver.hu/hir/arm_kedvezobb_eligazodas_kiegeszit_markajelzes.html', '2025-05-16 15:16:00', 'A legfontosabb kategóriák külön jelölést kapnak, így a felhasználók tudják majd, hogy hova szánt fejlesztésről van szó.'),
	(50, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Ingyenes az Epic Store-ban a Dead Island 2 (PC)', 'https://prohardver.hu/hir/ingyenes_az_epic_store-ban_a_dead_island_2_pc.html', '2025-05-16 15:15:00', 'Extraként a Happy Game-et is bezsákolhatják az érdeklődők.'),
	(51, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Kiszáll az optikai meghajtók piacáról a Pioneer', 'https://prohardver.hu/hir/pioneer_kiszall_optikai_meghajto_piac.html', '2025-05-16 13:56:00', 'Ez immáron hivatalos, a cég nem gyárt több Blu-ray-írót és -olvasót.'),
	(52, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Jövő hónapban jön a nyári The Future Games Show', 'https://prohardver.hu/hir/jovo_honapban_jon_a_nyari_the_future_games_show.html', '2025-05-16 13:15:00', 'A műsor házigazdái Laura Bailey és Matthew Mercer lesznek.'),
	(53, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Idén nyáron visszatér az HBO Max', 'https://prohardver.hu/hir/visszater_az_hbo_max.html', '2025-05-16 12:46:00', 'A Warner Bros Discovery visszahozza az HBO Max márkát a globális terjeszkedés érdekében.'),
	(54, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'A kereskedőknek elege van a Visa és a Mastercard díjaiból', 'https://prohardver.hu/hir/visa_mastercard_dijak_kereskedoknek_elege_van.html', '2025-05-16 11:36:00', 'Az EB mielőbbi fellépését követelik a Visa és a Mastercard magasnak mondott díjai miatt.'),
	(55, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Stellar Blade - Hivatalosan is bejelentették a PC-s kiadás premier dátumát', 'https://prohardver.hu/hir/stellar_blade_hivatalosan_is_bejelentettek_a_pc-s.html', '2025-05-16 11:10:00', 'A leleplezés mellé friss trailer is érkezett, valamint megkaptuk a gépigényt is.'),
	(56, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Ömlenek a felhasználók a reklámos Netflixre', 'https://prohardver.hu/hir/netflix_reklamos_csomag_omlenek_a_felhasznalok.html', '2025-05-16 10:16:00', 'A reklámokkal támogatott szolgáltatásnak a Netflix szerint már 94 millió felhasználója van.'),
	(57, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Free Play Days 20. hét - Train Sim World 5: Thomas & Friends, House Flipper 2', 'https://prohardver.hu/hir/free_play_days_20_het_train_sim_world_5_thomas_fri.html', '2025-05-16 09:30:00', 'Extraként a Stranger of Paradise Final Fantasy Origin is kipróbálható az elkövetkező napokban.'),
	(58, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'A Sony még nem döntötte el, hogy miképpen reagálnak az USA vámpolitikájára', 'https://prohardver.hu/hir/sony_playstation_5_vam_usa_reakcio.html', '2025-05-15 18:35:00', 'A veszteségek amerikai fogyasztókra való áthárítása mellett a PlayStation 5 egyes gyártási folyamatainak USA-ba költöztetése is lehetséges.'),
	(59, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', '2027-re várja a bérgyártás fordulópontját az Intel', 'https://prohardver.hu/hir/intel_2027_varja_bergyartas_fordulopont.html', '2025-05-15 17:15:00', 'A vállalat szerint az Intel 14A node elérhetésőgére nullszaldós lehet az üzletág.'),
	(60, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Kiderült mit tud a Nintendo Switch 2 rendszerchipje', 'https://prohardver.hu/hir/nintendo_switch_2_soc_kiderult_mit_tud.html', '2025-05-15 16:05:00', 'A Digital Foundry utánajárt a konkért specifikációnak, így a még titkolt adatokat is lehet már tudni.'),
	(61, 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 'Plágiumvádak a Bungie ellen, ezúttal a Marathon érintett', 'https://prohardver.hu/hir/plagiumvadak_a_bungie_ellen_ezuttal_a_marathon_eri.html', '2025-05-18 12:30:00', 'A cég elismerte hogy hibázott, kíváncsian várjuk hogy módosítja mindez az új játékuk érkezésének terveit.');

-- Struktúra mentése tábla EKKE_ProgTekBejadando. Feed
CREATE TABLE IF NOT EXISTS `Feed` (
  `name` varchar(255) NOT NULL DEFAULT '0',
  `url` varchar(512) NOT NULL DEFAULT '0',
  `refresh_interval_min` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Tábla adatainak mentése EKKE_ProgTekBejadando.Feed: ~1 rows (hozzávetőleg)
REPLACE INTO `Feed` (`name`, `url`, `refresh_interval_min`) VALUES
	('Prohardver', 'https://prohardver.hu/hirfolyam/anyagok/rss.xml', 30);

-- Struktúra mentése tábla EKKE_ProgTekBejadando. User
CREATE TABLE IF NOT EXISTS `User` (
  `username` varchar(100) NOT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Tábla adatainak mentése EKKE_ProgTekBejadando.User: ~3 rows (hozzávetőleg)
REPLACE INTO `User` (`username`, `password_hash`, `email`, `created_at`) VALUES
	('asdf', '$2a$10$q8PMqpUQuMv.ImjrB.lrkOXRT1NEHMUQSLb7ze9Xc6Xe9B3p8TOd2', 'asdf@teszt.hu', '2025-05-09 11:11:45'),
	('atix', '$2a$10$.QVlViUmOYgmaxBQNq6JlO5JOi3Wv/IYa5u5wAIoRMsyi0mtnbTlC', 'atix@atix.hu', '2025-05-18 11:08:27'),
	('peti', '$2a$10$bOuHaQoryq9t6XzjaT1HSudBF0rkhqKsj5wDM.dyloyUjWi/m5yXi', 'peti@f.hu', '2025-05-17 13:39:16');

-- Struktúra mentése tábla EKKE_ProgTekBejadando. UserArticle
CREATE TABLE IF NOT EXISTS `UserArticle` (
  `user` varchar(100) NOT NULL DEFAULT '',
  `article_id` int(11) NOT NULL,
  `is_favorite` tinyint(1) DEFAULT 0,
  `is_read` tinyint(1) DEFAULT 0,
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  KEY `FK_UserArticle_Article` (`article_id`),
  KEY `FK_UserArticle_User` (`user`),
  CONSTRAINT `FK_UserArticle_Article` FOREIGN KEY (`article_id`) REFERENCES `Article` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_UserArticle_User` FOREIGN KEY (`user`) REFERENCES `User` (`username`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- Tábla adatainak mentése EKKE_ProgTekBejadando.UserArticle: ~6 rows (hozzávetőleg)
REPLACE INTO `UserArticle` (`user`, `article_id`, `is_favorite`, `is_read`, `updated_at`) VALUES
	('peti', 42, 1, 0, '2025-05-18 16:52:30'),
	('peti', 47, 1, 0, '2025-05-18 16:52:41'),
	('atix', 41, 1, 1, '2025-05-18 17:06:21'),
	('atix', 42, 1, 1, '2025-05-18 17:06:23'),
	('atix', 43, 0, 1, '2025-05-18 16:57:40'),
	('atix', 44, 1, 0, '2025-05-18 16:57:42');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
