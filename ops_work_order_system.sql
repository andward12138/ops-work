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
  `approver_id` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `work_order_id` int NOT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
  `approved_at` datetime(6) DEFAULT NULL COMMENT '审批时间',
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
  `id` int NOT NULL AUTO_INCREMENT,
  `operator_id` int DEFAULT NULL,
  `work_order_id` int NOT NULL,
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
  `id` int NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `role` enum('ADMIN','MANAGER','OPERATOR','USER') DEFAULT 'USER',
  `department_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  CONSTRAINT `FK_user_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('2025-04-27 13:04:43.654810',1,'2025-04-27 13:04:43.654810','admin@example.com','$2a$10$M0KN1Cajd64H0Z/7pcnq8uqzI9L/Ir81Pe3Qqm9Td27TUWMiXgbtK',NULL,'admin','ADMIN',2),('2025-04-27 13:04:43.754243',2,'2025-04-27 13:04:43.754243','operator@example.com','$2a$10$tiYqe5ERSAY9YqOZEEIinuFt9HXSUz5RmsmkLxpKupGa3FwABDk2W',NULL,'operator','OPERATOR',3),('2025-04-27 13:04:43.821243',3,'2025-04-27 13:04:43.821243','manager@example.com','$2a$10$oT5E1lTO/3.qi7mAyevq1uCVf7u4rgXjmlndJT2wHjLt.kn09glrO',NULL,'manager','MANAGER',3);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_orders`
--

DROP TABLE IF EXISTS `work_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_orders` (
  `assigned_to` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `priority` enum('HIGH','LOW','MEDIUM') NOT NULL,
  `status` enum('APPROVED','COMPLETED','IN_PROGRESS','OVERDUE','PENDING','REJECTED') NOT NULL,
  `type` varchar(100) DEFAULT NULL COMMENT '工单类型',
  `deadline` datetime(6) DEFAULT NULL,
  `is_overdue` boolean DEFAULT FALSE,
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

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `type` enum('HEADQUARTERS','BRANCH','DEPARTMENT','TEAM') DEFAULT 'DEPARTMENT',
  `is_active` boolean DEFAULT TRUE,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_parent_department` (`parent_id`),
  CONSTRAINT `FK_parent_department` FOREIGN KEY (`parent_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `departments`
--

LOCK TABLES `departments` WRITE;
/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` (`name`, `description`, `type`, `created_at`, `updated_at`) VALUES
('总部', '公司总部', 'HEADQUARTERS', NOW(), NOW()),
('技术部', '技术开发部门', 'DEPARTMENT', NOW(), NOW()),
('运维部', '运维管理部门', 'DEPARTMENT', NOW(), NOW()),
('网络组', '网络运维小组', 'TEAM', NOW(), NOW()),
('系统组', '系统运维小组', 'TEAM', NOW(), NOW());
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow_templates`
--

DROP TABLE IF EXISTS `workflow_templates`;
CREATE TABLE `workflow_templates` (
  `id` int NOT NULL AUTO_INCREMENT,
  `template_name` varchar(100) NOT NULL UNIQUE,
  `description` varchar(255) DEFAULT NULL,
  `work_order_type` varchar(100) DEFAULT NULL,
  `is_active` boolean DEFAULT TRUE,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `workflow_templates`
--

LOCK TABLES `workflow_templates` WRITE;
/*!40000 ALTER TABLE `workflow_templates` DISABLE KEYS */;
INSERT INTO `workflow_templates` (`template_name`, `description`, `work_order_type`, `created_at`, `updated_at`) VALUES
('标准运维工单流程', '适用于一般运维工单的标准审批流程', 'OPERATION', NOW(), NOW()),
('紧急变更流程', '适用于紧急变更的快速审批流程', 'EMERGENCY', NOW(), NOW()),
('例行维护流程', '适用于例行维护的简化流程', 'MAINTENANCE', NOW(), NOW());
/*!40000 ALTER TABLE `workflow_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow_template_steps`
--

DROP TABLE IF EXISTS `workflow_template_steps`;
CREATE TABLE `workflow_template_steps` (
  `id` int NOT NULL AUTO_INCREMENT,
  `template_id` int NOT NULL,
  `step_order` int NOT NULL,
  `step_name` varchar(100) NOT NULL,
  `step_type` enum('DEPARTMENT_REVIEW','MANAGER_APPROVAL','DIRECTOR_APPROVAL','EXECUTION','VERIFICATION','COMPLETION') NOT NULL,
  `assignee_role` enum('ADMIN','OPERATOR','MANAGER','USER') DEFAULT NULL,
  `assignee_department_id` int DEFAULT NULL,
  `is_skippable` boolean DEFAULT FALSE,
  `is_parallel` boolean DEFAULT FALSE,
  `parallel_group` int DEFAULT NULL,
  `time_limit_hours` int DEFAULT NULL,
  `auto_approve` boolean DEFAULT FALSE,
  `conditions` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_template_step_template` (`template_id`),
  KEY `FK_template_step_department` (`assignee_department_id`),
  CONSTRAINT `FK_template_step_template` FOREIGN KEY (`template_id`) REFERENCES `workflow_templates` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_template_step_department` FOREIGN KEY (`assignee_department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `workflow_template_steps`
--

LOCK TABLES `workflow_template_steps` WRITE;
/*!40000 ALTER TABLE `workflow_template_steps` DISABLE KEYS */;
SET @template_id = (SELECT id FROM `workflow_templates` WHERE `template_name` = '标准运维工单流程');
INSERT INTO `workflow_template_steps` 
(`template_id`, `step_order`, `step_name`, `step_type`, `assignee_role`, `time_limit_hours`) VALUES
(@template_id, 1, '部门初审', 'DEPARTMENT_REVIEW', 'MANAGER', 24),
(@template_id, 2, '经理审批', 'MANAGER_APPROVAL', 'MANAGER', 48),
(@template_id, 3, '执行操作', 'EXECUTION', 'OPERATOR', 72),
(@template_id, 4, '完成确认', 'COMPLETION', 'USER', 24);
/*!40000 ALTER TABLE `workflow_template_steps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow_steps`
--

DROP TABLE IF EXISTS `workflow_steps`;
CREATE TABLE `workflow_steps` (
  `id` int NOT NULL AUTO_INCREMENT,
  `work_order_id` int NOT NULL,
  `step_order` int NOT NULL,
  `step_name` varchar(100) NOT NULL,
  `step_type` enum('DEPARTMENT_REVIEW','MANAGER_APPROVAL','DIRECTOR_APPROVAL','EXECUTION','VERIFICATION','COMPLETION') NOT NULL,
  `assignee_id` int DEFAULT NULL,
  `assignee_role` enum('ADMIN','OPERATOR','MANAGER','USER') DEFAULT NULL,
  `assignee_department_id` int DEFAULT NULL,
  `status` enum('PENDING','IN_PROGRESS','COMPLETED','REJECTED','SKIPPED') NOT NULL DEFAULT 'PENDING',
  `started_at` datetime(6) DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `deadline` datetime(6) DEFAULT NULL,
  `comments` text DEFAULT NULL,
  `is_skippable` boolean DEFAULT FALSE,
  `is_parallel` boolean DEFAULT FALSE,
  `parallel_group` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_workflow_step_order` (`work_order_id`),
  KEY `FK_workflow_step_assignee` (`assignee_id`),
  KEY `FK_workflow_step_department` (`assignee_department_id`),
  CONSTRAINT `FK_workflow_step_order` FOREIGN KEY (`work_order_id`) REFERENCES `work_orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_workflow_step_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_workflow_step_department` FOREIGN KEY (`assignee_department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `workflow_steps`
--

LOCK TABLES `workflow_steps` WRITE;
/*!40000 ALTER TABLE `workflow_steps` DISABLE KEYS */;
/*!40000 ALTER TABLE `workflow_steps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow_step_assignments`
--

DROP TABLE IF EXISTS `workflow_step_assignments`;
CREATE TABLE `workflow_step_assignments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `workflow_step_id` int NOT NULL,
  `assignee_id` int NOT NULL,
  `status` enum('PENDING','IN_PROGRESS','COMPLETED','REJECTED','SKIPPED') NOT NULL DEFAULT 'PENDING',
  `started_at` datetime(6) DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `comments` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_workflow_step_assignment_workflow_step` (`workflow_step_id`),
  KEY `FK_workflow_step_assignment_assignee` (`assignee_id`),
  CONSTRAINT `FK_workflow_step_assignment_workflow_step` FOREIGN KEY (`workflow_step_id`) REFERENCES `workflow_steps` (`id`),
  CONSTRAINT `FK_workflow_step_assignment_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `workflow_step_assignments`
--

LOCK TABLES `workflow_step_assignments` WRITE;
/*!40000 ALTER TABLE `workflow_step_assignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `workflow_step_assignments` ENABLE KEYS */;
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

-- 新增表和字段更新 (2025-05-31)

-- 添加 work_orders 表的 type 字段（如果不存在）
ALTER TABLE `work_orders` 
ADD COLUMN IF NOT EXISTS `type` varchar(100) DEFAULT NULL COMMENT '工单类型',
ADD COLUMN IF NOT EXISTS `deadline` datetime(6) DEFAULT NULL,
ADD COLUMN IF NOT EXISTS `is_overdue` boolean DEFAULT FALSE;

-- 添加 approval_records 表的 approved_at 字段（如果不存在）
ALTER TABLE `approval_records` 
ADD COLUMN IF NOT EXISTS `approved_at` datetime(6) DEFAULT NULL COMMENT '审批时间';

-- 更新 work_orders 表的 status 枚举，添加 REJECTED 状态
ALTER TABLE `work_orders` 
MODIFY COLUMN `status` enum('APPROVED','COMPLETED','IN_PROGRESS','OVERDUE','PENDING','REJECTED') NOT NULL;

-- 创建部门表（如果不存在）
CREATE TABLE IF NOT EXISTS `departments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `type` enum('HEADQUARTERS','BRANCH','DEPARTMENT','TEAM') DEFAULT 'DEPARTMENT',
  `is_active` boolean DEFAULT TRUE,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_parent_department` (`parent_id`),
  CONSTRAINT `FK_parent_department` FOREIGN KEY (`parent_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 添加 users 表的 department_id 字段（如果不存在）
ALTER TABLE `users` 
ADD COLUMN IF NOT EXISTS `department_id` int DEFAULT NULL;

-- 添加用户和部门的外键关系（如果不存在）
ALTER TABLE `users` 
ADD CONSTRAINT IF NOT EXISTS `FK_user_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`);

-- 创建工作流模板表
CREATE TABLE IF NOT EXISTS `workflow_templates` (
  `id` int NOT NULL AUTO_INCREMENT,
  `template_name` varchar(100) NOT NULL UNIQUE,
  `description` varchar(255) DEFAULT NULL,
  `work_order_type` varchar(100) DEFAULT NULL,
  `is_active` boolean DEFAULT TRUE,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 创建工作流模板步骤表
CREATE TABLE IF NOT EXISTS `workflow_template_steps` (
  `id` int NOT NULL AUTO_INCREMENT,
  `template_id` int NOT NULL,
  `step_order` int NOT NULL,
  `step_name` varchar(100) NOT NULL,
  `step_type` enum('DEPARTMENT_REVIEW','MANAGER_APPROVAL','DIRECTOR_APPROVAL','EXECUTION','VERIFICATION','COMPLETION') NOT NULL,
  `assignee_role` enum('ADMIN','OPERATOR','MANAGER','USER') DEFAULT NULL,
  `assignee_department_id` int DEFAULT NULL,
  `is_skippable` boolean DEFAULT FALSE,
  `is_parallel` boolean DEFAULT FALSE,
  `parallel_group` int DEFAULT NULL,
  `time_limit_hours` int DEFAULT NULL,
  `auto_approve` boolean DEFAULT FALSE,
  `conditions` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_template_step_template` (`template_id`),
  KEY `FK_template_step_department` (`assignee_department_id`),
  CONSTRAINT `FK_template_step_template` FOREIGN KEY (`template_id`) REFERENCES `workflow_templates` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_template_step_department` FOREIGN KEY (`assignee_department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 创建工作流步骤表
CREATE TABLE IF NOT EXISTS `workflow_steps` (
  `id` int NOT NULL AUTO_INCREMENT,
  `work_order_id` int NOT NULL,
  `step_order` int NOT NULL,
  `step_name` varchar(100) NOT NULL,
  `step_type` enum('DEPARTMENT_REVIEW','MANAGER_APPROVAL','DIRECTOR_APPROVAL','EXECUTION','VERIFICATION','COMPLETION') NOT NULL,
  `assignee_id` int DEFAULT NULL,
  `assignee_role` enum('ADMIN','OPERATOR','MANAGER','USER') DEFAULT NULL,
  `assignee_department_id` int DEFAULT NULL,
  `status` enum('PENDING','IN_PROGRESS','COMPLETED','REJECTED','SKIPPED') NOT NULL DEFAULT 'PENDING',
  `started_at` datetime(6) DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `deadline` datetime(6) DEFAULT NULL,
  `comments` text DEFAULT NULL,
  `is_skippable` boolean DEFAULT FALSE,
  `is_parallel` boolean DEFAULT FALSE,
  `parallel_group` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_workflow_step_order` (`work_order_id`),
  KEY `FK_workflow_step_assignee` (`assignee_id`),
  KEY `FK_workflow_step_department` (`assignee_department_id`),
  CONSTRAINT `FK_workflow_step_order` FOREIGN KEY (`work_order_id`) REFERENCES `work_orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_workflow_step_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_workflow_step_department` FOREIGN KEY (`assignee_department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 插入示例部门数据
INSERT INTO `departments` (`name`, `description`, `type`, `created_at`, `updated_at`) VALUES
('总部', '公司总部', 'HEADQUARTERS', NOW(), NOW()),
('技术部', '技术开发部门', 'DEPARTMENT', NOW(), NOW()),
('运维部', '运维管理部门', 'DEPARTMENT', NOW(), NOW()),
('网络组', '网络运维小组', 'TEAM', NOW(), NOW()),
('系统组', '系统运维小组', 'TEAM', NOW(), NOW());

-- 设置部门层级关系
UPDATE `departments` SET `parent_id` = 1 WHERE `name` IN ('技术部', '运维部');
UPDATE `departments` SET `parent_id` = 3 WHERE `name` IN ('网络组', '系统组');

-- 插入示例工作流模板
INSERT INTO `workflow_templates` (`template_name`, `description`, `work_order_type`, `created_at`, `updated_at`) VALUES
('标准运维工单流程', '适用于一般运维工单的标准审批流程', 'OPERATION', NOW(), NOW()),
('紧急变更流程', '适用于紧急变更的快速审批流程', 'EMERGENCY', NOW(), NOW()),
('例行维护流程', '适用于例行维护的简化流程', 'MAINTENANCE', NOW(), NOW());

-- 插入标准运维工单流程的步骤
SET @template_id = (SELECT id FROM `workflow_templates` WHERE `template_name` = '标准运维工单流程');
INSERT INTO `workflow_template_steps` 
(`template_id`, `step_order`, `step_name`, `step_type`, `assignee_role`, `time_limit_hours`) VALUES
(@template_id, 1, '部门初审', 'DEPARTMENT_REVIEW', 'MANAGER', 24),
(@template_id, 2, '经理审批', 'MANAGER_APPROVAL', 'MANAGER', 48),
(@template_id, 3, '执行操作', 'EXECUTION', 'OPERATOR', 72),
(@template_id, 4, '完成确认', 'COMPLETION', 'USER', 24);

-- 更新用户表，添加部门关联（示例）
UPDATE `users` SET `department_id` = 2 WHERE `username` = 'admin';
UPDATE `users` SET `department_id` = 3 WHERE `username` = 'operator';
UPDATE `users` SET `department_id` = 3 WHERE `username` = 'manager';
