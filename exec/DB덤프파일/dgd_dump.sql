CREATE DATABASE  IF NOT EXISTS `ding_ga_ding` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `ding_ga_ding`;
-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ding_ga_ding
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attempt`
--

DROP TABLE IF EXISTS `attempt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attempt` (
  `beat_score` int DEFAULT NULL,
  `tone_score` int DEFAULT NULL,
  `total_score` int NOT NULL,
  `tune_score` int DEFAULT NULL,
  `attempt_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `rank_matching_id` bigint DEFAULT NULL,
  `song_by_instrument_id` bigint NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `game_type` enum('PRACTICE','RANK') NOT NULL,
  `rank_type` enum('CHALLENGE','DEFENCE','FIRST') DEFAULT NULL,
  `status` enum('BEFORE_ANALYZE','FAIL','SUCCESS') NOT NULL,
  PRIMARY KEY (`attempt_id`),
  KEY `FKtpsp1q59t52dlsopxwnhxipb9` (`rank_matching_id`),
  KEY `FK58a1msmwb57qb9f6w6gttciy5` (`song_by_instrument_id`),
  CONSTRAINT `FK58a1msmwb57qb9f6w6gttciy5` FOREIGN KEY (`song_by_instrument_id`) REFERENCES `song_by_instrument` (`song_by_instrument_id`),
  CONSTRAINT `FKtpsp1q59t52dlsopxwnhxipb9` FOREIGN KEY (`rank_matching_id`) REFERENCES `rank_matching` (`rank_matching_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attempt`
--

LOCK TABLES `attempt` WRITE;
/*!40000 ALTER TABLE `attempt` DISABLE KEYS */;
/*!40000 ALTER TABLE `attempt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `band`
--

DROP TABLE IF EXISTS `band`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `band` (
  `job_opening` bit(1) NOT NULL,
  `max_size` int DEFAULT NULL,
  `band_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `updated_at` datetime(6) DEFAULT NULL,
  `band_master_id` binary(16) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `profile_url` varchar(255) DEFAULT NULL,
  `sigun` varchar(255) NOT NULL,
  `tags` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`band_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `band`
--

LOCK TABLES `band` WRITE;
/*!40000 ALTER TABLE `band` DISABLE KEYS */;
INSERT INTO `band` VALUES (_binary '',5,1,'2025-04-01 10:00:00.000000','2025-04-01 10:00:00.000000',_binary '��g�Z�\�B�\0','다양한 장르의 음악을 연주하는 밴드입니다','소리꾼들','https://plus.unsplash.com/premium_photo-1682855223699-edb85ffa57b3?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8JUVCJUIwJUI0JUVCJTkzJTlDfGVufDB8fDB8fHww','서울','락밴드,인디,커버'),(_binary '',4,2,'2025-04-01 10:10:00.000000','2025-04-01 10:10:00.000000',_binary '��i�Z�\�B�\0','재즈와 팝 음악을 혼합한 창작곡을 연주합니다','블루노트','https://plus.unsplash.com/premium_photo-1682855221220-4dcc4ac702c7?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OXx8JUVCJUIwJUI0JUVCJTkzJTlDfGVufDB8fDB8fHww','부산','재즈,팝,창작곡'),(_binary '\0',6,3,'2025-04-01 10:20:00.000000','2025-04-01 10:20:00.000000',_binary '��j�Z�\�B�\0','록과 메탈 음악을 주로 연주하는 열정적인 밴드','메탈헤드','https://images.unsplash.com/photo-1507808973436-a4ed7b5e87c9?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjN8fCVFQiVCMCVCNCVFQiU5MyU5Q3xlbnwwfHwwfHx8MA%3D%3D','대구','록,메탈,헤비'),(_binary '',5,4,'2025-04-01 10:30:00.000000','2025-04-01 10:30:00.000000',_binary '��k4Z�\�B�\0','감성적인 어쿠스틱 사운드를 연주하는 밴드','어쿠스틱 소울','https://images.unsplash.com/photo-1617136785693-54b279bddfeb?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NDN8fCVFQiVCMCVCNCVFQiU5MyU5Q3xlbnwwfHwwfHx8MA%3D%3D','인천','어쿠스틱,발라드,감성'),(_binary '',7,5,'2025-04-01 10:40:00.000000','2025-04-01 10:40:00.000000',_binary '��kdZ�\�B�\0','한국 전통 음악과 현대 음악을 결합한 퓨전 밴드','국악 프로젝트','https://images.unsplash.com/photo-1651694721718-7a72df522ae3?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTE4fHwlRUIlQjAlQjQlRUIlOTMlOUN8ZW58MHx8MHx8fDA%3D','광주','퓨전,국악,전통'),(_binary '\0',5,6,'2025-04-01 10:50:00.000000','2025-04-01 10:50:00.000000',_binary '��k�Z�\�B�\0','클래식과 팝을 결합한 독특한 사운드의 밴드','클래식 팝스','https://images.unsplash.com/photo-1632054543980-3b8b44256f27?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTI3fHwlRUIlQjAlQjQlRUIlOTMlOUN8ZW58MHx8MHx8fDA%3D','울산','클래식,팝,오케스트라'),(_binary '',5,7,'2025-04-01 11:00:00.000000','2025-04-01 11:00:00.000000',_binary '��Q_Z�\�B�\0','일렉트로닉과 EDM을 기반으로 하는 밴드입니다','일렉트로닉 웨이브','https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8ZWxlY3Ryb25pYyUyMG11c2ljfGVufDB8fDB8fHww','서울','EDM,일렉트로닉,신스팝'),(_binary '',6,8,'2025-04-01 11:10:00.000000','2025-04-01 11:10:00.000000',_binary '��TZ�\�B�\0','힙합과 R&B를 결합한 퓨전 밴드입니다','힙합 퓨전','https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8aGlwJTIwaG9wfGVufDB8fDB8fHww','경기','힙합,R&B,퓨전'),(_binary '\0',4,9,'2025-04-01 11:20:00.000000','2025-04-01 11:20:00.000000',_binary '��UgZ�\�B�\0','클래식 블루스와 모던 블루스를 연주하는 밴드','블루스 트레인','https://images.unsplash.com/photo-1579797990768-555ac3a4c7e5?q=80&w=3174&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D','부산','블루스,소울,재즈블루스'),(_binary '',5,10,'2025-04-01 11:30:00.000000','2025-04-01 11:30:00.000000',_binary '��U�Z�\�B�\0','포크와 컨트리 음악을 연주하는 어쿠스틱 밴드','포크 하모니','https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Zm9sayUyMG11c2ljfGVufDB8fDB8fHww','대전','포크,컨트리,어쿠스틱'),(_binary '',6,11,'2025-04-01 11:40:00.000000','2025-04-01 11:40:00.000000',_binary '��U\�Z�\�B�\0','레게와 스카 음악을 연주하는 밴드','레게 비트','https://images.unsplash.com/photo-1561839561-b13bcfe95249?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8cmVnZ2FlfGVufDB8fDB8fHww','광주','레게,스카,라틴'),(_binary '\0',4,12,'2025-04-01 11:50:00.000000','2025-04-01 11:50:00.000000',_binary '��VZ�\�B�\0','펑크와 하드코어를 연주하는 에너지 넘치는 밴드','펑크 레볼루션','https://images.unsplash.com/photo-1598387993248-0addc49abf0d?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTF8fHB1bmslMjByb2NrfGVufDB8fDB8fHww','대구','펑크,하드코어,얼터너티브');
/*!40000 ALTER TABLE `band` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `band_member`
--

DROP TABLE IF EXISTS `band_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `band_member` (
  `band_id` bigint NOT NULL,
  `band_member_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `updated_at` datetime(6) DEFAULT NULL,
  `member_id` binary(16) NOT NULL,
  `instrument` enum('BASS','DRUM','GUITAR','VOCAL') DEFAULT NULL,
  PRIMARY KEY (`band_member_id`),
  KEY `FK260yqm4dj85eh8d8xsy0pteas` (`band_id`),
  KEY `FKtdge8j6t2m9i5h8rcjk3ju0vq` (`member_id`),
  CONSTRAINT `FK260yqm4dj85eh8d8xsy0pteas` FOREIGN KEY (`band_id`) REFERENCES `band` (`band_id`),
  CONSTRAINT `FKtdge8j6t2m9i5h8rcjk3ju0vq` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `band_member`
--

LOCK TABLES `band_member` WRITE;
/*!40000 ALTER TABLE `band_member` DISABLE KEYS */;
INSERT INTO `band_member` VALUES (1,1,'2025-04-01 10:05:00.000000','2025-04-01 10:05:00.000000',_binary '��g�Z�\�B�\0','GUITAR'),(1,2,'2025-04-01 10:08:00.000000','2025-04-01 10:08:00.000000',_binary '��i�Z�\�B�\0','DRUM'),(2,3,'2025-04-01 10:15:00.000000','2025-04-01 10:15:00.000000',_binary '��i�Z�\�B�\0','DRUM'),(2,4,'2025-04-01 10:18:00.000000','2025-04-01 10:18:00.000000',_binary '��k\�Z�\�B�\0','VOCAL'),(3,5,'2025-04-01 10:25:00.000000','2025-04-01 10:25:00.000000',_binary '��j�Z�\�B�\0','BASS'),(3,6,'2025-04-01 10:28:00.000000','2025-04-01 10:28:00.000000',_binary '��k�Z�\�B�\0','GUITAR'),(3,7,'2025-04-01 10:31:00.000000','2025-04-01 10:31:00.000000',_binary '��lZ�\�B�\0','VOCAL'),(4,8,'2025-04-01 10:35:00.000000','2025-04-01 10:35:00.000000',_binary '��k4Z�\�B�\0','VOCAL'),(4,9,'2025-04-01 10:38:00.000000','2025-04-01 10:38:00.000000',_binary '��k\�Z�\�B�\0','BASS'),(5,10,'2025-04-01 10:45:00.000000','2025-04-01 10:45:00.000000',_binary '��kdZ�\�B�\0','VOCAL'),(7,11,'2025-04-01 11:05:00.000000','2025-04-01 11:05:00.000000',_binary '��Q_Z�\�B�\0','GUITAR'),(7,12,'2025-04-01 11:06:00.000000','2025-04-01 11:06:00.000000',_binary '��i�Z�\�B�\0','DRUM'),(7,13,'2025-04-01 11:07:00.000000','2025-04-01 11:07:00.000000',_binary '��kdZ�\�B�\0','VOCAL'),(8,14,'2025-04-01 11:15:00.000000','2025-04-01 11:15:00.000000',_binary '��TZ�\�B�\0','VOCAL'),(8,15,'2025-04-01 11:16:00.000000','2025-04-01 11:16:00.000000',_binary '��k\�Z�\�B�\0','BASS'),(8,16,'2025-04-01 11:17:00.000000','2025-04-01 11:17:00.000000',_binary '��l�Z�\�B�\0','DRUM'),(9,17,'2025-04-01 11:25:00.000000','2025-04-01 11:25:00.000000',_binary '��UgZ�\�B�\0','GUITAR'),(9,18,'2025-04-01 11:26:00.000000','2025-04-01 11:26:00.000000',_binary '��j�Z�\�B�\0','BASS'),(9,19,'2025-04-01 11:27:00.000000','2025-04-01 11:27:00.000000',_binary '��k\�Z�\�B�\0','VOCAL'),(10,20,'2025-04-01 11:35:00.000000','2025-04-01 11:35:00.000000',_binary '��U�Z�\�B�\0','VOCAL'),(10,21,'2025-04-01 11:36:00.000000','2025-04-01 11:36:00.000000',_binary '��g�Z�\�B�\0','GUITAR'),(11,22,'2025-04-01 11:45:00.000000','2025-04-01 11:45:00.000000',_binary '��U\�Z�\�B�\0','DRUM'),(11,23,'2025-04-01 11:46:00.000000','2025-04-01 11:46:00.000000',_binary '��k4Z�\�B�\0','VOCAL'),(11,24,'2025-04-01 11:47:00.000000','2025-04-01 11:47:00.000000',_binary '��lSZ�\�B�\0','BASS'),(12,25,'2025-04-01 11:55:00.000000','2025-04-01 11:55:00.000000',_binary '��VZ�\�B�\0','GUITAR'),(12,26,'2025-04-01 11:56:00.000000','2025-04-01 11:56:00.000000',_binary '��k�Z�\�B�\0','VOCAL'),(12,27,'2025-04-01 11:57:00.000000','2025-04-01 11:57:00.000000',_binary '��lZ�\�B�\0','BASS');
/*!40000 ALTER TABLE `band_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `band_recruitment`
--

DROP TABLE IF EXISTS `band_recruitment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `band_recruitment` (
  `audition_date` datetime(6) NOT NULL,
  `audition_song_id` bigint NOT NULL,
  `band_id` bigint NOT NULL,
  `band_recruitment_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `updated_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `status` enum('COMPLETED','READY','RECRUITING') NOT NULL,
  PRIMARY KEY (`band_recruitment_id`),
  KEY `FK620k63n4ihc8bw0srx8mr3w4k` (`audition_song_id`),
  KEY `FKalb14r1d8hyq2djkj5hsf8r3o` (`band_id`),
  CONSTRAINT `FK620k63n4ihc8bw0srx8mr3w4k` FOREIGN KEY (`audition_song_id`) REFERENCES `song` (`song_id`),
  CONSTRAINT `FKalb14r1d8hyq2djkj5hsf8r3o` FOREIGN KEY (`band_id`) REFERENCES `band` (`band_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `band_recruitment`
--

LOCK TABLES `band_recruitment` WRITE;
/*!40000 ALTER TABLE `band_recruitment` DISABLE KEYS */;
INSERT INTO `band_recruitment` VALUES ('2025-04-15 14:00:00.000000',1,1,1,'2025-04-02 11:00:00.000000','2025-04-02 11:00:00.000000','기타리스트를 모집합니다. 락 음악에 관심 있는 분이면 좋겠습니다.','소리꾼들 기타리스트 모집','RECRUITING'),('2025-04-16 15:00:00.000000',3,1,2,'2025-04-02 11:30:00.000000','2025-04-02 11:30:00.000000','베이시스트를 모집합니다. 경험자 우대합니다.','소리꾼들 베이시스트 모집','RECRUITING'),('2025-04-17 16:00:00.000000',5,2,3,'2025-04-02 12:00:00.000000','2025-04-02 12:00:00.000000','보컬을 모집합니다. 재즈와 팝 음악을 좋아하는 분이면 좋겠습니다.','블루노트 보컬 모집','RECRUITING'),('2025-04-18 17:00:00.000000',4,3,4,'2025-04-02 12:30:00.000000','2025-04-02 12:30:00.000000','드러머를 모집합니다. 헤비 메탈 경험이 있으면 좋습니다.','메탈헤드 드러머 모집','READY'),('2025-04-19 18:00:00.000000',2,4,5,'2025-04-02 13:00:00.000000','2025-04-02 13:00:00.000000','기타리스트와 보컬을 모집합니다. 어쿠스틱 음악에 관심 있는 분이면 좋겠습니다.','어쿠스틱 소울 멤버 모집','RECRUITING'),('2025-04-20 19:00:00.000000',6,5,6,'2025-04-02 13:30:00.000000','2025-04-02 13:30:00.000000','국악기 연주자를 모집합니다. 전통 악기 연주 가능자 우대합니다.','국악 프로젝트 단원 모집','COMPLETED');
/*!40000 ALTER TABLE `band_recruitment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `band_recruitment_applicant`
--

DROP TABLE IF EXISTS `band_recruitment_applicant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `band_recruitment_applicant` (
  `apply_date` datetime(6) NOT NULL,
  `band_recruitment_applicant_id` bigint NOT NULL AUTO_INCREMENT,
  `band_recruitment_instruments_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `updated_at` datetime(6) DEFAULT NULL,
  `applicant_id` binary(16) NOT NULL,
  `status` enum('ACCEPTED','PENDING','REJECTED') NOT NULL,
  PRIMARY KEY (`band_recruitment_applicant_id`),
  KEY `FKsq1v8khgfn2yr4vnuj8gqmrua` (`applicant_id`),
  KEY `FK1mk4yfk3cmxqwj25utkv7bh7i` (`band_recruitment_instruments_id`),
  CONSTRAINT `FK1mk4yfk3cmxqwj25utkv7bh7i` FOREIGN KEY (`band_recruitment_instruments_id`) REFERENCES `band_recruitment_instrument` (`band_recruitment_instruments_id`),
  CONSTRAINT `FKsq1v8khgfn2yr4vnuj8gqmrua` FOREIGN KEY (`applicant_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `band_recruitment_applicant`
--

LOCK TABLES `band_recruitment_applicant` WRITE;
/*!40000 ALTER TABLE `band_recruitment_applicant` DISABLE KEYS */;
/*!40000 ALTER TABLE `band_recruitment_applicant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `band_recruitment_instrument`
--

DROP TABLE IF EXISTS `band_recruitment_instrument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `band_recruitment_instrument` (
  `max_size` int NOT NULL,
  `band_recruitment_id` bigint NOT NULL,
  `band_recruitment_instruments_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `updated_at` datetime(6) DEFAULT NULL,
  `instrument` enum('BASS','DRUM','GUITAR','VOCAL') NOT NULL,
  `required_tier` enum('BRONZE','DIAMOND','GOLD','IRON','PLATINUM','SILVER','UNRANKED') NOT NULL,
  PRIMARY KEY (`band_recruitment_instruments_id`),
  KEY `FKdiffi9n0kvc1bfqf01acy6an8` (`band_recruitment_id`),
  CONSTRAINT `FKdiffi9n0kvc1bfqf01acy6an8` FOREIGN KEY (`band_recruitment_id`) REFERENCES `band_recruitment` (`band_recruitment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `band_recruitment_instrument`
--

LOCK TABLES `band_recruitment_instrument` WRITE;
/*!40000 ALTER TABLE `band_recruitment_instrument` DISABLE KEYS */;
INSERT INTO `band_recruitment_instrument` VALUES (1,1,1,'2025-04-02 11:05:00.000000','2025-04-02 11:05:00.000000','GUITAR','GOLD'),(1,2,2,'2025-04-02 11:35:00.000000','2025-04-02 11:35:00.000000','BASS','SILVER'),(1,3,3,'2025-04-02 12:05:00.000000','2025-04-02 12:05:00.000000','VOCAL','PLATINUM'),(1,4,4,'2025-04-02 12:35:00.000000','2025-04-02 12:35:00.000000','DRUM','GOLD'),(1,5,5,'2025-04-02 13:05:00.000000','2025-04-02 13:05:00.000000','GUITAR','SILVER'),(1,5,6,'2025-04-02 13:06:00.000000','2025-04-02 13:06:00.000000','VOCAL','GOLD'),(2,6,7,'2025-04-02 13:35:00.000000','2025-04-02 13:35:00.000000','GUITAR','BRONZE'),(1,6,8,'2025-04-02 13:36:00.000000','2025-04-02 13:36:00.000000','DRUM','SILVER');
/*!40000 ALTER TABLE `band_recruitment_instrument` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `band_recruitment_song`
--

DROP TABLE IF EXISTS `band_recruitment_song`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `band_recruitment_song` (
  `band_recruitment_id` bigint DEFAULT NULL,
  `band_recruitment_song_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `song_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`band_recruitment_song_id`),
  KEY `FKh4ugju4jsre0kq95x59pxmoap` (`band_recruitment_id`),
  KEY `FKsamhdbcuk1155pwl5qhkrt8bh` (`song_id`),
  CONSTRAINT `FKh4ugju4jsre0kq95x59pxmoap` FOREIGN KEY (`band_recruitment_id`) REFERENCES `band_recruitment` (`band_recruitment_id`),
  CONSTRAINT `FKsamhdbcuk1155pwl5qhkrt8bh` FOREIGN KEY (`song_id`) REFERENCES `song` (`song_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `band_recruitment_song`
--

LOCK TABLES `band_recruitment_song` WRITE;
/*!40000 ALTER TABLE `band_recruitment_song` DISABLE KEYS */;
/*!40000 ALTER TABLE `band_recruitment_song` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contact` (
  `band_id` bigint NOT NULL,
  `contact_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `updated_at` datetime(6) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  `sns` enum('CUSTOM','FACEBOOK','GITHUB','INSTAGRAM','MATTERMOST','X','YOUTUBE') NOT NULL,
  PRIMARY KEY (`contact_id`),
  KEY `FKiks5jdmfkgycu0to045mnypd` (`band_id`),
  CONSTRAINT `FKiks5jdmfkgycu0to045mnypd` FOREIGN KEY (`band_id`) REFERENCES `band` (`band_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact`
--

LOCK TABLES `contact` WRITE;
/*!40000 ALTER TABLE `contact` DISABLE KEYS */;
INSERT INTO `contact` VALUES (1,1,'2025-04-01 10:05:00.000000','2025-04-01 10:05:00.000000','소리꾼들 인스타그램','https://instagram.com/sorikkun','INSTAGRAM'),(1,2,'2025-04-01 10:06:00.000000','2025-04-01 10:06:00.000000','소리꾼들 유튜브','https://youtube.com/@sorikkun','YOUTUBE'),(2,3,'2025-04-01 10:15:00.000000','2025-04-01 10:15:00.000000','블루노트 인스타그램','https://instagram.com/bluenote','INSTAGRAM'),(2,4,'2025-04-01 10:16:00.000000','2025-04-01 10:16:00.000000','블루노트 X(트위터)','https://x.com/bluenote','X'),(3,5,'2025-04-01 10:25:00.000000','2025-04-01 10:25:00.000000','메탈헤드 인스타그램','https://instagram.com/metalhead','INSTAGRAM'),(3,6,'2025-04-01 10:26:00.000000','2025-04-01 10:26:00.000000','메탈헤드 유튜브','https://youtube.com/@metalhead','YOUTUBE'),(3,7,'2025-04-01 10:27:00.000000','2025-04-01 10:27:00.000000','메탈헤드 웹사이트','https://metalhead.kr','CUSTOM'),(4,8,'2025-04-01 10:35:00.000000','2025-04-01 10:35:00.000000','어쿠스틱 소울 인스타그램','https://instagram.com/acousticsoul','INSTAGRAM'),(5,9,'2025-04-01 10:45:00.000000','2025-04-01 10:45:00.000000','국악 프로젝트 유튜브','https://youtube.com/@kpproject','YOUTUBE'),(5,10,'2025-04-01 10:46:00.000000','2025-04-01 10:46:00.000000','국악 프로젝트 페이스북','https://facebook.com/kpproject','FACEBOOK'),(7,11,'2025-04-01 11:00:00.000000','2025-04-01 11:00:00.000000','일렉트로닉 웨이브 인스타그램','https://instagram.com/electronic_wave','INSTAGRAM'),(7,12,'2025-04-01 11:01:00.000000','2025-04-01 11:01:00.000000','일렉트로닉 웨이브 사운드클라우드','https://soundcloud.com/electronic_wave','CUSTOM'),(8,13,'2025-04-01 11:10:00.000000','2025-04-01 11:10:00.000000','힙합 퓨전 인스타그램','https://instagram.com/hiphop_fusion','INSTAGRAM'),(8,14,'2025-04-01 11:11:00.000000','2025-04-01 11:11:00.000000','힙합 퓨전 유튜브','https://youtube.com/@hiphop_fusion','YOUTUBE'),(9,15,'2025-04-01 11:20:00.000000','2025-04-01 11:20:00.000000','블루스 트레인 인스타그램','https://instagram.com/blues_train','INSTAGRAM'),(9,16,'2025-04-01 11:21:00.000000','2025-04-01 11:21:00.000000','블루스 트레인 페이스북','https://facebook.com/blues_train','FACEBOOK'),(10,17,'2025-04-01 11:30:00.000000','2025-04-01 11:30:00.000000','포크 하모니 인스타그램','https://instagram.com/folk_harmony','INSTAGRAM'),(10,18,'2025-04-01 11:31:00.000000','2025-04-01 11:31:00.000000','포크 하모니 X(트위터)','https://x.com/folk_harmony','X'),(11,19,'2025-04-01 11:40:00.000000','2025-04-01 11:40:00.000000','레게 비트 인스타그램','https://instagram.com/reggae_beat','INSTAGRAM'),(11,20,'2025-04-01 11:41:00.000000','2025-04-01 11:41:00.000000','레게 비트 유튜브','https://youtube.com/@reggae_beat','YOUTUBE'),(12,21,'2025-04-01 11:50:00.000000','2025-04-01 11:50:00.000000','펑크 레볼루션 인스타그램','https://instagram.com/punk_revolution','INSTAGRAM'),(12,22,'2025-04-01 11:51:00.000000','2025-04-01 11:51:00.000000','펑크 레볼루션 유튜브','https://youtube.com/@punk_revolution','YOUTUBE'),(12,23,'2025-04-01 11:52:00.000000','2025-04-01 11:52:00.000000','펑크 레볼루션 웹사이트','https://punk-revolution.kr','CUSTOM');
/*!40000 ALTER TABLE `contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follow`
--

DROP TABLE IF EXISTS `follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follow` (
  `followed_eachother` bit(1) NOT NULL COMMENT '쿼리로 처리하는게 나을지, 저장해두는게 나을지?',
  `follow_id` bigint NOT NULL AUTO_INCREMENT,
  `followed_user_id` binary(16) NOT NULL,
  `following_user_id` binary(16) NOT NULL,
  PRIMARY KEY (`follow_id`),
  KEY `FKgscm534p4p56w13wa3hbjcp9o` (`followed_user_id`),
  KEY `FK12l2s4u9l76rs4qy8gvkbh45i` (`following_user_id`),
  CONSTRAINT `FK12l2s4u9l76rs4qy8gvkbh45i` FOREIGN KEY (`following_user_id`) REFERENCES `member` (`member_id`),
  CONSTRAINT `FKgscm534p4p56w13wa3hbjcp9o` FOREIGN KEY (`followed_user_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follow`
--

LOCK TABLES `follow` WRITE;
/*!40000 ALTER TABLE `follow` DISABLE KEYS */;
/*!40000 ALTER TABLE `follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `livehouses`
--

DROP TABLE IF EXISTS `livehouses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `livehouses` (
  `max_participants` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `livehouse_id` bigint NOT NULL,
  `host_id` binary(16) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `host_nickname` varchar(255) NOT NULL,
  `session_id` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `status` enum('ACTIVE','CLOSED') NOT NULL,
  PRIMARY KEY (`livehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `livehouses`
--

LOCK TABLES `livehouses` WRITE;
/*!40000 ALTER TABLE `livehouses` DISABLE KEYS */;
/*!40000 ALTER TABLE `livehouses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `livehouses_seq`
--

DROP TABLE IF EXISTS `livehouses_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `livehouses_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `livehouses_seq`
--

LOCK TABLES `livehouses_seq` WRITE;
/*!40000 ALTER TABLE `livehouses_seq` DISABLE KEYS */;
INSERT INTO `livehouses_seq` VALUES (1);
/*!40000 ALTER TABLE `livehouses_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `favorite_band_id` bigint DEFAULT NULL COMMENT '즐겨찾기 된 밴드',
  `updated_at` datetime(6) DEFAULT NULL,
  `member_id` binary(16) NOT NULL,
  `nickname` varchar(255) NOT NULL COMMENT '사용자 닉네임 (자동 생성)',
  `profile_img_url` varchar(255) DEFAULT NULL COMMENT '프로필 이미지',
  `username` varchar(255) NOT NULL COMMENT '사용자 로그인 아이디',
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `UKgc3jmn7c2abyo3wf6syln5t2i` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
INSERT INTO `member` VALUES ('2025-04-01 09:00:00.000000',NULL,'2025-04-01 09:00:00.000000',_binary '��g�Z�\�B�\0','기타킹','https://storage.dinggading.com/profiles/avatar1.jpg','guitar_master@gmail.com'),('2025-04-01 09:05:00.000000',NULL,'2025-04-01 09:05:00.000000',_binary '��i�Z�\�B�\0','드럼왕','https://storage.dinggading.com/profiles/avatar2.jpg','drum_king@gmail.com'),('2025-04-01 09:10:00.000000',NULL,'2025-04-01 09:10:00.000000',_binary '��j�Z�\�B�\0','베이싱','https://storage.dinggading.com/profiles/avatar3.jpg','bass_pro@gmail.com'),('2025-04-01 09:15:00.000000',NULL,'2025-04-01 09:15:00.000000',_binary '��k4Z�\�B�\0','보컬여신','https://storage.dinggading.com/profiles/avatar4.jpg','vocal_queen@gmail.com'),('2025-04-01 09:20:00.000000',NULL,'2025-04-01 09:20:00.000000',_binary '��kdZ�\�B�\0','멜로디','https://storage.dinggading.com/profiles/avatar5.jpg','melody_singer@gmail.com'),('2025-04-01 09:25:00.000000',NULL,'2025-04-01 09:25:00.000000',_binary '��k�Z�\�B�\0','록스타','https://storage.dinggading.com/profiles/avatar6.jpg','rock_star@gmail.com'),('2025-04-01 09:30:00.000000',NULL,'2025-04-01 09:30:00.000000',_binary '��k\�Z�\�B�\0','음악대장','https://storage.dinggading.com/profiles/avatar7.jpg','music_captain@gmail.com'),('2025-04-01 09:35:00.000000',NULL,'2025-04-01 09:35:00.000000',_binary '��k\�Z�\�B�\0','재즈맨','https://storage.dinggading.com/profiles/avatar8.jpg','jazz_man@gmail.com'),('2025-04-01 09:40:00.000000',NULL,'2025-04-01 09:40:00.000000',_binary '��lZ�\�B�\0','팝스타','https://storage.dinggading.com/profiles/avatar9.jpg','pop_star@gmail.com'),('2025-04-01 09:45:00.000000',NULL,'2025-04-01 09:45:00.000000',_binary '��lSZ�\�B�\0','음악천재','https://storage.dinggading.com/profiles/avatar10.jpg','music_genius@gmail.com'),('2025-04-01 09:50:00.000000',NULL,'2025-04-01 09:50:00.000000',_binary '��l�Z�\�B�\0','무대여왕','https://storage.dinggading.com/profiles/avatar11.jpg','stage_queen@gmail.com'),('2025-04-01 09:55:00.000000',NULL,'2025-04-01 09:55:00.000000',_binary '��l�Z�\�B�\0','리듬킹','https://storage.dinggading.com/profiles/avatar12.jpg','rhythm_king@gmail.com'),('2025-04-01 10:00:00.000000',NULL,'2025-04-01 10:00:00.000000',_binary '��Q_Z�\�B�\0','일렉트로닉','https://storage.dinggading.com/profiles/avatar13.jpg','electronic_artist@gmail.com'),('2025-04-01 10:05:00.000000',NULL,'2025-04-01 10:05:00.000000',_binary '��TZ�\�B�\0','힙합비트','https://storage.dinggading.com/profiles/avatar14.jpg','hiphop_beats@gmail.com'),('2025-04-01 10:10:00.000000',NULL,'2025-04-01 10:10:00.000000',_binary '��UgZ�\�B�\0','블루스맨','https://storage.dinggading.com/profiles/avatar15.jpg','blues_man@gmail.com'),('2025-04-01 10:15:00.000000',NULL,'2025-04-01 10:15:00.000000',_binary '��U�Z�\�B�\0','포크소울','https://storage.dinggading.com/profiles/avatar16.jpg','folk_soul@gmail.com'),('2025-04-01 10:20:00.000000',NULL,'2025-04-01 10:20:00.000000',_binary '��U\�Z�\�B�\0','레게리듬','https://storage.dinggading.com/profiles/avatar17.jpg','reggae_rhythm@gmail.com'),('2025-04-01 10:25:00.000000',NULL,'2025-04-01 10:25:00.000000',_binary '��VZ�\�B�\0','펑크록','https://storage.dinggading.com/profiles/avatar18.jpg','punk_rock@gmail.com');
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_rank`
--

DROP TABLE IF EXISTS `member_rank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_rank` (
  `beat_score` int DEFAULT NULL,
  `rank_success_count` int DEFAULT '0',
  `tone_score` int DEFAULT NULL,
  `tune_score` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `defence_expire_date` datetime(6) DEFAULT NULL,
  `last_attempt_date` datetime(6) DEFAULT NULL,
  `member_rank_id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `member_id` binary(16) NOT NULL,
  `instrument` enum('BASS','DRUM','GUITAR','VOCAL') NOT NULL,
  `last_attempt_tier` enum('BRONZE','DIAMOND','GOLD','IRON','PLATINUM','SILVER','UNRANKED') DEFAULT NULL,
  `tier` enum('BRONZE','DIAMOND','GOLD','IRON','PLATINUM','SILVER','UNRANKED') NOT NULL,
  PRIMARY KEY (`member_rank_id`),
  KEY `FK40k4e0u2hxs8q8w6cl0r8qpyh` (`member_id`),
  CONSTRAINT `FK40k4e0u2hxs8q8w6cl0r8qpyh` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_rank`
--

LOCK TABLES `member_rank` WRITE;
/*!40000 ALTER TABLE `member_rank` DISABLE KEYS */;
INSERT INTO `member_rank` VALUES (95,10,90,92,'2025-04-03 09:00:00.000000','2025-06-03 09:00:00.000000','2025-04-03 09:00:00.000000',1,'2025-04-03 09:00:00.000000',_binary '��g�Z�\�B�\0','GUITAR','DIAMOND','DIAMOND'),(75,3,70,72,'2025-04-03 09:10:00.000000','2025-06-03 09:10:00.000000','2025-04-03 09:10:00.000000',2,'2025-04-03 09:10:00.000000',_binary '��g�Z�\�B�\0','BASS','SILVER','SILVER'),(65,2,60,62,'2025-04-03 09:20:00.000000','2025-06-03 09:20:00.000000','2025-04-03 09:20:00.000000',3,'2025-04-03 09:20:00.000000',_binary '��g�Z�\�B�\0','DRUM','BRONZE','BRONZE'),(0,0,0,0,'2025-04-03 09:30:00.000000',NULL,NULL,4,'2025-04-03 09:30:00.000000',_binary '��g�Z�\�B�\0','VOCAL','UNRANKED','UNRANKED'),(92,8,88,90,'2025-04-03 10:00:00.000000','2025-06-03 10:00:00.000000','2025-04-03 10:00:00.000000',5,'2025-04-03 10:00:00.000000',_binary '��i�Z�\�B�\0','DRUM','PLATINUM','PLATINUM'),(80,5,78,82,'2025-04-03 10:10:00.000000','2025-06-03 10:10:00.000000','2025-04-03 10:10:00.000000',6,'2025-04-03 10:10:00.000000',_binary '��i�Z�\�B�\0','GUITAR','GOLD','GOLD'),(70,3,68,72,'2025-04-03 10:20:00.000000','2025-06-03 10:20:00.000000','2025-04-03 10:20:00.000000',7,'2025-04-03 10:20:00.000000',_binary '��i�Z�\�B�\0','BASS','SILVER','SILVER'),(60,2,58,62,'2025-04-03 10:30:00.000000','2025-06-03 10:30:00.000000','2025-04-03 10:30:00.000000',8,'2025-04-03 10:30:00.000000',_binary '��i�Z�\�B�\0','VOCAL','BRONZE','BRONZE'),(96,12,94,95,'2025-04-03 11:00:00.000000','2025-06-03 11:00:00.000000','2025-04-03 11:00:00.000000',9,'2025-04-03 11:00:00.000000',_binary '��j�Z�\�B�\0','BASS','DIAMOND','DIAMOND'),(85,7,82,84,'2025-04-03 11:10:00.000000','2025-06-03 11:10:00.000000','2025-04-03 11:10:00.000000',10,'2025-04-03 11:10:00.000000',_binary '��j�Z�\�B�\0','GUITAR','PLATINUM','PLATINUM'),(75,5,73,76,'2025-04-03 11:20:00.000000','2025-06-03 11:20:00.000000','2025-04-03 11:20:00.000000',11,'2025-04-03 11:20:00.000000',_binary '��j�Z�\�B�\0','DRUM','GOLD','GOLD'),(65,3,63,66,'2025-04-03 11:30:00.000000','2025-06-03 11:30:00.000000','2025-04-03 11:30:00.000000',12,'2025-04-03 11:30:00.000000',_binary '��j�Z�\�B�\0','VOCAL','SILVER','SILVER'),(94,11,98,96,'2025-04-03 12:00:00.000000','2025-06-03 12:00:00.000000','2025-04-03 12:00:00.000000',13,'2025-04-03 12:00:00.000000',_binary '��k4Z�\�B�\0','VOCAL','DIAMOND','DIAMOND'),(65,2,63,64,'2025-04-03 12:10:00.000000','2025-06-03 12:10:00.000000','2025-04-03 12:10:00.000000',14,'2025-04-03 12:10:00.000000',_binary '��k4Z�\�B�\0','GUITAR','BRONZE','BRONZE'),(55,1,53,54,'2025-04-03 12:20:00.000000','2025-06-03 12:20:00.000000','2025-04-03 12:20:00.000000',15,'2025-04-03 12:20:00.000000',_binary '��k4Z�\�B�\0','BASS','IRON','IRON'),(0,0,0,0,'2025-04-03 12:30:00.000000',NULL,NULL,16,'2025-04-03 12:30:00.000000',_binary '��k4Z�\�B�\0','DRUM','UNRANKED','UNRANKED'),(88,7,92,90,'2025-04-03 13:00:00.000000','2025-06-03 13:00:00.000000','2025-04-03 13:00:00.000000',17,'2025-04-03 13:00:00.000000',_binary '��kdZ�\�B�\0','VOCAL','PLATINUM','PLATINUM'),(82,5,80,81,'2025-04-03 13:10:00.000000','2025-06-03 13:10:00.000000','2025-04-03 13:10:00.000000',18,'2025-04-03 13:10:00.000000',_binary '��kdZ�\�B�\0','GUITAR','GOLD','GOLD'),(72,3,70,71,'2025-04-03 13:20:00.000000','2025-06-03 13:20:00.000000','2025-04-03 13:20:00.000000',19,'2025-04-03 13:20:00.000000',_binary '��kdZ�\�B�\0','BASS','SILVER','SILVER'),(62,2,60,61,'2025-04-03 13:30:00.000000','2025-06-03 13:30:00.000000','2025-04-03 13:30:00.000000',20,'2025-04-03 13:30:00.000000',_binary '��kdZ�\�B�\0','DRUM','BRONZE','BRONZE'),(96,12,94,95,'2025-04-03 14:00:00.000000','2025-06-03 14:00:00.000000','2025-04-03 14:00:00.000000',21,'2025-04-03 14:00:00.000000',_binary '��k�Z�\�B�\0','GUITAR','DIAMOND','DIAMOND'),(94,11,92,93,'2025-04-03 14:10:00.000000','2025-06-03 14:10:00.000000','2025-04-03 14:10:00.000000',22,'2025-04-03 14:10:00.000000',_binary '��k�Z�\�B�\0','DRUM','DIAMOND','DIAMOND'),(86,8,84,85,'2025-04-03 14:20:00.000000','2025-06-03 14:20:00.000000','2025-04-03 14:20:00.000000',23,'2025-04-03 14:20:00.000000',_binary '��k�Z�\�B�\0','BASS','PLATINUM','PLATINUM'),(78,5,80,79,'2025-04-03 14:30:00.000000','2025-06-03 14:30:00.000000','2025-04-03 14:30:00.000000',24,'2025-04-03 14:30:00.000000',_binary '��k�Z�\�B�\0','VOCAL','GOLD','GOLD'),(82,6,80,81,'2025-04-03 15:00:00.000000','2025-06-03 15:00:00.000000','2025-04-03 15:00:00.000000',25,'2025-04-03 15:00:00.000000',_binary '��k\�Z�\�B�\0','GUITAR','GOLD','GOLD'),(84,6,82,83,'2025-04-03 15:10:00.000000','2025-06-03 15:10:00.000000','2025-04-03 15:10:00.000000',26,'2025-04-03 15:10:00.000000',_binary '��k\�Z�\�B�\0','DRUM','GOLD','GOLD'),(86,7,84,85,'2025-04-03 15:20:00.000000','2025-06-03 15:20:00.000000','2025-04-03 15:20:00.000000',27,'2025-04-03 15:20:00.000000',_binary '��k\�Z�\�B�\0','BASS','PLATINUM','PLATINUM'),(75,4,78,76,'2025-04-03 15:30:00.000000','2025-06-03 15:30:00.000000','2025-04-03 15:30:00.000000',28,'2025-04-03 15:30:00.000000',_binary '��k\�Z�\�B�\0','VOCAL','SILVER','SILVER'),(78,5,76,77,'2025-04-03 16:00:00.000000','2025-06-03 16:00:00.000000','2025-04-03 16:00:00.000000',29,'2025-04-03 16:00:00.000000',_binary '��k\�Z�\�B�\0','GUITAR','SILVER','SILVER'),(68,3,66,67,'2025-04-03 16:10:00.000000','2025-06-03 16:10:00.000000','2025-04-03 16:10:00.000000',30,'2025-04-03 16:10:00.000000',_binary '��k\�Z�\�B�\0','DRUM','BRONZE','BRONZE'),(75,4,73,74,'2025-04-03 16:20:00.000000','2025-06-03 16:20:00.000000','2025-04-03 16:20:00.000000',31,'2025-04-03 16:20:00.000000',_binary '��k\�Z�\�B�\0','BASS','SILVER','SILVER'),(90,9,92,91,'2025-04-03 16:30:00.000000','2025-06-03 16:30:00.000000','2025-04-03 16:30:00.000000',32,'2025-04-03 16:30:00.000000',_binary '��k\�Z�\�B�\0','VOCAL','PLATINUM','PLATINUM'),(75,4,73,74,'2025-04-03 17:00:00.000000','2025-06-03 17:00:00.000000','2025-04-03 17:00:00.000000',33,'2025-04-03 17:00:00.000000',_binary '��lZ�\�B�\0','GUITAR','SILVER','SILVER'),(65,2,63,64,'2025-04-03 17:10:00.000000','2025-06-03 17:10:00.000000','2025-04-03 17:10:00.000000',34,'2025-04-03 17:10:00.000000',_binary '��lZ�\�B�\0','DRUM','BRONZE','BRONZE'),(70,3,68,69,'2025-04-03 17:20:00.000000','2025-06-03 17:20:00.000000','2025-04-03 17:20:00.000000',35,'2025-04-03 17:20:00.000000',_binary '��lZ�\�B�\0','BASS','BRONZE','BRONZE'),(88,8,90,89,'2025-04-03 17:30:00.000000','2025-06-03 17:30:00.000000','2025-04-03 17:30:00.000000',36,'2025-04-03 17:30:00.000000',_binary '��lZ�\�B�\0','VOCAL','PLATINUM','PLATINUM'),(92,10,90,91,'2025-04-03 18:00:00.000000','2025-06-03 18:00:00.000000','2025-04-03 18:00:00.000000',37,'2025-04-03 18:00:00.000000',_binary '��lSZ�\�B�\0','GUITAR','PLATINUM','PLATINUM'),(88,8,86,87,'2025-04-03 18:10:00.000000','2025-06-03 18:10:00.000000','2025-04-03 18:10:00.000000',38,'2025-04-03 18:10:00.000000',_binary '��lSZ�\�B�\0','DRUM','PLATINUM','PLATINUM'),(90,9,88,89,'2025-04-03 18:20:00.000000','2025-06-03 18:20:00.000000','2025-04-03 18:20:00.000000',39,'2025-04-03 18:20:00.000000',_binary '��lSZ�\�B�\0','BASS','PLATINUM','PLATINUM'),(85,7,87,86,'2025-04-03 18:30:00.000000','2025-06-03 18:30:00.000000','2025-04-03 18:30:00.000000',40,'2025-04-03 18:30:00.000000',_binary '��lSZ�\�B�\0','VOCAL','GOLD','GOLD'),(70,3,68,69,'2025-04-03 19:00:00.000000','2025-06-03 19:00:00.000000','2025-04-03 19:00:00.000000',41,'2025-04-03 19:00:00.000000',_binary '��l�Z�\�B�\0','GUITAR','BRONZE','BRONZE'),(60,2,58,59,'2025-04-03 19:10:00.000000','2025-06-03 19:10:00.000000','2025-04-03 19:10:00.000000',42,'2025-04-03 19:10:00.000000',_binary '��l�Z�\�B�\0','DRUM','BRONZE','BRONZE'),(65,2,63,64,'2025-04-03 19:20:00.000000','2025-06-03 19:20:00.000000','2025-04-03 19:20:00.000000',43,'2025-04-03 19:20:00.000000',_binary '��l�Z�\�B�\0','BASS','BRONZE','BRONZE'),(92,10,94,93,'2025-04-03 19:30:00.000000','2025-06-03 19:30:00.000000','2025-04-03 19:30:00.000000',44,'2025-04-03 19:30:00.000000',_binary '��l�Z�\�B�\0','VOCAL','PLATINUM','PLATINUM'),(72,4,70,71,'2025-04-03 20:00:00.000000','2025-06-03 20:00:00.000000','2025-04-03 20:00:00.000000',45,'2025-04-03 20:00:00.000000',_binary '��l�Z�\�B�\0','GUITAR','SILVER','SILVER'),(94,11,92,93,'2025-04-03 20:10:00.000000','2025-06-03 20:10:00.000000','2025-04-03 20:10:00.000000',46,'2025-04-03 20:10:00.000000',_binary '��l�Z�\�B�\0','DRUM','DIAMOND','DIAMOND'),(80,6,78,79,'2025-04-03 20:20:00.000000','2025-06-03 20:20:00.000000','2025-04-03 20:20:00.000000',47,'2025-04-03 20:20:00.000000',_binary '��l�Z�\�B�\0','BASS','GOLD','GOLD'),(68,3,70,69,'2025-04-03 20:30:00.000000','2025-06-03 20:30:00.000000','2025-04-03 20:30:00.000000',48,'2025-04-03 20:30:00.000000',_binary '��l�Z�\�B�\0','VOCAL','BRONZE','BRONZE'),(90,8,88,89,'2025-04-03 21:00:00.000000','2025-06-03 21:00:00.000000','2025-04-03 21:00:00.000000',49,'2025-04-03 21:00:00.000000',_binary '��Q_Z�\�B�\0','GUITAR','PLATINUM','PLATINUM'),(80,5,78,79,'2025-04-03 21:10:00.000000','2025-06-03 21:10:00.000000','2025-04-03 21:10:00.000000',50,'2025-04-03 21:10:00.000000',_binary '��Q_Z�\�B�\0','BASS','GOLD','GOLD'),(70,3,68,69,'2025-04-03 21:20:00.000000','2025-06-03 21:20:00.000000','2025-04-03 21:20:00.000000',51,'2025-04-03 21:20:00.000000',_binary '��Q_Z�\�B�\0','DRUM','SILVER','SILVER'),(60,1,58,59,'2025-04-03 21:30:00.000000','2025-06-03 21:30:00.000000','2025-04-03 21:30:00.000000',52,'2025-04-03 21:30:00.000000',_binary '��Q_Z�\�B�\0','VOCAL','BRONZE','BRONZE'),(70,3,72,71,'2025-04-03 22:00:00.000000','2025-06-03 22:00:00.000000','2025-04-03 22:00:00.000000',53,'2025-04-03 22:00:00.000000',_binary '��TZ�\�B�\0','GUITAR','SILVER','SILVER'),(75,4,77,76,'2025-04-03 22:10:00.000000','2025-06-03 22:10:00.000000','2025-04-03 22:10:00.000000',54,'2025-04-03 22:10:00.000000',_binary '��TZ�\�B�\0','DRUM','SILVER','SILVER'),(80,5,82,81,'2025-04-03 22:20:00.000000','2025-06-03 22:20:00.000000','2025-04-03 22:20:00.000000',55,'2025-04-03 22:20:00.000000',_binary '��TZ�\�B�\0','BASS','GOLD','GOLD'),(95,10,97,96,'2025-04-03 22:30:00.000000','2025-06-03 22:30:00.000000','2025-04-03 22:30:00.000000',56,'2025-04-03 22:30:00.000000',_binary '��TZ�\�B�\0','VOCAL','DIAMOND','DIAMOND'),(93,9,92,93,'2025-04-03 23:00:00.000000','2025-06-03 23:00:00.000000','2025-04-03 23:00:00.000000',57,'2025-04-03 23:00:00.000000',_binary '��UgZ�\�B�\0','GUITAR','PLATINUM','PLATINUM'),(78,4,77,78,'2025-04-03 23:10:00.000000','2025-06-03 23:10:00.000000','2025-04-03 23:10:00.000000',58,'2025-04-03 23:10:00.000000',_binary '��UgZ�\�B�\0','DRUM','SILVER','SILVER'),(82,6,81,82,'2025-04-03 23:20:00.000000','2025-06-03 23:20:00.000000','2025-04-03 23:20:00.000000',59,'2025-04-03 23:20:00.000000',_binary '��UgZ�\�B�\0','BASS','GOLD','GOLD'),(88,7,90,89,'2025-04-03 23:30:00.000000','2025-06-03 23:30:00.000000','2025-04-03 23:30:00.000000',60,'2025-04-03 23:30:00.000000',_binary '��UgZ�\�B�\0','VOCAL','PLATINUM','PLATINUM'),(85,6,84,85,'2025-04-04 00:00:00.000000','2025-06-04 00:00:00.000000','2025-04-04 00:00:00.000000',61,'2025-04-04 00:00:00.000000',_binary '��U�Z�\�B�\0','GUITAR','GOLD','GOLD'),(65,2,64,65,'2025-04-04 00:10:00.000000','2025-06-04 00:10:00.000000','2025-04-04 00:10:00.000000',62,'2025-04-04 00:10:00.000000',_binary '��U�Z�\�B�\0','DRUM','BRONZE','BRONZE'),(70,3,69,70,'2025-04-04 00:20:00.000000','2025-06-04 00:20:00.000000','2025-04-04 00:20:00.000000',63,'2025-04-04 00:20:00.000000',_binary '��U�Z�\�B�\0','BASS','SILVER','SILVER'),(92,9,94,93,'2025-04-04 00:30:00.000000','2025-06-04 00:30:00.000000','2025-04-04 00:30:00.000000',64,'2025-04-04 00:30:00.000000',_binary '��U�Z�\�B�\0','VOCAL','PLATINUM','PLATINUM'),(78,5,77,78,'2025-04-04 01:00:00.000000','2025-06-04 01:00:00.000000','2025-04-04 01:00:00.000000',65,'2025-04-04 01:00:00.000000',_binary '��U\�Z�\�B�\0','GUITAR','SILVER','SILVER'),(92,9,91,92,'2025-04-04 01:10:00.000000','2025-06-04 01:10:00.000000','2025-04-04 01:10:00.000000',66,'2025-04-04 01:10:00.000000',_binary '��U\�Z�\�B�\0','DRUM','PLATINUM','PLATINUM'),(80,6,79,80,'2025-04-04 01:20:00.000000','2025-06-04 01:20:00.000000','2025-04-04 01:20:00.000000',67,'2025-04-04 01:20:00.000000',_binary '��U\�Z�\�B�\0','BASS','PLATINUM','PLATINUM'),(92,9,94,93,'2025-04-04 00:30:00.000000','2025-06-04 00:30:00.000000','2025-04-04 00:30:00.000000',68,'2025-04-04 00:30:00.000000',_binary '��U\�Z�\�B�\0','VOCAL','PLATINUM','PLATINUM');
/*!40000 ALTER TABLE `member_rank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_tier_log`
--

DROP TABLE IF EXISTS `member_tier_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_tier_log` (
  `changed_date` date NOT NULL,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `member_rank_id` bigint NOT NULL,
  `tier_log_id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `after_tier` enum('BRONZE','DIAMOND','GOLD','IRON','PLATINUM','SILVER','UNRANKED') NOT NULL,
  `before_tier` enum('BRONZE','DIAMOND','GOLD','IRON','PLATINUM','SILVER','UNRANKED') NOT NULL,
  PRIMARY KEY (`tier_log_id`),
  KEY `FKs0w19it2ii66837jx953r2l0w` (`member_rank_id`),
  CONSTRAINT `FKs0w19it2ii66837jx953r2l0w` FOREIGN KEY (`member_rank_id`) REFERENCES `member_rank` (`member_rank_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_tier_log`
--

LOCK TABLES `member_tier_log` WRITE;
/*!40000 ALTER TABLE `member_tier_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `member_tier_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `is_success` bit(1) DEFAULT NULL,
  `read_or_not` bit(1) NOT NULL,
  `attempt_id` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `notification_id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `receiver_id` binary(16) NOT NULL,
  `sender_id` binary(16) DEFAULT NULL,
  `chat_room_id` varchar(255) DEFAULT NULL COMMENT '채팅방 ID',
  `message` varchar(255) NOT NULL,
  `tier_and_instrument` varchar(255) DEFAULT NULL,
  `type` enum('CHAT','FOLLOW','RANK','RECRUITMENT') NOT NULL COMMENT '알림 타입',
  PRIMARY KEY (`notification_id`),
  KEY `FK1jpw68rbaxvu8u5l1dniain1l` (`receiver_id`),
  KEY `FKovuqht5f5v592yehlp3tgji9y` (`sender_id`),
  CONSTRAINT `FK1jpw68rbaxvu8u5l1dniain1l` FOREIGN KEY (`receiver_id`) REFERENCES `member` (`member_id`),
  CONSTRAINT `FKovuqht5f5v592yehlp3tgji9y` FOREIGN KEY (`sender_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `participants`
--

DROP TABLE IF EXISTS `participants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `participants` (
  `is_host` bit(1) NOT NULL,
  `joined_at` datetime(6) NOT NULL,
  `livehouse_id` bigint NOT NULL,
  `participant_id` bigint NOT NULL AUTO_INCREMENT,
  `connection_id` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) NOT NULL,
  PRIMARY KEY (`participant_id`),
  KEY `FKjftgelu8ef2ksd5l6k2dhopcv` (`livehouse_id`),
  CONSTRAINT `FKjftgelu8ef2ksd5l6k2dhopcv` FOREIGN KEY (`livehouse_id`) REFERENCES `livehouses` (`livehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `participants`
--

LOCK TABLES `participants` WRITE;
/*!40000 ALTER TABLE `participants` DISABLE KEYS */;
/*!40000 ALTER TABLE `participants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rank_matching`
--

DROP TABLE IF EXISTS `rank_matching`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rank_matching` (
  `attempt_count` int NOT NULL,
  `expire_date` date NOT NULL,
  `started_at` date NOT NULL,
  `success_count` int NOT NULL,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `member_rank` bigint NOT NULL,
  `rank_matching_id` bigint NOT NULL AUTO_INCREMENT,
  `song_instrument_pack_id` bigint NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `instrument` enum('BASS','DRUM','GUITAR','VOCAL') NOT NULL,
  `rank_type` enum('CHALLENGE','DEFENCE','FIRST') NOT NULL,
  `status` enum('ANALYZING','COMPLETED','EXPIRED','FAILED','IN_PROGRESS') NOT NULL,
  `target_tier` enum('BRONZE','DIAMOND','GOLD','IRON','PLATINUM','SILVER','UNRANKED') NOT NULL,
  PRIMARY KEY (`rank_matching_id`),
  KEY `FK4lunff6cfbrajma4elbhnw3yx` (`member_rank`),
  KEY `FKs29sxctxkdwqpli3o9wtblo79` (`song_instrument_pack_id`),
  CONSTRAINT `FK4lunff6cfbrajma4elbhnw3yx` FOREIGN KEY (`member_rank`) REFERENCES `member_rank` (`member_rank_id`),
  CONSTRAINT `FKs29sxctxkdwqpli3o9wtblo79` FOREIGN KEY (`song_instrument_pack_id`) REFERENCES `song_instrument_pack` (`song_instrument_pack_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rank_matching`
--

LOCK TABLES `rank_matching` WRITE;
/*!40000 ALTER TABLE `rank_matching` DISABLE KEYS */;
/*!40000 ALTER TABLE `rank_matching` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `record`
--

DROP TABLE IF EXISTS `record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `record` (
  `attempt_id` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `record_id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `member_id` binary(16) NOT NULL,
  `record_url` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `dtype` enum('CHALLENGE','LIVE_HOUSE','PRACTICE') NOT NULL,
  PRIMARY KEY (`record_id`),
  KEY `FK5x2le9w7u4enyk4le46j84jbb` (`attempt_id`),
  KEY `FKt0rib4n7orlcfx52cnsicmriw` (`member_id`),
  CONSTRAINT `FK5x2le9w7u4enyk4le46j84jbb` FOREIGN KEY (`attempt_id`) REFERENCES `attempt` (`attempt_id`),
  CONSTRAINT `FKt0rib4n7orlcfx52cnsicmriw` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `record`
--

LOCK TABLES `record` WRITE;
/*!40000 ALTER TABLE `record` DISABLE KEYS */;
/*!40000 ALTER TABLE `record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `song`
--

DROP TABLE IF EXISTS `song`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `song` (
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `song_id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `artist` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `song_filename` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `youtube_url` varchar(255) NOT NULL,
  PRIMARY KEY (`song_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `song`
--

LOCK TABLES `song` WRITE;
/*!40000 ALTER TABLE `song` DISABLE KEYS */;
INSERT INTO `song` VALUES ('2025-04-10 23:37:09.000000',1,NULL,'Cranberries',NULL,'original_Cranberries-Zombie.wav','Zombie','https://www.youtube.com/watch?v=yGxHDmHoqXc'),('2025-04-10 23:37:09.000000',2,NULL,'Day6',NULL,'original_Day6-HAPPY.wav','HAPPY','https://www.youtube.com/watch?v=2o1zdX72400'),('2025-04-10 23:37:09.000000',3,NULL,'윤도현',NULL,'original_윤도현-나는 나비.wav','나는 나비','https://www.youtube.com/watch?v=UTg9wf1HGI8'),('2025-04-10 23:37:09.000000',4,NULL,'전기뱀장어',NULL,'original_전기뱀장어-미로.wav','미로','https://www.youtube.com/watch?v=VcBBvkC_OIc'),('2025-04-10 23:37:09.000000',5,NULL,'카더가든',NULL,'original_카더가든-꿈을 꿨어요.wav','꿈을 꿨어요','https://www.youtube.com/watch?v=ACORuGQlj7s'),('2025-04-10 23:37:09.000000',6,NULL,'QWER',NULL,'original_QWER-불꽃놀이.wav','불꽃놀이','https://www.youtube.com/watch?v=HBsU1GpsGKw'),('2025-04-10 23:37:09.000000',7,NULL,'백예린',NULL,'original_백예린-Square.wav','Square','https://www.youtube.com/watch?v=4iFP_wd6QU8'),('2025-04-10 23:37:09.000000',8,NULL,'국카스텐',NULL,'original_국카스텐-거울.wav','거울','https://www.youtube.com/watch?v=YWDKnqOZ3ro'),('2025-04-10 23:37:09.000000',9,NULL,'실리카겔',NULL,'original_실리카겔-Tik_Tak_Tok.wav','Tik Tak Tok','https://www.youtube.com/watch?v=-ofxXCNCEMk'),('2025-04-10 23:37:09.000000',10,NULL,'Day6',NULL,'original_Day6-한_페이지가_될_수_있게.wav','한 페이지가 될 수 있게','https://www.youtube.com/watch?v=oYvgISKD5Y8'),('2025-04-10 23:37:09.000000',11,NULL,'GreenDay',NULL,'original_GreenDay-American_Idiot.wav','American Idiot','https://www.youtube.com/watch?v=kqkGY9jsBOw'),('2025-04-10 23:37:09.000000',12,NULL,'검정치마',NULL,'original_검정치마-Antifreeze.wav','Antifreeze','https://www.youtube.com/watch?v=PGADim6UzHE'),('2025-04-10 23:37:09.000000',13,NULL,'루시',NULL,'original_루시-아니_근데_진짜.wav','아니 근데 진짜','https://www.youtube.com/watch?v=3B48-nEois4'),('2025-04-10 23:37:09.000000',14,NULL,'송골매',NULL,'original_송골매-어쩌다_마주친_그대.wav','어쩌다 마주친 그대','https://www.youtube.com/watch?v=Jcfi9gOru6k'),('2025-04-10 23:37:09.000000',15,NULL,'쏜애플',NULL,'original_쏜애플-시퍼런 봄.wav','시퍼런 봄','https://www.youtube.com/watch?v=8i-B1ieI_kY'),('2025-04-10 23:37:09.000000',16,NULL,'적재',NULL,'original_적재-Runaway.wav','Runaway','https://www.youtube.com/watch?v=3euQmS2s5s8&list=RD3euQmS2s5s8&start_radio=1'),('2025-04-10 23:37:09.000000',17,NULL,'터치드',NULL,'original_터치드-highlight.wav','highlight','https://www.youtube.com/watch?v=L5Ba-pp3Qw0'),('2025-04-10 23:37:09.000000',18,NULL,'한로로',NULL,'original_한로로-먹이사슬.wav','먹이사슬','https://www.youtube.com/watch?v=zIWJCvYkeXY'),('2025-04-10 23:37:09.000000',19,NULL,'혁오',NULL,'original_혁오-가죽자켓.wav','가죽자켓','https://www.youtube.com/watch?v=Nk6xg5D81eI'),('2025-04-10 23:37:09.000000',20,NULL,'새소년',NULL,'original_새소년-파도.wav','파도','https://www.youtube.com/watch?v=L4dOpDJPcEA'),('2025-04-10 23:37:09.000000',21,NULL,'실리카겔',NULL,'original_실리카겔-Desert_Eagle.wav','Desert Eagle','https://www.youtube.com/watch?v=CJ59m66zHTQ'),('2025-04-10 23:37:09.000000',22,NULL,'실리카겔',NULL,'original_실리카겔-No_Pain.wav','No Pain','https://www.youtube.com/watch?v=muBCVHSUNrE'),('2025-04-10 23:37:09.000000',23,NULL,'쏜애플',NULL,'original_쏜애플-멸종.wav','멸종','https://www.youtube.com/watch?v=NsoAy4vGyEg'),('2025-04-10 23:37:09.000000',24,NULL,'아이유',NULL,'original_아이유-있잖아.wav','있잖아','https://www.youtube.com/watch?v=MF7lASJkAPM'),('2025-04-10 23:37:09.000000',25,NULL,'유다빈밴드',NULL,'original_유다빈밴드-항해.wav','항해','https://www.youtube.com/watch?v=Gbb2A5m7d_4'),('2025-04-10 23:37:09.000000',26,NULL,'카더가든',NULL,'original_카더가든-가까운 듯 먼 그대여.wav','가까운 듯 먼 그대여','https://www.youtube.com/watch?v=CAxPgfOqtr8'),('2025-04-10 23:37:09.000000',27,NULL,'Stevie Wonder',NULL,'original_Stevie Wonder-Isnt She Lovely.wav','Isnt She Lovely','https://www.youtube.com/watch?v=oE56g61mW44');
/*!40000 ALTER TABLE `song` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `song_by_instrument`
--

DROP TABLE IF EXISTS `song_by_instrument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `song_by_instrument` (
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `song_by_instrument_id` bigint NOT NULL AUTO_INCREMENT,
  `song_id` bigint NOT NULL,
  `song_instrument_pack_id` bigint NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `song_by_instrument_analysis_json` varchar(255) NOT NULL,
  `song_by_instrument_ex_filename` varchar(255) NOT NULL,
  `song_by_instrument_filename` varchar(255) NOT NULL,
  `instrument` enum('BASS','DRUM','GUITAR','VOCAL') NOT NULL,
  `tier` enum('BRONZE','DIAMOND','GOLD','IRON','PLATINUM','SILVER','UNRANKED') NOT NULL,
  PRIMARY KEY (`song_by_instrument_id`),
  KEY `FKb5ocl7dx36xr4sm27x08qpqwx` (`song_id`),
  KEY `FK47vabskh4cl9dkioshn2rv9uw` (`song_instrument_pack_id`),
  CONSTRAINT `FK47vabskh4cl9dkioshn2rv9uw` FOREIGN KEY (`song_instrument_pack_id`) REFERENCES `song_instrument_pack` (`song_instrument_pack_id`),
  CONSTRAINT `FKb5ocl7dx36xr4sm27x08qpqwx` FOREIGN KEY (`song_id`) REFERENCES `song` (`song_id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `song_by_instrument`
--

LOCK TABLES `song_by_instrument` WRITE;
/*!40000 ALTER TABLE `song_by_instrument` DISABLE KEYS */;
INSERT INTO `song_by_instrument` VALUES ('2025-04-10 23:37:09.000000',1,1,1,NULL,'bronze_bass_Cranberries-Zombie.json','bronze_bass_Cranberries-Zombie_ex.wav','bronze_bass_Cranberries-Zombie.wav','BASS','BRONZE'),('2025-04-10 23:37:09.000000',2,2,1,NULL,'bronze_bass_Day6-HAPPY.json','bronze_bass_Day6-HAPPY_ex.wav','bronze_bass_Day6-HAPPY.wav','BASS','BRONZE'),('2025-04-10 23:37:09.000000',3,3,1,NULL,'bronze_bass_윤도현-나는 나비.json','bronze_bass_윤도현-나는 나비_ex.wav','bronze_bass_윤도현-나는 나비.wav','BASS','BRONZE'),('2025-04-10 23:37:09.000000',4,4,1,NULL,'bronze_bass_전기뱀장어-미로.json','bronze_bass_전기뱀장어-미로_ex.wav','bronze_bass_전기뱀장어-미로.wav','BASS','BRONZE'),('2025-04-10 23:37:09.000000',5,5,1,NULL,'bronze_bass_카더가든-꿈을 꿨어요.json','bronze_bass_카더가든-꿈을 꿨어요_ex.wav','bronze_bass_카더가든-꿈을 꿨어요.wav','BASS','BRONZE'),('2025-04-10 23:37:09.000000',6,6,16,NULL,'silver_bass_QWER-불꽃놀이.json','silver_bass_QWER-불꽃놀이_ex.wav','silver_bass_QWER-불꽃놀이.wav','BASS','SILVER'),('2025-04-10 23:37:09.000000',7,7,16,NULL,'silver_bass_백예린-Square.json','silver_bass_백예린-Square_ex.wav','silver_bass_백예린-Square.wav','BASS','SILVER'),('2025-04-10 23:37:09.000000',8,26,16,NULL,'silver_bass_카더가든-가까운 듯 먼 그대여.json','silver_bass_카더가든-가까운 듯 먼 그대여_ex.wav','silver_bass_카더가든-가까운 듯 먼 그대여.wav','BASS','SILVER'),('2025-04-10 23:37:09.000000',9,27,16,NULL,'silver_bass_Stevie_Wonder-Isnt_She_Lovely.json','silver_bass_Stevie_Wonder-Isnt_She_Lovely_ex.wav','silver_bass_Stevie_Wonder-Isnt_She_Lovely.wav','BASS','SILVER'),('2025-04-10 23:37:09.000000',10,15,8,NULL,'gold_bass_쏜애플-시퍼런 봄.json','gold_bass_쏜애플-시퍼런 봄_ex.wav','gold_bass_쏜애플-시퍼런 봄.wav','BASS','SILVER'),('2025-04-10 23:37:09.000000',11,10,8,NULL,'gold_bass_Day6-한_페이지가_될_수_있게.json','gold_bass_Day6-한_페이지가_될_수_있게_ex.wav','gold_bass_Day6-한_페이지가_될_수_있게.wav','BASS','GOLD'),('2025-04-10 23:37:09.000000',12,11,8,NULL,'gold_bass_GreenDay-American_Idiot.json','gold_bass_GreenDay-American_Idiot_ex.wav','gold_bass_GreenDay-American_Idiot.wav','BASS','GOLD'),('2025-04-10 23:37:09.000000',13,12,8,NULL,'gold_bass_검정치마-Antifreeze.json','gold_bass_검정치마-Antifreeze_ex.wav','gold_bass_검정치마-Antifreeze.wav','BASS','GOLD'),('2025-04-10 23:37:09.000000',14,13,8,NULL,'gold_bass_루시-아니_근데_진짜.json','gold_bass_루시-아니_근데_진짜_ex.wav','gold_bass_루시-아니_근데_진짜.wav','BASS','GOLD'),('2025-04-10 23:37:09.000000',15,14,8,NULL,'gold_bass_송골매-어쩌다_마주친_그대.json','gold_bass_송골매-어쩌다_마주친_그대_ex.wav','gold_bass_송골매-어쩌다_마주친_그대.wav','BASS','GOLD'),('2025-04-10 23:37:09.000000',16,20,12,NULL,'platinum_bass_새소년-파도.json','platinum_bass_새소년-파도_ex.wav','platinum_bass_새소년-파도.wav','BASS','PLATINUM'),('2025-04-10 23:37:09.000000',17,21,12,NULL,'platinum_bass_실리카겔-Desert_Eagle.json','platinum_bass_실리카겔-Desert_Eagle_ex.wav','platinum_bass_실리카겔-Desert_Eagle.wav','BASS','PLATINUM'),('2025-04-10 23:37:09.000000',18,22,12,NULL,'platinum_bass_실리카겔-No_Pain.json','platinum_bass_실리카겔-No_Pain_ex.wav','platinum_bass_실리카겔-No_Pain.wav','BASS','PLATINUM'),('2025-04-10 23:37:09.000000',19,23,12,NULL,'platinum_bass_쏜애플-멸종.json','platinum_bass_쏜애플-멸종_ex.wav','platinum_bass_쏜애플-멸종.wav','BASS','PLATINUM'),('2025-04-10 23:37:09.000000',20,24,12,NULL,'platinum_bass_아이유-있잖아.json','platinum_bass_아이유-있잖아_ex.wav','platinum_bass_아이유-있잖아.wav','BASS','PLATINUM'),('2025-04-10 23:37:09.000000',21,8,4,NULL,'diamond_bass_국카스텐-거울.json','diamond_bass_국카스텐-거울_ex.wav','diamond_bass_국카스텐-거울.wav','BASS','DIAMOND'),('2025-04-10 23:37:09.000000',22,9,4,NULL,'diamond_bass_실리카겔-Tik_Tak_Tok.json','diamond_bass_실리카겔-Tik_Tak_Tok_ex.wav','diamond_bass_실리카겔-Tik_Tak_Tok.wav','BASS','DIAMOND'),('2025-04-10 23:37:09.000000',23,1,2,NULL,'bronze_drums_Cranberries-Zombie.json','bronze_drums_Cranberries-Zombie_ex.wav','bronze_drums_Cranberries-Zombie.wav','DRUM','BRONZE'),('2025-04-10 23:37:09.000000',24,2,2,NULL,'bronze_drums_Day6-HAPPY.json','bronze_drums_Day6-HAPPY_ex.wav','bronze_drums_Day6-HAPPY.wav','DRUM','BRONZE'),('2025-04-10 23:37:09.000000',25,6,2,NULL,'bronze_drums_QWER-불꽃놀이.json','bronze_drums_QWER-불꽃놀이_ex.wav','bronze_drums_QWER-불꽃놀이.wav','DRUM','BRONZE'),('2025-04-10 23:37:09.000000',26,3,2,NULL,'bronze_drums_윤도현-나는 나비.json','bronze_drums_윤도현-나는 나비_ex.wav','bronze_drums_윤도현-나는 나비.wav','DRUM','BRONZE'),('2025-04-10 23:37:09.000000',27,7,17,NULL,'silver_drums_백예린-Square.json','silver_drums_백예린-Square_ex.wav','silver_drums_백예린-Square.wav','DRUM','SILVER'),('2025-04-10 23:37:09.000000',28,4,17,NULL,'silver_drums_전기뱀장어-미로.json','silver_drums_전기뱀장어-미로_ex.wav','silver_drums_전기뱀장어-미로.wav','DRUM','SILVER'),('2025-04-10 23:37:09.000000',29,26,17,NULL,'silver_drums_카더가든-가까운 듯 먼 그대여.json','silver_drums_카더가든-가까운 듯 먼 그대여_ex.wav','silver_drums_카더가든-가까운 듯 먼 그대여.wav','DRUM','SILVER'),('2025-04-10 23:37:09.000000',30,5,17,NULL,'silver_drums_카더가든-꿈을 꿨어요.json','silver_drums_카더가든-꿈을 꿨어요_ex.wav','silver_drums_카더가든-꿈을 꿨어요.wav','DRUM','SILVER'),('2025-04-10 23:37:09.000000',31,15,9,NULL,'gold_drums_쏜애플-시퍼런 봄.json','gold_drums_쏜애플-시퍼런 봄_ex.wav','gold_drums_쏜애플-시퍼런 봄.wav','DRUM','SILVER'),('2025-04-10 23:37:09.000000',32,10,9,NULL,'gold_drums_Day6-한_페이지가_될_수_있게.json','gold_drums_Day6-한_페이지가_될_수_있게_ex.wav','gold_drums_Day6-한_페이지가_될_수_있게.wav','DRUM','GOLD'),('2025-04-10 23:37:09.000000',33,11,9,NULL,'gold_drums_GreenDay-American_Idiot.json','gold_drums_GreenDay-American_Idiot_ex.wav','gold_drums_GreenDay-American_Idiot.wav','DRUM','GOLD'),('2025-04-10 23:37:09.000000',34,12,9,NULL,'gold_drums_검정치마-Antifreeze.json','gold_drums_검정치마-Antifreeze_ex.wav','gold_drums_검정치마-Antifreeze.wav','DRUM','GOLD'),('2025-04-10 23:37:09.000000',35,13,9,NULL,'gold_drums_루시-아니_근데_진짜.json','gold_drums_루시-아니_근데_진짜_ex.wav','gold_drums_루시-아니_근데_진짜.wav','DRUM','GOLD'),('2025-04-10 23:37:09.000000',36,14,9,NULL,'gold_drums_송골매-어쩌다_마주친_그대.json','gold_drums_송골매-어쩌다_마주친_그대_ex.wav','gold_drums_송골매-어쩌다_마주친_그대.wav','DRUM','GOLD'),('2025-04-10 23:37:09.000000',37,20,13,NULL,'platinum_drums_새소년-파도.json','platinum_drums_새소년-파도_ex.wav','platinum_drums_새소년-파도.wav','DRUM','PLATINUM'),('2025-04-10 23:37:09.000000',38,21,13,NULL,'platinum_drums_실리카겔-Desert_Eagle.json','platinum_drums_실리카겔-Desert_Eagle_ex.wav','platinum_drums_실리카겔-Desert_Eagle.wav','DRUM','PLATINUM'),('2025-04-10 23:37:09.000000',39,22,13,NULL,'platinum_drums_실리카겔-No_Pain.json','platinum_drums_실리카겔-No_Pain_ex.wav','platinum_drums_실리카겔-No_Pain.wav','DRUM','PLATINUM'),('2025-04-10 23:37:09.000000',40,23,13,NULL,'platinum_drums_쏜애플-멸종.json','platinum_drums_쏜애플-멸종_ex.wav','platinum_drums_쏜애플-멸종.wav','DRUM','PLATINUM'),('2025-04-10 23:37:09.000000',41,24,13,NULL,'platinum_drums_아이유-있잖아.json','platinum_drums_아이유-있잖아_ex.wav','platinum_drums_아이유-있잖아.wav','DRUM','PLATINUM'),('2025-04-10 23:37:09.000000',42,8,5,NULL,'diamond_drums_국카스텐-거울.json','diamond_drums_국카스텐-거울_ex.wav','diamond_drums_국카스텐-거울.wav','DRUM','DIAMOND'),('2025-04-10 23:37:09.000000',43,9,5,NULL,'diamond_drums_실리카겔-Tik_Tak_Tok.json','diamond_drums_실리카겔-Tik_Tak_Tok_ex.wav','diamond_drums_실리카겔-Tik_Tak_Tok.wav','DRUM','DIAMOND'),('2025-04-10 23:37:09.000000',44,1,3,NULL,'bronze_guitar_Cranberries-Zombie.json','bronze_guitar_Cranberries-Zombie_ex.wav','bronze_guitar_Cranberries-Zombie.wav','GUITAR','BRONZE'),('2025-04-10 23:37:09.000000',45,7,3,NULL,'bronze_guitar_백예린-Square.json','bronze_guitar_백예린-Square_ex.wav','bronze_guitar_백예린-Square.wav','GUITAR','BRONZE'),('2025-04-10 23:37:09.000000',46,3,3,NULL,'bronze_guitar_윤도현-나는 나비.json','bronze_guitar_윤도현-나는 나비_ex.wav','bronze_guitar_윤도현-나는 나비.wav','GUITAR','BRONZE'),('2025-04-10 23:37:09.000000',47,2,18,NULL,'silver_guitar_Day6-HAPPY.json','silver_guitar_Day6-HAPPY_ex.wav','silver_guitar_Day6-HAPPY.wav','GUITAR','SILVER'),('2025-04-10 23:37:09.000000',48,6,18,NULL,'silver_guitar_QWER-불꽃놀이.json','silver_guitar_QWER-불꽃놀이_ex.wav','silver_guitar_QWER-불꽃놀이.wav','GUITAR','SILVER'),('2025-04-10 23:37:09.000000',49,4,18,NULL,'silver_guitar_전기뱀장어-미로.json','silver_guitar_전기뱀장어-미로_ex.wav','silver_guitar_전기뱀장어-미로.wav','GUITAR','SILVER'),('2025-04-10 23:37:09.000000',50,26,18,NULL,'silver_guitar_카더가든-가까운 듯 먼 그대여.json','silver_guitar_카더가든-가까운 듯 먼 그대여_ex.wav','silver_guitar_카더가든-가까운 듯 먼 그대여.wav','GUITAR','SILVER'),('2025-04-10 23:37:09.000000',51,5,18,NULL,'silver_guitar_카더가든-꿈을 꿨어요.json','silver_guitar_카더가든-꿈을 꿨어요_ex.wav','silver_guitar_카더가든-꿈을 꿨어요.wav','GUITAR','SILVER'),('2025-04-10 23:37:09.000000',52,10,10,NULL,'gold_guitar_Day6-한_페이지가_될_수_있게.json','gold_guitar_Day6-한_페이지가_될_수_있게_ex.wav','gold_guitar_Day6-한_페이지가_될_수_있게.wav','GUITAR','GOLD'),('2025-04-10 23:37:09.000000',53,11,10,NULL,'gold_guitar_GreenDay-American_Idiot.json','gold_guitar_GreenDay-American_Idiot_ex.wav','gold_guitar_GreenDay-American_Idiot.wav','GUITAR','GOLD'),('2025-04-10 23:37:09.000000',54,12,10,NULL,'gold_guitar_검정치마-Antifreeze.json','gold_guitar_검정치마-Antifreeze_ex.wav','gold_guitar_검정치마-Antifreeze.wav','GUITAR','GOLD'),('2025-04-10 23:37:09.000000',55,13,10,NULL,'gold_guitar_루시-아니_근데_진짜.json','gold_guitar_루시-아니_근데_진짜_ex.wav','gold_guitar_루시-아니_근데_진짜.wav','GUITAR','GOLD'),('2025-04-10 23:37:09.000000',56,14,10,NULL,'gold_guitar_송골매-어쩌다_마주친_그대.json','gold_guitar_송골매-어쩌다_마주친_그대_ex.wav','gold_guitar_송골매-어쩌다_마주친_그대.wav','GUITAR','GOLD'),('2025-04-10 23:37:09.000000',57,20,14,NULL,'platinum_guitar_새소년-파도.json','platinum_guitar_새소년-파도_ex.wav','platinum_guitar_새소년-파도.wav','GUITAR','PLATINUM'),('2025-04-10 23:37:09.000000',58,21,14,NULL,'platinum_guitar_실리카겔-Desert_Eagle.json','platinum_guitar_실리카겔-Desert_Eagle_ex.wav','platinum_guitar_실리카겔-Desert_Eagle.wav','GUITAR','PLATINUM'),('2025-04-10 23:37:09.000000',59,22,14,NULL,'platinum_guitar_실리카겔-No_Pain.json','platinum_guitar_실리카겔-No_Pain_ex.wav','platinum_guitar_실리카겔-No_Pain.wav','GUITAR','PLATINUM'),('2025-04-10 23:37:09.000000',60,23,14,NULL,'platinum_guitar_쏜애플-멸종.json','platinum_guitar_쏜애플-멸종_ex.wav','platinum_guitar_쏜애플-멸종.wav','GUITAR','PLATINUM'),('2025-04-10 23:37:09.000000',61,24,14,NULL,'platinum_guitar_아이유-있잖아.json','platinum_guitar_아이유-있잖아_ex.wav','platinum_guitar_아이유-있잖아.wav','GUITAR','PLATINUM'),('2025-04-10 23:37:09.000000',62,8,6,NULL,'diamond_guitar_국카스텐-거울.json','diamond_guitar_국카스텐-거울_ex.wav','diamond_guitar_국카스텐-거울.wav','GUITAR','DIAMOND'),('2025-04-10 23:37:09.000000',63,9,6,NULL,'diamond_guitar_실리카겔-Tik_Tak_Tok.json','diamond_guitar_실리카겔-Tik_Tak_Tok_ex.wav','diamond_guitar_실리카겔-Tik_Tak_Tok.wav','GUITAR','DIAMOND'),('2025-04-10 23:37:09.000000',64,26,19,NULL,'silver_vocals_카더가든-가까운 듯 먼 그대여.json','silver_vocals_카더가든-가까운 듯 먼 그대여_ex.wav','silver_vocals_카더가든-가까운 듯 먼 그대여.wav','VOCAL','BRONZE'),('2025-04-10 23:37:09.000000',65,5,19,NULL,'silver_vocals_카더가든-꿈을 꿨어요.json','silver_vocals_카더가든-꿈을 꿨어요_ex.wav','silver_vocals_카더가든-꿈을 꿨어요.wav','VOCAL','BRONZE'),('2025-04-10 23:37:09.000000',66,14,11,NULL,'gold_vocals_송골매-어쩌다_마주친_그대.json','gold_vocals_송골매-어쩌다_마주친_그대_ex.wav','gold_vocals_송골매-어쩌다_마주친_그대.wav','VOCAL','BRONZE'),('2025-04-10 23:37:09.000000',67,15,11,NULL,'gold_vocals_쏜애플-시퍼런 봄.json','gold_vocals_쏜애플-시퍼런 봄_ex.wav','gold_vocals_쏜애플-시퍼런 봄.wav','VOCAL','BRONZE'),('2025-04-10 23:37:09.000000',68,3,11,NULL,'gold_vocals_윤도현-나는 나비.json','gold_vocals_윤도현-나는 나비_ex.wav','gold_vocals_윤도현-나는 나비.wav','VOCAL','BRONZE'),('2025-04-10 23:37:09.000000',69,1,19,NULL,'silver_vocals_Cranberries-Zombie.json','silver_vocals_Cranberries-Zombie_ex.wav','silver_vocals_Cranberries-Zombie.wav','VOCAL','SILVER'),('2025-04-10 23:37:09.000000',70,13,19,NULL,'silver_vocals_루시-아니_근데_진짜.json','silver_vocals_루시-아니_근데_진짜_ex.wav','silver_vocals_루시-아니_근데_진짜.wav','VOCAL','SILVER'),('2025-04-10 23:37:09.000000',71,2,19,NULL,'silver_vocals_Day6-HAPPY.json','silver_vocals_Day6-HAPPY_ex.wav','silver_vocals_Day6-HAPPY.wav','VOCAL','SILVER'),('2025-04-10 23:37:09.000000',72,6,19,NULL,'silver_vocals_QWER-불꽃놀이.json','silver_vocals_QWER-불꽃놀이_ex.wav','silver_vocals_QWER-불꽃놀이.wav','VOCAL','SILVER'),('2025-04-10 23:37:09.000000',73,4,19,NULL,'silver_vocals_전기뱀장어-미로.json','silver_vocals_전기뱀장어-미로_ex.wav','silver_vocals_전기뱀장어-미로.wav','VOCAL','SILVER'),('2025-04-10 23:37:09.000000',74,10,11,NULL,'gold_vocals_Day6-한_페이지가_될_수_있게.json','gold_vocals_Day6-한_페이지가_될_수_있게_ex.wav','gold_vocals_Day6-한_페이지가_될_수_있게.wav','VOCAL','GOLD'),('2025-04-10 23:37:09.000000',75,11,11,NULL,'gold_vocals_GreenDay-American_Idiot.json','gold_vocals_GreenDay-American_Idiot_ex.wav','gold_vocals_GreenDay-American_Idiot.wav','VOCAL','GOLD'),('2025-04-10 23:37:09.000000',76,12,11,NULL,'gold_vocals_검정치마-Antifreeze.json','gold_vocals_검정치마-Antifreeze_ex.wav','gold_vocals_검정치마-Antifreeze.wav','VOCAL','GOLD'),('2025-04-10 23:37:09.000000',77,16,11,NULL,'gold_vocals_적재-Runaway.json','gold_vocals_적재-Runaway_ex.wav','gold_vocals_적재-Runaway.wav','VOCAL','GOLD'),('2025-04-10 23:37:09.000000',78,18,11,NULL,'gold_vocals_한로로-먹이사슬.json','gold_vocals_한로로-먹이사슬_ex.wav','gold_vocals_한로로-먹이사슬.wav','VOCAL','GOLD'),('2025-04-10 23:37:09.000000',79,20,15,NULL,'platinum_vocals_새소년-파도.json','platinum_vocals_새소년-파도_ex.wav','platinum_vocals_새소년-파도.wav','VOCAL','PLATINUM'),('2025-04-10 23:37:09.000000',80,21,15,NULL,'platinum_vocals_실리카겔-Desert_Eagle.json','platinum_vocals_실리카겔-Desert_Eagle_ex.wav','platinum_vocals_실리카겔-Desert_Eagle.wav','VOCAL','PLATINUM'),('2025-04-10 23:37:09.000000',81,22,15,NULL,'platinum_vocals_실리카겔-No_Pain.json','platinum_vocals_실리카겔-No_Pain_ex.wav','platinum_vocals_실리카겔-No_Pain.wav','VOCAL','PLATINUM'),('2025-04-10 23:37:09.000000',82,23,15,NULL,'platinum_vocals_쏜애플-멸종.json','platinum_vocals_쏜애플-멸종_ex.wav','platinum_vocals_쏜애플-멸종.wav','VOCAL','PLATINUM'),('2025-04-10 23:37:09.000000',83,24,15,NULL,'platinum_vocals_아이유-있잖아.json','platinum_vocals_아이유-있잖아_ex.wav','platinum_vocals_아이유-있잖아.wav','VOCAL','PLATINUM'),('2025-04-10 23:37:09.000000',84,8,7,NULL,'diamond_vocals_국카스텐-거울.json','diamond_vocals_국카스텐-거울_ex.wav','diamond_vocals_국카스텐-거울.wav','VOCAL','DIAMOND'),('2025-04-10 23:37:09.000000',85,9,7,NULL,'diamond_vocals_실리카겔-Tik_Tak_Tok.json','diamond_vocals_실리카겔-Tik_Tak_Tok_ex.wav','diamond_vocals_실리카겔-Tik_Tak_Tok.wav','VOCAL','DIAMOND');
/*!40000 ALTER TABLE `song_by_instrument` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `song_instrument_pack`
--

DROP TABLE IF EXISTS `song_instrument_pack`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `song_instrument_pack` (
  `created_at` datetime(6) NOT NULL COMMENT '생성된 날자',
  `song_instrument_pack_id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `pack_name` varchar(255) NOT NULL,
  `song_pack_instrument` enum('BASS','DRUM','GUITAR','VOCAL') NOT NULL,
  `song_pack_tier` enum('BRONZE','DIAMOND','GOLD','IRON','PLATINUM','SILVER','UNRANKED') NOT NULL,
  PRIMARY KEY (`song_instrument_pack_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `song_instrument_pack`
--

LOCK TABLES `song_instrument_pack` WRITE;
/*!40000 ALTER TABLE `song_instrument_pack` DISABLE KEYS */;
INSERT INTO `song_instrument_pack` VALUES ('2025-04-10 23:37:09.000000',1,NULL,'BASS_BRONZE','BASS','BRONZE'),('2025-04-10 23:37:09.000000',2,NULL,'DRUM_BRONZE','DRUM','BRONZE'),('2025-04-10 23:37:09.000000',3,NULL,'GUITAR_BRONZE','GUITAR','BRONZE'),('2025-04-10 23:37:09.000000',4,NULL,'BASS_DIAMOND','BASS','DIAMOND'),('2025-04-10 23:37:09.000000',5,NULL,'DRUM_DIAMOND','DRUM','DIAMOND'),('2025-04-10 23:37:09.000000',6,NULL,'GUITAR_DIAMOND','GUITAR','DIAMOND'),('2025-04-10 23:37:09.000000',7,NULL,'VOCAL_DIAMOND','VOCAL','DIAMOND'),('2025-04-10 23:37:09.000000',8,NULL,'BASS_GOLD','BASS','GOLD'),('2025-04-10 23:37:09.000000',9,NULL,'DRUM_GOLD','DRUM','GOLD'),('2025-04-10 23:37:09.000000',10,NULL,'GUITAR_GOLD','GUITAR','GOLD'),('2025-04-10 23:37:09.000000',11,NULL,'VOCAL_GOLD','VOCAL','GOLD'),('2025-04-10 23:37:09.000000',12,NULL,'BASS_PLATINUM','BASS','PLATINUM'),('2025-04-10 23:37:09.000000',13,NULL,'DRUM_PLATINUM','DRUM','PLATINUM'),('2025-04-10 23:37:09.000000',14,NULL,'GUITAR_PLATINUM','GUITAR','PLATINUM'),('2025-04-10 23:37:09.000000',15,NULL,'VOCAL_PLATINUM','VOCAL','PLATINUM'),('2025-04-10 23:37:09.000000',16,NULL,'BASS_SILVER','BASS','SILVER'),('2025-04-10 23:37:09.000000',17,NULL,'DRUM_SILVER','DRUM','SILVER'),('2025-04-10 23:37:09.000000',18,NULL,'GUITAR_SILVER','GUITAR','SILVER'),('2025-04-10 23:37:09.000000',19,NULL,'VOCAL_SILVER','VOCAL','SILVER');
/*!40000 ALTER TABLE `song_instrument_pack` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-11  7:26:45
