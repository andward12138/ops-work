-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: localhost    Database: ops_work_order_system
-- ------------------------------------------------------
-- Server version	8.0.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `approval_records`
--

DROP TABLE IF EXISTS `approval_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_records` (
  `approver_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `work_order_id` bigint NOT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
  `approved_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpiegtiv1mkj4am4ipxx3d2iqp` (`approver_id`),
  KEY `FK7o4xnr38kq9mgomjjnqi8358u` (`work_order_id`),
  CONSTRAINT `FK7o4xnr38kq9mgomjjnqi8358u` FOREIGN KEY (`work_order_id`) REFERENCES `work_orders` (`id`),
  CONSTRAINT `FKpiegtiv1mkj4am4ipxx3d2iqp` FOREIGN KEY (`approver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_records`
--

LOCK TABLES `approval_records` WRITE;
/*!40000 ALTER TABLE `approval_records` DISABLE KEYS */;
INSERT INTO `approval_records` VALUES (1,'2025-06-04 09:48:29.703573',1,1,'1','APPROVED',NULL);
/*!40000 ALTER TABLE `approval_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department_contacts`
--

DROP TABLE IF EXISTS `department_contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department_contacts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_emergency` bit(1) DEFAULT NULL,
  `is_primary` bit(1) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `position` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `working_hours` varchar(255) DEFAULT NULL,
  `department_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhr29u6qa6u2rqd74h247ck8bk` (`department_id`),
  CONSTRAINT `FKhr29u6qa6u2rqd74h247ck8bk` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department_contacts`
--

LOCK TABLES `department_contacts` WRITE;
/*!40000 ALTER TABLE `department_contacts` DISABLE KEYS */;
/*!40000 ALTER TABLE `department_contacts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department_permissions`
--

DROP TABLE IF EXISTS `department_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department_permissions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `expires_at` datetime(6) DEFAULT NULL,
  `granted_at` datetime(6) DEFAULT NULL,
  `granted_by` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `permission_type` enum('APPROVE_WORK_ORDER','ASSIGN_WORK_ORDER','CONFIG_WORKFLOW','CREATE_WORK_ORDER','DELETE_WORK_ORDER','EDIT_WORK_ORDER','EXPORT_REPORTS','MANAGE_CONTACTS','MANAGE_DEPARTMENT','MANAGE_USERS','REASSIGN_WORK_ORDER','REJECT_WORK_ORDER','SYSTEM_ADMIN','VIEW_DEPARTMENT','VIEW_REPORTS','VIEW_USERS','VIEW_WORK_ORDER') NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `department_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK24cmpeuqvbqprsvsdfqif5p20` (`department_id`,`permission_type`),
  CONSTRAINT `FKnqxfum7b4w8d8t2cfr27gn7tt` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department_permissions`
--

LOCK TABLES `department_permissions` WRITE;
/*!40000 ALTER TABLE `department_permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `department_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `contact_person` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `level` int DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `type` enum('BUSINESS','COUNTY','MUNICIPAL','OPERATIONAL','PROVINCIAL','SUPPORT') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKl7tivi5261wxdnvo6cct9gg6t` (`code`),
  KEY `FK63q917a0aq92i7gcw6h7f1jrv` (`parent_id`),
  CONSTRAINT `FK63q917a0aq92i7gcw6h7f1jrv` FOREIGN KEY (`parent_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

LOCK TABLES `departments` WRITE;
/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` VALUES (1,NULL,NULL,NULL,'2025-05-31 04:51:07.113010','公司总部',_binary '',NULL,'总部','PROVINCIAL','2025-05-31 04:51:07.113010',NULL),(2,NULL,NULL,NULL,'2025-05-31 04:51:07.154773','技术开发部门',_binary '\0',NULL,'技术部','SUPPORT','2025-06-01 07:36:44.813005',1),(3,NULL,NULL,NULL,'2025-05-31 04:51:07.166489','运维管理部门',_binary '',NULL,'运维部','OPERATIONAL','2025-05-31 04:51:07.166489',1),(4,NULL,NULL,NULL,'2025-05-31 04:51:07.177187','网络运维小组',_binary '\0',NULL,'网络组','OPERATIONAL','2025-06-01 07:36:38.935236',3),(5,NULL,NULL,NULL,'2025-05-31 04:51:07.186386','系统运维小组',_binary '\0',NULL,'系统组','OPERATIONAL','2025-06-01 07:36:34.782819',3),(6,'001',NULL,NULL,'2025-06-01 07:36:17.789048','测试部门',_binary '\0',1,'测试部门','PROVINCIAL','2025-06-03 15:13:27.760689',NULL),(7,'PROV101','Manager Li','0371-12345678','2025-06-03 23:12:29.000000','HeNan Power Company HQ',_binary '',1,'HeNan Power Company','PROVINCIAL',NULL,NULL),(8,'PROV102','Director Wang','0371-12345679','2025-06-03 23:12:29.000000','Provincial Dispatch Center',_binary '',1,'HeNan Power Dispatch Center','OPERATIONAL',NULL,NULL),(9,'PROV103','Director Zhang','0371-12345680','2025-06-03 23:12:29.000000','Provincial IT Center',_binary '',1,'HeNan Power Information Center','SUPPORT',NULL,NULL),(10,'ZZ101','Manager Chen','0371-22345678','2025-06-03 23:12:29.000000','Zhengzhou Power Supply',_binary '',2,'Zhengzhou Power Supply Company','MUNICIPAL',NULL,7),(11,'LY101','Manager Liu','0379-22345678','2025-06-03 23:12:29.000000','Luoyang Power Supply',_binary '',2,'Luoyang Power Supply Company','MUNICIPAL',NULL,7),(12,'KF101','Manager Zhao','0378-22345678','2025-06-03 23:12:29.000000','Kaifeng Power Supply',_binary '',2,'Kaifeng Power Supply Company','MUNICIPAL',NULL,7),(13,'ZZ102','Dispatcher Wu','0371-22345679','2025-06-03 23:12:29.000000','Zhengzhou Dispatch',_binary '',2,'Zhengzhou Dispatch Sub-center','OPERATIONAL',NULL,8),(14,'ZZ103','Supervisor Sun','0371-22345680','2025-06-03 23:12:29.000000','Zhengzhou IT',_binary '',2,'Zhengzhou Information Sub-center','SUPPORT',NULL,9),(15,'ZY101','Chief Ma','0371-32345678','2025-06-03 23:12:29.000000','Zhongyuan District',_binary '',3,'Zhongyuan District Power Station','COUNTY',NULL,10),(16,'JS101','Chief Tian','0371-32345679','2025-06-03 23:12:29.000000','Jinshui District',_binary '',3,'Jinshui District Power Station','COUNTY',NULL,10),(17,'EQ101','Chief He','0371-32345680','2025-06-03 23:12:29.000000','Erqi District',_binary '',3,'Erqi District Power Station','COUNTY',NULL,10),(18,'LL101','Chief Hu','0379-32345678','2025-06-03 23:12:29.000000','Luolong District',_binary '',3,'Luolong District Power Station','COUNTY',NULL,11),(19,'XG101','Chief Guo','0379-32345679','2025-06-03 23:12:29.000000','Xigong District',_binary '',3,'Xigong District Power Station','COUNTY',NULL,11),(20,'LT101','Chief Zhu','0378-32345678','2025-06-03 23:12:29.000000','Longting District',_binary '',3,'Longting District Power Station','COUNTY',NULL,12);
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device_tokens`
--

DROP TABLE IF EXISTS `device_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_tokens` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `device_info` varchar(500) DEFAULT NULL,
  `device_type` enum('ANDROID','IOS','WEB') NOT NULL,
  `fcm_token` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `last_used_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_tokens`
--

LOCK TABLES `device_tokens` WRITE;
/*!40000 ALTER TABLE `device_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `device_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_records`
--

DROP TABLE IF EXISTS `operation_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_records` (
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operator_id` bigint DEFAULT NULL,
  `work_order_id` bigint NOT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `action` enum('COMPLETION','EXECUTION','HANDOVER') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9qqfdmgem5fpl6dm51uwk3wnt` (`operator_id`),
  KEY `FK2a953ucfoddmv1otak600kj9s` (`work_order_id`),
  CONSTRAINT `FK2a953ucfoddmv1otak600kj9s` FOREIGN KEY (`work_order_id`) REFERENCES `work_orders` (`id`),
  CONSTRAINT `FK9qqfdmgem5fpl6dm51uwk3wnt` FOREIGN KEY (`operator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_records`
--

LOCK TABLES `operation_records` WRITE;
/*!40000 ALTER TABLE `operation_records` DISABLE KEYS */;
/*!40000 ALTER TABLE `operation_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfer_records`
--

DROP TABLE IF EXISTS `transfer_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transfer_records` (
  `id` int NOT NULL AUTO_INCREMENT,
  `accepted_at` datetime(6) DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_assistance` bit(1) DEFAULT NULL,
  `requested_at` datetime(6) DEFAULT NULL,
  `status` enum('ACCEPTED','CANCELLED','COMPLETED','PENDING','REJECTED') NOT NULL,
  `transfer_comments` text,
  `transfer_reason` varchar(500) DEFAULT NULL,
  `transfer_type` enum('ASSISTANCE_REQUEST','DEPARTMENT_TRANSFER','USER_TRANSFER') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `accepted_by` int DEFAULT NULL,
  `from_department_id` int DEFAULT NULL,
  `from_user_id` int DEFAULT NULL,
  `requested_by` int NOT NULL,
  `to_department_id` int DEFAULT NULL,
  `to_user_id` int DEFAULT NULL,
  `work_order_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnbdhv5e237uf38h497tuemn0f` (`from_department_id`),
  KEY `FKr1cjo15dxu65y2a1udwy8ukla` (`to_department_id`),
  KEY `idx_transfer_records_status` (`status`),
  KEY `idx_transfer_records_status_created_at` (`status`,`created_at` DESC),
  CONSTRAINT `FKnbdhv5e237uf38h497tuemn0f` FOREIGN KEY (`from_department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `FKr1cjo15dxu65y2a1udwy8ukla` FOREIGN KEY (`to_department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfer_records`
--

LOCK TABLES `transfer_records` WRITE;
/*!40000 ALTER TABLE `transfer_records` DISABLE KEYS */;
INSERT INTO `transfer_records` VALUES (1,NULL,NULL,'2025-06-03 15:18:41.270736',_binary '\0','2025-06-03 15:18:41.270736','PENDING',NULL,'1111','USER_TRANSFER','2025-06-03 15:18:41.270736',NULL,9,18,1,7,4,1);
/*!40000 ALTER TABLE `transfer_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `role` enum('ADMIN','MANAGER','OPERATOR') NOT NULL,
  `department_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FKsbg59w8q63i0oo53rlgvlcnjq` (`department_id`),
  CONSTRAINT `FKsbg59w8q63i0oo53rlgvlcnjq` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('2025-04-27 13:04:43.654810',1,'2025-04-27 13:04:43.654810','admin@example.com','$2a$10$M0KN1Cajd64H0Z/7pcnq8uqzI9L/Ir81Pe3Qqm9Td27TUWMiXgbtK',NULL,'admin','ADMIN',NULL),('2025-04-27 13:04:43.754243',2,'2025-04-27 13:04:43.754243','operator@example.com','$2a$10$tiYqe5ERSAY9YqOZEEIinuFt9HXSUz5RmsmkLxpKupGa3FwABDk2W',NULL,'operator','OPERATOR',NULL),('2025-04-27 13:04:43.821243',3,'2025-04-27 13:04:43.821243','manager@example.com','$2a$10$oT5E1lTO/3.qi7mAyevq1uCVf7u4rgXjmlndJT2wHjLt.kn09glrO',NULL,'manager','MANAGER',NULL),('2025-06-03 23:12:29.000000',4,NULL,'lizong@power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'li_zong','ADMIN',7),('2025-06-03 23:12:29.000000',5,NULL,'zhangzr@power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'zhang_zr','MANAGER',9),('2025-06-03 23:12:29.000000',6,NULL,'wangdd@power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'wang_dd','MANAGER',8),('2025-06-03 23:12:29.000000',7,NULL,'chenjl@zz.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'chen_jl','MANAGER',10),('2025-06-03 23:12:29.000000',8,NULL,'liuwy@ly.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'liu_wy','MANAGER',11),('2025-06-03 23:12:29.000000',9,NULL,'zhaohl@kf.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'zhao_hl','MANAGER',12),('2025-06-03 23:12:29.000000',10,NULL,'wudd@zz.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'wu_dd','OPERATOR',13),('2025-06-03 23:12:29.000000',11,NULL,'sunzg@zz.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'sun_zg','OPERATOR',14),('2025-06-03 23:12:29.000000',12,NULL,'masz@zy.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'ma_sz','OPERATOR',15),('2025-06-03 23:12:29.000000',13,NULL,'tiansz@js.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'tian_sz','OPERATOR',16),('2025-06-03 23:12:29.000000',14,NULL,'heszl@eq.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'he_szl','OPERATOR',17),('2025-06-03 23:12:29.000000',15,NULL,'husz@ll.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'hu_sz','OPERATOR',18),('2025-06-03 23:12:29.000000',16,NULL,'guosz@xg.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'guo_sz','OPERATOR',19),('2025-06-03 23:12:29.000000',17,NULL,'zhusz@lt.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'zhu_sz','OPERATOR',20),('2025-06-03 23:12:29.000000',18,NULL,'techzhang@power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'tech_zhang','OPERATOR',9),('2025-06-03 23:12:29.000000',19,NULL,'techli@zz.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'tech_li','OPERATOR',14),('2025-06-03 23:12:29.000000',20,NULL,'repairchen@js.power.com','$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa',NULL,'repair_chen','OPERATOR',16);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_orders`
--

DROP TABLE IF EXISTS `work_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_orders` (
  `assigned_to` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `priority` enum('HIGH','LOW','MEDIUM') NOT NULL,
  `status` enum('APPROVED','COMPLETED','IN_PROGRESS','OVERDUE','PENDING') NOT NULL,
  `deadline` datetime(6) DEFAULT NULL,
  `is_overdue` bit(1) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKm24kvbbs7q2qem1v1sk8opca0` (`assigned_to`),
  KEY `FKpjx6go2keqx7y8idesxp1kvsd` (`created_by`),
  CONSTRAINT `FKm24kvbbs7q2qem1v1sk8opca0` FOREIGN KEY (`assigned_to`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpjx6go2keqx7y8idesxp1kvsd` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_orders`
--

LOCK TABLES `work_orders` WRITE;
/*!40000 ALTER TABLE `work_orders` DISABLE KEYS */;
INSERT INTO `work_orders` VALUES (18,'2025-06-01 23:12:29.000000',12,1,'2025-06-02 01:12:29.000000','Zhongyuan District Power Station','Zhongyuan 10kV line tripped, affecting 3000 households','Emergency Line Trip','HIGH','COMPLETED','2025-06-02 01:12:29.000000',NULL,'EMERGENCY'),(19,'2025-06-02 23:12:29.000000',13,2,'2025-06-03 01:12:29.000000','Jinshui District Power Station','Jinshui substation main transformer protection activated','Transformer Protection Action','HIGH','COMPLETED','2025-06-03 01:12:29.000000',NULL,'EMERGENCY'),(18,'2025-06-03 22:12:29.000000',9,3,'2025-06-04 03:41:36.654488','Kaifeng Power Supply Company','Kaifeng dispatch system communication interrupted','Dispatch System Failure','HIGH','OVERDUE','2025-06-04 00:12:29.000000',_binary '','EMERGENCY'),(20,'2025-05-31 23:12:29.000000',14,4,'2025-06-01 04:12:29.000000','Erqi District Power Station','Erqi district distribution box shows high temperature','Distribution Box Temperature High','MEDIUM','COMPLETED','2025-06-01 04:12:29.000000',NULL,'INCIDENT'),(19,'2025-05-29 23:12:29.000000',8,5,'2025-05-30 06:12:29.000000','Luoyang Power Supply Company','Luoyang SCADA system shows abnormal data','SCADA Data Abnormal','MEDIUM','COMPLETED','2025-05-30 06:12:29.000000',NULL,'INCIDENT'),(18,'2025-06-02 23:12:29.000000',12,6,'2025-06-04 06:41:36.506971','Zhongyuan District Power Station','Zhongyuan street lights not working','Street Lighting Failure','MEDIUM','OVERDUE','2025-06-04 06:12:29.000000',_binary '','INCIDENT'),(19,'2025-05-24 23:12:29.000000',13,7,'2025-05-27 21:12:29.000000','Jinshui District Power Station','Jinshui 35kV substation routine maintenance','Substation Spring Maintenance','LOW','COMPLETED','2025-05-27 21:12:29.000000',NULL,'MAINTENANCE'),(18,'2025-05-27 23:12:29.000000',6,8,'2025-05-31 00:12:29.000000','HeNan Power Dispatch Center','Provincial dispatch center AC maintenance','AC System Maintenance','LOW','COMPLETED','2025-05-31 00:12:29.000000',NULL,'MAINTENANCE'),(16,'2025-05-31 23:12:29.000000',16,9,'2025-06-09 02:41:30.947847','Luolong District Power Station','Luolong 10kV line insulator cleaning','Line Insulator Cleaning','LOW','OVERDUE','2025-06-06 20:12:29.000000',_binary '','MAINTENANCE'),(19,'2025-05-19 23:12:29.000000',7,10,'2025-06-10 12:42:26.733551','Zhengzhou Information Sub-center','Zhengzhou distribution automation upgrade','Automation System Upgrade','LOW','OVERDUE','2025-06-10 08:12:29.000000',_binary '','REQUEST'),(5,'2025-05-27 23:12:29.000000',6,11,'2025-06-18 03:27:45.259245','HeNan Power Information Center','Provincial dispatch screen system upgrade','Display Screen Reform','LOW','OVERDUE','2025-06-10 16:12:29.000000',_binary '','REQUEST'),(8,'2025-05-31 23:12:29.000000',8,12,'2025-06-18 03:27:45.301246','Luoyang Power Supply Company','Luoyang smart meter data collection optimization','Smart Meter Optimization','LOW','OVERDUE','2025-06-10 20:12:29.000000',_binary '','REQUEST');
/*!40000 ALTER TABLE `work_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow_steps`
--

DROP TABLE IF EXISTS `workflow_steps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workflow_steps` (
  `id` int NOT NULL AUTO_INCREMENT,
  `assignee_role` enum('ADMIN','MANAGER','OPERATOR','USER') DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `deadline` datetime(6) DEFAULT NULL,
  `is_parallel` bit(1) DEFAULT NULL,
  `is_skippable` bit(1) DEFAULT NULL,
  `started_at` datetime(6) DEFAULT NULL,
  `status` enum('COMPLETED','IN_PROGRESS','PENDING','REJECTED','SKIPPED') NOT NULL,
  `step_name` varchar(255) NOT NULL,
  `step_order` int NOT NULL,
  `step_type` enum('COMPLETION','DEPARTMENT_REVIEW','DIRECTOR_APPROVAL','EXECUTION','MANAGER_APPROVAL','VERIFICATION') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `assignee_id` int DEFAULT NULL,
  `assignee_department_id` int DEFAULT NULL,
  `work_order_id` int NOT NULL,
  `parallel_group` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKedr992p21uxdxxyswa8mu67ip` (`assignee_department_id`),
  CONSTRAINT `FKedr992p21uxdxxyswa8mu67ip` FOREIGN KEY (`assignee_department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workflow_steps`
--

LOCK TABLES `workflow_steps` WRITE;
/*!40000 ALTER TABLE `workflow_steps` DISABLE KEYS */;
INSERT INTO `workflow_steps` VALUES (1,NULL,NULL,NULL,'2025-05-24 23:12:29.000000',NULL,NULL,NULL,NULL,'COMPLETED','Department Manager Approval',1,'MANAGER_APPROVAL','2025-05-25 23:12:29.000000',7,10,7,NULL),(2,NULL,NULL,NULL,'2025-05-25 23:12:29.000000',NULL,NULL,NULL,NULL,'COMPLETED','Safety Department Review',2,'DEPARTMENT_REVIEW','2025-05-26 23:12:29.000000',4,1,7,NULL),(3,NULL,NULL,NULL,'2025-05-26 23:12:29.000000',NULL,NULL,NULL,NULL,'COMPLETED','Technical Execution',3,'EXECUTION','2025-05-27 21:12:29.000000',19,16,7,NULL),(4,NULL,NULL,NULL,'2025-05-27 23:12:29.000000',NULL,NULL,NULL,NULL,'COMPLETED','Information Center Approval',1,'DIRECTOR_APPROVAL','2025-05-28 23:12:29.000000',5,9,8,NULL),(5,NULL,NULL,NULL,'2025-05-28 23:12:29.000000',NULL,NULL,NULL,NULL,'COMPLETED','Technical Execution',2,'EXECUTION','2025-05-31 00:12:29.000000',18,9,8,NULL),(6,NULL,NULL,NULL,'2025-05-27 23:12:29.000000',NULL,NULL,NULL,NULL,'PENDING','Information Director Approval',1,'DIRECTOR_APPROVAL',NULL,5,9,11,NULL),(7,NULL,NULL,NULL,'2025-05-27 23:12:29.000000',NULL,NULL,NULL,NULL,'PENDING','General Manager Approval',2,'MANAGER_APPROVAL',NULL,4,7,11,NULL),(8,NULL,NULL,NULL,'2025-05-27 23:12:29.000000',NULL,NULL,NULL,NULL,'PENDING','Technical Implementation',3,'EXECUTION',NULL,18,9,11,NULL);
/*!40000 ALTER TABLE `workflow_steps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow_template_steps`
--

DROP TABLE IF EXISTS `workflow_template_steps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workflow_template_steps` (
  `id` int NOT NULL AUTO_INCREMENT,
  `assignee_role` enum('ADMIN','MANAGER','OPERATOR','USER') DEFAULT NULL,
  `auto_approve` bit(1) DEFAULT NULL,
  `conditions` varchar(255) DEFAULT NULL,
  `is_parallel` bit(1) DEFAULT NULL,
  `is_skippable` bit(1) DEFAULT NULL,
  `parallel_group` int DEFAULT NULL,
  `step_name` varchar(255) NOT NULL,
  `step_order` int NOT NULL,
  `step_type` enum('COMPLETION','DEPARTMENT_REVIEW','DIRECTOR_APPROVAL','EXECUTION','MANAGER_APPROVAL','VERIFICATION') NOT NULL,
  `time_limit_hours` int DEFAULT NULL,
  `assignee_department_id` int DEFAULT NULL,
  `template_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbpnw31s5h98f2i5ri92pkvtbt` (`assignee_department_id`),
  KEY `FKhko2gh77u9nvf62xglbdfeu7d` (`template_id`),
  CONSTRAINT `FKbpnw31s5h98f2i5ri92pkvtbt` FOREIGN KEY (`assignee_department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `FKhko2gh77u9nvf62xglbdfeu7d` FOREIGN KEY (`template_id`) REFERENCES `workflow_templates` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workflow_template_steps`
--

LOCK TABLES `workflow_template_steps` WRITE;
/*!40000 ALTER TABLE `workflow_template_steps` DISABLE KEYS */;
/*!40000 ALTER TABLE `workflow_template_steps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow_templates`
--

DROP TABLE IF EXISTS `workflow_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workflow_templates` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `template_name` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `work_order_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6ohawur72g565ytlvbqt9111a` (`template_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workflow_templates`
--

LOCK TABLES `workflow_templates` WRITE;
/*!40000 ALTER TABLE `workflow_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `workflow_templates` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-18 11:59:41
