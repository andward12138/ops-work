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
  PRIMARY KEY (`id`),
  KEY `FKpiegtiv1mkj4am4ipxx3d2iqp` (`approver_id`),
  KEY `FK7o4xnr38kq9mgomjjnqi8358u` (`work_order_id`),
  CONSTRAINT `FK7o4xnr38kq9mgomjjnqi8358u` FOREIGN KEY (`work_order_id`) REFERENCES `work_orders` (`id`),
  CONSTRAINT `FKpiegtiv1mkj4am4ipxx3d2iqp` FOREIGN KEY (`approver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_records`
--

LOCK TABLES `approval_records` WRITE;
/*!40000 ALTER TABLE `approval_records` DISABLE KEYS */;
/*!40000 ALTER TABLE `approval_records` ENABLE KEYS */;
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('2025-04-27 13:04:43.654810',1,'2025-04-27 13:04:43.654810','admin@example.com','$2a$10$M0KN1Cajd64H0Z/7pcnq8uqzI9L/Ir81Pe3Qqm9Td27TUWMiXgbtK',NULL,'admin','ADMIN'),('2025-04-27 13:04:43.754243',2,'2025-04-27 13:04:43.754243','operator@example.com','$2a$10$tiYqe5ERSAY9YqOZEEIinuFt9HXSUz5RmsmkLxpKupGa3FwABDk2W',NULL,'operator','OPERATOR'),('2025-04-27 13:04:43.821243',3,'2025-04-27 13:04:43.821243','manager@example.com','$2a$10$oT5E1lTO/3.qi7mAyevq1uCVf7u4rgXjmlndJT2wHjLt.kn09glrO',NULL,'manager','MANAGER');
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
  PRIMARY KEY (`id`),
  KEY `FKm24kvbbs7q2qem1v1sk8opca0` (`assigned_to`),
  KEY `FKpjx6go2keqx7y8idesxp1kvsd` (`created_by`),
  CONSTRAINT `FKm24kvbbs7q2qem1v1sk8opca0` FOREIGN KEY (`assigned_to`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpjx6go2keqx7y8idesxp1kvsd` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_orders`
--

LOCK TABLES `work_orders` WRITE;
/*!40000 ALTER TABLE `work_orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `work_orders` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-17 11:44:31
