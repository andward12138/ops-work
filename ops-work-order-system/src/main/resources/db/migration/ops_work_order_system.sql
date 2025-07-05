/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : ops_work_order_system

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 05/07/2025 11:24:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for approval_records
-- ----------------------------
DROP TABLE IF EXISTS `approval_records`;
CREATE TABLE `approval_records`  (
  `approver_id` bigint NULL DEFAULT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `work_order_id` bigint NOT NULL,
  `comments` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `approved_at` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_approval_records_work_order_created_at`(`work_order_id`, `created_at`) USING BTREE,
  INDEX `idx_approval_records_approver_status`(`approver_id`, `status`) USING BTREE,
  INDEX `idx_approval_records_status_created_at`(`status`, `created_at`) USING BTREE,
  CONSTRAINT `FK7o4xnr38kq9mgomjjnqi8358u` FOREIGN KEY (`work_order_id`) REFERENCES `work_orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKpiegtiv1mkj4am4ipxx3d2iqp` FOREIGN KEY (`approver_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of approval_records
-- ----------------------------
INSERT INTO `approval_records` VALUES (1, '2025-06-04 09:48:29.703573', 1, 1, '1', 'APPROVED', NULL);
INSERT INTO `approval_records` VALUES (1, '2025-06-21 11:36:47.396070', 2, 23, '测试通过', 'APPROVED', NULL);
INSERT INTO `approval_records` VALUES (1, '2025-06-25 15:28:10.846156', 3, 24, '审批测试', 'APPROVED', NULL);

-- ----------------------------
-- Table structure for department_contacts
-- ----------------------------
DROP TABLE IF EXISTS `department_contacts`;
CREATE TABLE `department_contacts`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_active` bit(1) NULL DEFAULT NULL,
  `is_emergency` bit(1) NULL DEFAULT NULL,
  `is_primary` bit(1) NULL DEFAULT NULL,
  `mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `working_hours` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `department_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_department_contacts_dept_active`(`department_id`, `is_active`) USING BTREE,
  INDEX `idx_department_contacts_emergency_active`(`is_emergency`, `is_active`) USING BTREE,
  INDEX `idx_department_contacts_primary`(`is_primary`, `department_id`) USING BTREE,
  CONSTRAINT `FKhr29u6qa6u2rqd74h247ck8bk` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department_contacts
-- ----------------------------

-- ----------------------------
-- Table structure for department_permissions
-- ----------------------------
DROP TABLE IF EXISTS `department_permissions`;
CREATE TABLE `department_permissions`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `expires_at` datetime(6) NULL DEFAULT NULL,
  `granted_at` datetime(6) NULL DEFAULT NULL,
  `granted_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_active` bit(1) NULL DEFAULT NULL,
  `permission_type` enum('APPROVE_WORK_ORDER','ASSIGN_WORK_ORDER','CONFIG_WORKFLOW','CREATE_WORK_ORDER','DELETE_WORK_ORDER','EDIT_WORK_ORDER','EXPORT_REPORTS','MANAGE_CONTACTS','MANAGE_DEPARTMENT','MANAGE_USERS','REASSIGN_WORK_ORDER','REJECT_WORK_ORDER','SYSTEM_ADMIN','VIEW_DEPARTMENT','VIEW_REPORTS','VIEW_USERS','VIEW_WORK_ORDER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `department_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK24cmpeuqvbqprsvsdfqif5p20`(`department_id`, `permission_type`) USING BTREE,
  INDEX `idx_department_permissions_dept_type`(`department_id`, `permission_type`) USING BTREE,
  INDEX `idx_department_permissions_type_active`(`permission_type`, `is_active`) USING BTREE,
  CONSTRAINT `FKnqxfum7b4w8d8t2cfr27gn7tt` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department_permissions
-- ----------------------------

-- ----------------------------
-- Table structure for departments
-- ----------------------------
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact_person` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_active` bit(1) NULL DEFAULT NULL,
  `level` int NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` enum('BUSINESS','COUNTY','MUNICIPAL','OPERATIONAL','PROVINCIAL','SUPPORT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `parent_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKl7tivi5261wxdnvo6cct9gg6t`(`code`) USING BTREE,
  INDEX `idx_departments_parent_active`(`parent_id`, `is_active`) USING BTREE,
  INDEX `idx_departments_type_active`(`type`, `is_active`) USING BTREE,
  INDEX `idx_departments_level`(`level`) USING BTREE,
  CONSTRAINT `FK63q917a0aq92i7gcw6h7f1jrv` FOREIGN KEY (`parent_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of departments
-- ----------------------------
INSERT INTO `departments` VALUES (1, NULL, NULL, NULL, '2025-05-31 04:51:07.113010', '公司总部', b'1', NULL, '总部', 'PROVINCIAL', '2025-05-31 04:51:07.113010', NULL);
INSERT INTO `departments` VALUES (2, NULL, NULL, NULL, '2025-05-31 04:51:07.154773', '技术开发部门', b'1', NULL, '技术部', 'SUPPORT', '2025-06-01 07:36:44.813005', 1);
INSERT INTO `departments` VALUES (3, NULL, NULL, NULL, '2025-05-31 04:51:07.166489', '运维管理部门', b'1', NULL, '运维部', 'OPERATIONAL', '2025-05-31 04:51:07.166489', 1);
INSERT INTO `departments` VALUES (4, NULL, NULL, NULL, '2025-05-31 04:51:07.177187', '网络运维小组', b'1', NULL, '网络组', 'OPERATIONAL', '2025-06-01 07:36:38.935236', 3);
INSERT INTO `departments` VALUES (5, NULL, NULL, NULL, '2025-05-31 04:51:07.186386', '系统运维小组', b'1', NULL, '系统组', 'OPERATIONAL', '2025-06-01 07:36:34.782819', 3);
INSERT INTO `departments` VALUES (6, '001', NULL, NULL, '2025-06-01 07:36:17.789048', '测试部门', b'1', 1, '测试部门', 'PROVINCIAL', '2025-06-03 15:13:27.760689', NULL);
INSERT INTO `departments` VALUES (7, 'PROV101', 'Manager Li', '0371-12345678', '2025-06-03 23:12:29.000000', 'HeNan Power Company HQ', b'1', 1, 'HeNan Power Company', 'PROVINCIAL', NULL, NULL);
INSERT INTO `departments` VALUES (8, 'PROV102', 'Director Wang', '0371-12345679', '2025-06-03 23:12:29.000000', 'Provincial Dispatch Center', b'1', 1, 'HeNan Power Dispatch Center', 'OPERATIONAL', NULL, NULL);
INSERT INTO `departments` VALUES (9, 'PROV103', 'Director Zhang', '0371-12345680', '2025-06-03 23:12:29.000000', 'Provincial IT Center', b'1', 1, 'HeNan Power Information Center', 'SUPPORT', NULL, NULL);
INSERT INTO `departments` VALUES (10, 'ZZ101', 'Manager Chen', '0371-22345678', '2025-06-03 23:12:29.000000', 'Zhengzhou Power Supply', b'1', 2, 'Zhengzhou Power Supply Company', 'MUNICIPAL', NULL, 7);
INSERT INTO `departments` VALUES (11, 'LY101', 'Manager Liu', '0379-22345678', '2025-06-03 23:12:29.000000', 'Luoyang Power Supply', b'1', 2, 'Luoyang Power Supply Company', 'MUNICIPAL', NULL, 7);
INSERT INTO `departments` VALUES (12, 'KF101', 'Manager Zhao', '0378-22345678', '2025-06-03 23:12:29.000000', 'Kaifeng Power Supply', b'1', 2, 'Kaifeng Power Supply Company', 'MUNICIPAL', NULL, 7);
INSERT INTO `departments` VALUES (13, 'ZZ102', 'Dispatcher Wu', '0371-22345679', '2025-06-03 23:12:29.000000', 'Zhengzhou Dispatch', b'1', 2, 'Zhengzhou Dispatch Sub-center', 'OPERATIONAL', NULL, 8);
INSERT INTO `departments` VALUES (14, 'ZZ103', 'Supervisor Sun', '0371-22345680', '2025-06-03 23:12:29.000000', 'Zhengzhou IT', b'1', 2, 'Zhengzhou Information Sub-center', 'SUPPORT', NULL, 9);
INSERT INTO `departments` VALUES (15, 'ZY101', 'Chief Ma', '0371-32345678', '2025-06-03 23:12:29.000000', 'Zhongyuan District', b'1', 3, 'Zhongyuan District Power Station', 'COUNTY', NULL, 10);
INSERT INTO `departments` VALUES (16, 'JS101', 'Chief Tian', '0371-32345679', '2025-06-03 23:12:29.000000', 'Jinshui District', b'1', 3, 'Jinshui District Power Station', 'COUNTY', NULL, 10);
INSERT INTO `departments` VALUES (17, 'EQ101', 'Chief He', '0371-32345680', '2025-06-03 23:12:29.000000', 'Erqi District', b'1', 3, 'Erqi District Power Station', 'COUNTY', NULL, 10);
INSERT INTO `departments` VALUES (18, 'LL101', 'Chief Hu', '0379-32345678', '2025-06-03 23:12:29.000000', 'Luolong District', b'1', 3, 'Luolong District Power Station', 'COUNTY', NULL, 11);
INSERT INTO `departments` VALUES (19, 'XG101', 'Chief Guo', '0379-32345679', '2025-06-03 23:12:29.000000', 'Xigong District', b'1', 3, 'Xigong District Power Station', 'COUNTY', NULL, 11);
INSERT INTO `departments` VALUES (20, 'LT101', 'Chief Zhu', '0378-32345678', '2025-06-03 23:12:29.000000', 'Longting District', b'0', 3, 'Longting District Power Station', 'COUNTY', '2025-06-19 13:03:58.659052', 12);

-- ----------------------------
-- Table structure for device_tokens
-- ----------------------------
DROP TABLE IF EXISTS `device_tokens`;
CREATE TABLE `device_tokens`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `device_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `device_type` enum('ANDROID','IOS','WEB') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `fcm_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_active` bit(1) NOT NULL,
  `last_used_at` datetime(6) NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_device_tokens_user_active`(`user_id`, `is_active`) USING BTREE,
  INDEX `idx_device_tokens_type_active`(`device_type`, `is_active`) USING BTREE,
  INDEX `idx_device_tokens_last_used`(`last_used_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_tokens
-- ----------------------------

-- ----------------------------
-- Table structure for operation_records
-- ----------------------------
DROP TABLE IF EXISTS `operation_records`;
CREATE TABLE `operation_records`  (
  `created_at` datetime(6) NULL DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operator_id` bigint NULL DEFAULT NULL,
  `work_order_id` bigint NOT NULL,
  `comments` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `action` enum('COMPLETION','EXECUTION','HANDOVER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operation_records_work_order_created_at`(`work_order_id`, `created_at`) USING BTREE,
  INDEX `idx_operation_records_operator_action`(`operator_id`, `action`) USING BTREE,
  INDEX `idx_operation_records_action_created_at`(`action`, `created_at`) USING BTREE,
  CONSTRAINT `FK2a953ucfoddmv1otak600kj9s` FOREIGN KEY (`work_order_id`) REFERENCES `work_orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK9qqfdmgem5fpl6dm51uwk3wnt` FOREIGN KEY (`operator_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_records
-- ----------------------------
INSERT INTO `operation_records` VALUES ('2025-06-21 11:37:28.649667', 1, 1, 23, '完成需求', 'EXECUTION');
INSERT INTO `operation_records` VALUES ('2025-06-21 11:37:51.138047', 2, 1, 23, '完成需求', 'COMPLETION');
INSERT INTO `operation_records` VALUES ('2025-06-25 15:28:24.485528', 3, 1, 24, '执行测试1\n', 'EXECUTION');
INSERT INTO `operation_records` VALUES ('2025-06-25 15:28:32.136231', 4, 1, 24, '执行测试2\n', 'EXECUTION');
INSERT INTO `operation_records` VALUES ('2025-06-25 15:30:23.536111', 5, 1, 24, '完成测试', 'COMPLETION');

-- ----------------------------
-- Table structure for transfer_records
-- ----------------------------
DROP TABLE IF EXISTS `transfer_records`;
CREATE TABLE `transfer_records`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `accepted_at` datetime(6) NULL DEFAULT NULL,
  `completed_at` datetime(6) NULL DEFAULT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `is_assistance` bit(1) NULL DEFAULT NULL,
  `requested_at` datetime(6) NULL DEFAULT NULL,
  `status` enum('ACCEPTED','CANCELLED','COMPLETED','PENDING','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `transfer_comments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `transfer_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `transfer_type` enum('ASSISTANCE_REQUEST','DEPARTMENT_TRANSFER','USER_TRANSFER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `accepted_by` int NULL DEFAULT NULL,
  `from_department_id` int NULL DEFAULT NULL,
  `from_user_id` int NULL DEFAULT NULL,
  `requested_by` int NOT NULL,
  `to_department_id` int NULL DEFAULT NULL,
  `to_user_id` int NULL DEFAULT NULL,
  `work_order_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKr1cjo15dxu65y2a1udwy8ukla`(`to_department_id`) USING BTREE,
  INDEX `idx_transfer_records_status`(`status`) USING BTREE,
  INDEX `idx_transfer_records_status_created_at`(`status`, `created_at`) USING BTREE,
  INDEX `idx_transfer_records_work_order_created_at`(`work_order_id`, `created_at`) USING BTREE,
  INDEX `idx_transfer_records_requested_by_status`(`requested_by`, `status`) USING BTREE,
  INDEX `idx_transfer_records_from_to_department`(`from_department_id`, `to_department_id`) USING BTREE,
  INDEX `idx_transfer_records_type_status`(`transfer_type`, `status`) USING BTREE,
  INDEX `idx_transfer_records_accepted_by`(`accepted_by`) USING BTREE,
  CONSTRAINT `FKnbdhv5e237uf38h497tuemn0f` FOREIGN KEY (`from_department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKr1cjo15dxu65y2a1udwy8ukla` FOREIGN KEY (`to_department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transfer_records
-- ----------------------------
INSERT INTO `transfer_records` VALUES (1, NULL, NULL, '2025-06-03 15:18:41.270736', b'0', '2025-06-03 15:18:41.270736', 'PENDING', NULL, '1111', 'USER_TRANSFER', '2025-06-03 15:18:41.270736', NULL, 9, 18, 1, 7, 4, 1);
INSERT INTO `transfer_records` VALUES (2, NULL, NULL, '2025-06-21 11:41:00.712926', b'0', '2025-06-21 11:41:00.712926', 'PENDING', NULL, '测试转派', 'USER_TRANSFER', '2025-06-21 11:41:00.712926', NULL, 10, 7, 1, NULL, 3, 22);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `created_at` datetime(6) NULL DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` enum('ADMIN','MANAGER','OPERATOR') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `department_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKr43af9ap4edm43mmtq01oddj6`(`username`) USING BTREE,
  INDEX `idx_users_department_role`(`department_id`, `role`) USING BTREE,
  INDEX `idx_users_role`(`role`) USING BTREE,
  INDEX `idx_users_email`(`email`) USING BTREE,
  CONSTRAINT `FKsbg59w8q63i0oo53rlgvlcnjq` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('2025-04-27 13:04:43.654810', 1, '2025-06-19 13:03:32.818861', 'admin@example.com', '$2a$10$0sLGjnvCigklE2ieqL3SbOXcwOLOOap4A3rkcot7pfspGFYoM9r8m', '', 'admin', 'ADMIN', NULL);
INSERT INTO `users` VALUES ('2025-04-27 13:04:43.754243', 2, '2025-04-27 13:04:43.754243', 'operator@example.com', '$2a$10$tiYqe5ERSAY9YqOZEEIinuFt9HXSUz5RmsmkLxpKupGa3FwABDk2W', NULL, 'operator', 'OPERATOR', NULL);
INSERT INTO `users` VALUES ('2025-04-27 13:04:43.821243', 3, '2025-04-27 13:04:43.821243', 'manager@example.com', '$2a$10$oT5E1lTO/3.qi7mAyevq1uCVf7u4rgXjmlndJT2wHjLt.kn09glrO', NULL, 'manager', 'MANAGER', NULL);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 4, NULL, 'lizong@power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'li_zong', 'ADMIN', 7);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 5, NULL, 'zhangzr@power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'zhang_zr', 'MANAGER', 9);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 6, NULL, 'wangdd@power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'wang_dd', 'MANAGER', 8);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 7, '2025-06-19 13:03:42.883225', 'chenjl@zz.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', '', 'chen_jl', 'OPERATOR', 10);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 8, NULL, 'liuwy@ly.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'liu_wy', 'MANAGER', 11);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 9, NULL, 'zhaohl@kf.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'zhao_hl', 'MANAGER', 12);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 10, NULL, 'wudd@zz.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'wu_dd', 'OPERATOR', 13);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 11, NULL, 'sunzg@zz.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'sun_zg', 'OPERATOR', 14);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 12, NULL, 'masz@zy.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'ma_sz', 'OPERATOR', 15);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 13, NULL, 'tiansz@js.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'tian_sz', 'OPERATOR', 16);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 14, NULL, 'heszl@eq.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'he_szl', 'OPERATOR', 17);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 15, NULL, 'husz@ll.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'hu_sz', 'OPERATOR', 18);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 16, NULL, 'guosz@xg.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'guo_sz', 'OPERATOR', 19);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 17, NULL, 'zhusz@lt.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'zhu_sz', 'OPERATOR', 20);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 18, NULL, 'techzhang@power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'tech_zhang', 'OPERATOR', 9);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 19, NULL, 'techli@zz.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'tech_li', 'OPERATOR', 14);
INSERT INTO `users` VALUES ('2025-06-03 23:12:29.000000', 20, NULL, 'repairchen@js.power.com', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', NULL, 'repair_chen', 'OPERATOR', 16);

-- ----------------------------
-- Table structure for work_orders
-- ----------------------------
DROP TABLE IF EXISTS `work_orders`;
CREATE TABLE `work_orders`  (
  `assigned_to` bigint NULL DEFAULT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `created_by` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `department` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `priority` enum('HIGH','LOW','MEDIUM') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` enum('APPROVED','COMPLETED','IN_PROGRESS','OVERDUE','PENDING') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `deadline` datetime(6) NULL DEFAULT NULL,
  `is_overdue` bit(1) NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_work_orders_status_created_at`(`status`, `created_at`) USING BTREE,
  INDEX `idx_work_orders_status_deadline`(`status`, `deadline`) USING BTREE,
  INDEX `idx_work_orders_assigned_to_status`(`assigned_to`, `status`) USING BTREE,
  INDEX `idx_work_orders_created_by_created_at`(`created_by`, `created_at`) USING BTREE,
  INDEX `idx_work_orders_priority_status`(`priority`, `status`) USING BTREE,
  INDEX `idx_work_orders_overdue_status`(`is_overdue`, `status`) USING BTREE,
  INDEX `idx_work_orders_type_status`(`type`, `status`) USING BTREE,
  INDEX `idx_work_orders_department`(`department`) USING BTREE,
  CONSTRAINT `FKm24kvbbs7q2qem1v1sk8opca0` FOREIGN KEY (`assigned_to`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKpjx6go2keqx7y8idesxp1kvsd` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work_orders
-- ----------------------------
INSERT INTO `work_orders` VALUES (18, '2025-06-01 23:12:29.000000', 12, 1, '2025-06-19 13:12:10.658484', 'Zhongyuan District Power Station', 'Zhongyuan 10kV line tripped, affecting 3000 households', 'Emergency Line Trip', 'HIGH', 'COMPLETED', '2025-06-02 01:12:29.000000', NULL, 'EMERGENCY');
INSERT INTO `work_orders` VALUES (19, '2025-06-02 23:12:29.000000', 13, 2, '2025-06-19 13:12:23.976951', 'Jinshui District Power Station', 'Jinshui substation main transformer protection activated', 'Transformer Protection Action', 'HIGH', 'COMPLETED', '2025-06-03 01:12:29.000000', b'1', 'EMERGENCY');
INSERT INTO `work_orders` VALUES (18, '2025-06-03 22:12:29.000000', 9, 3, '2025-06-18 14:07:12.569564', 'Kaifeng Power Supply Company', 'Kaifeng dispatch system communication interrupted', 'Dispatch System Failure', 'HIGH', 'COMPLETED', '2025-06-04 00:12:29.000000', b'1', 'EMERGENCY');
INSERT INTO `work_orders` VALUES (20, '2025-05-31 23:12:29.000000', 14, 4, '2025-06-01 04:12:29.000000', 'Erqi District Power Station', 'Erqi district distribution box shows high temperature', 'Distribution Box Temperature High', 'MEDIUM', 'COMPLETED', '2025-06-01 04:12:29.000000', NULL, 'INCIDENT');
INSERT INTO `work_orders` VALUES (19, '2025-05-29 23:12:29.000000', 8, 5, '2025-05-30 06:12:29.000000', 'Luoyang Power Supply Company', 'Luoyang SCADA system shows abnormal data', 'SCADA Data Abnormal', 'MEDIUM', 'COMPLETED', '2025-05-30 06:12:29.000000', NULL, 'INCIDENT');
INSERT INTO `work_orders` VALUES (18, '2025-06-02 23:12:29.000000', 12, 6, '2025-06-04 06:41:36.506971', 'Zhongyuan District Power Station', 'Zhongyuan street lights not working', 'Street Lighting Failure', 'MEDIUM', 'OVERDUE', '2025-06-04 06:12:29.000000', b'1', 'INCIDENT');
INSERT INTO `work_orders` VALUES (19, '2025-05-24 23:12:29.000000', 13, 7, '2025-05-27 21:12:29.000000', 'Jinshui District Power Station', 'Jinshui 35kV substation routine maintenance', 'Substation Spring Maintenance', 'LOW', 'COMPLETED', '2025-05-27 21:12:29.000000', NULL, 'MAINTENANCE');
INSERT INTO `work_orders` VALUES (18, '2025-05-27 23:12:29.000000', 6, 8, '2025-05-31 00:12:29.000000', 'HeNan Power Dispatch Center', 'Provincial dispatch center AC maintenance', 'AC System Maintenance', 'LOW', 'COMPLETED', '2025-05-31 00:12:29.000000', NULL, 'MAINTENANCE');
INSERT INTO `work_orders` VALUES (16, '2025-05-31 23:12:29.000000', 16, 9, '2025-06-09 02:41:30.947847', 'Luolong District Power Station', 'Luolong 10kV line insulator cleaning', 'Line Insulator Cleaning', 'LOW', 'OVERDUE', '2025-06-06 20:12:29.000000', b'1', 'MAINTENANCE');
INSERT INTO `work_orders` VALUES (19, '2025-05-19 23:12:29.000000', 7, 10, '2025-06-10 12:42:26.733551', 'Zhengzhou Information Sub-center', 'Zhengzhou distribution automation upgrade', 'Automation System Upgrade', 'LOW', 'OVERDUE', '2025-06-10 08:12:29.000000', b'1', 'REQUEST');
INSERT INTO `work_orders` VALUES (5, '2025-05-27 23:12:29.000000', 6, 11, '2025-06-18 03:27:45.259245', 'HeNan Power Information Center', 'Provincial dispatch screen system upgrade', 'Display Screen Reform', 'LOW', 'OVERDUE', '2025-06-10 16:12:29.000000', b'1', 'REQUEST');
INSERT INTO `work_orders` VALUES (8, '2025-05-31 23:12:29.000000', 8, 12, '2025-06-18 03:27:45.301246', 'Luoyang Power Supply Company', 'Luoyang smart meter data collection optimization', 'Smart Meter Optimization', 'LOW', 'OVERDUE', '2025-06-10 20:12:29.000000', b'1', 'REQUEST');
INSERT INTO `work_orders` VALUES (2, '2025-06-19 13:11:22.679110', 1, 21, '2025-06-19 13:11:55.678348', '总部', '1', '测试工单', 'MEDIUM', 'COMPLETED', '2025-06-23 21:11:00.000000', b'0', 'REQUEST');
INSERT INTO `work_orders` VALUES (7, '2025-06-21 11:35:44.383885', 1, 22, '2025-06-27 03:10:07.562721', '总部', '需求工单测试', '需求工单测试', 'MEDIUM', 'OVERDUE', '2025-06-25 19:35:00.000000', b'1', 'REQUEST');
INSERT INTO `work_orders` VALUES (2, '2025-06-21 11:35:51.101875', 1, 23, '2025-06-21 11:38:11.460895', '总部', '需求工单测试', '需求工单测试', 'MEDIUM', 'COMPLETED', '2025-06-25 19:35:00.000000', b'0', 'REQUEST');
INSERT INTO `work_orders` VALUES (2, '2025-06-25 15:27:47.890404', 1, 24, '2025-06-25 15:31:15.207623', '总部', '测试工单', '测试工单', 'MEDIUM', 'COMPLETED', '2025-06-29 23:27:00.000000', b'0', 'REQUEST');
INSERT INTO `work_orders` VALUES (2, '2025-06-25 16:00:58.587799', 1, 25, '2025-07-05 02:44:17.698770', 'zongbu', 'cs1', 'cs123', 'MEDIUM', 'OVERDUE', '2025-06-30 08:00:00.000000', b'1', 'REQUEST');

-- ----------------------------
-- Table structure for workflow_steps
-- ----------------------------
DROP TABLE IF EXISTS `workflow_steps`;
CREATE TABLE `workflow_steps`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `assignee_role` enum('ADMIN','MANAGER','OPERATOR','USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `comments` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `completed_at` datetime(6) NULL DEFAULT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `deadline` datetime(6) NULL DEFAULT NULL,
  `is_parallel` bit(1) NULL DEFAULT NULL,
  `is_skippable` bit(1) NULL DEFAULT NULL,
  `started_at` datetime(6) NULL DEFAULT NULL,
  `status` enum('COMPLETED','IN_PROGRESS','PENDING','REJECTED','SKIPPED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `step_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `step_order` int NOT NULL,
  `step_type` enum('COMPLETION','DEPARTMENT_REVIEW','DIRECTOR_APPROVAL','EXECUTION','MANAGER_APPROVAL','VERIFICATION') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `assignee_id` int NULL DEFAULT NULL,
  `assignee_department_id` int NULL DEFAULT NULL,
  `work_order_id` int NOT NULL,
  `parallel_group` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_workflow_steps_work_order_step_order`(`work_order_id`, `step_order`) USING BTREE,
  INDEX `idx_workflow_steps_assignee_status`(`assignee_id`, `status`) USING BTREE,
  INDEX `idx_workflow_steps_department_status`(`assignee_department_id`, `status`) USING BTREE,
  INDEX `idx_workflow_steps_status_created_at`(`status`, `created_at`) USING BTREE,
  INDEX `idx_workflow_steps_type_status`(`step_type`, `status`) USING BTREE,
  CONSTRAINT `FKedr992p21uxdxxyswa8mu67ip` FOREIGN KEY (`assignee_department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of workflow_steps
-- ----------------------------

-- ----------------------------
-- Table structure for workflow_template_steps
-- ----------------------------
DROP TABLE IF EXISTS `workflow_template_steps`;
CREATE TABLE `workflow_template_steps`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `assignee_role` enum('ADMIN','MANAGER','OPERATOR','USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `auto_approve` bit(1) NULL DEFAULT NULL,
  `conditions` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_parallel` bit(1) NULL DEFAULT NULL,
  `is_skippable` bit(1) NULL DEFAULT NULL,
  `parallel_group` int NULL DEFAULT NULL,
  `step_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `step_order` int NOT NULL,
  `step_type` enum('COMPLETION','DEPARTMENT_REVIEW','DIRECTOR_APPROVAL','EXECUTION','MANAGER_APPROVAL','VERIFICATION') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `time_limit_hours` int NULL DEFAULT NULL,
  `assignee_department_id` int NULL DEFAULT NULL,
  `template_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_workflow_template_steps_template_order`(`template_id`, `step_order`) USING BTREE,
  INDEX `idx_workflow_template_steps_dept_type`(`assignee_department_id`, `step_type`) USING BTREE,
  CONSTRAINT `FKbpnw31s5h98f2i5ri92pkvtbt` FOREIGN KEY (`assignee_department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKhko2gh77u9nvf62xglbdfeu7d` FOREIGN KEY (`template_id`) REFERENCES `workflow_templates` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of workflow_template_steps
-- ----------------------------
INSERT INTO `workflow_template_steps` VALUES (1, 'MANAGER', b'0', NULL, b'0', b'0', NULL, '部门初审', 1, 'DEPARTMENT_REVIEW', 24, 3, 1);
INSERT INTO `workflow_template_steps` VALUES (2, 'MANAGER', b'0', NULL, b'0', b'0', NULL, '经理审批', 2, 'MANAGER_APPROVAL', 48, 3, 1);
INSERT INTO `workflow_template_steps` VALUES (3, 'OPERATOR', b'0', NULL, b'0', b'0', NULL, '执行操作', 3, 'EXECUTION', 72, 3, 1);
INSERT INTO `workflow_template_steps` VALUES (4, 'USER', b'0', NULL, b'0', b'0', NULL, '完成确认', 4, 'COMPLETION', 24, NULL, 1);
INSERT INTO `workflow_template_steps` VALUES (5, 'MANAGER', b'1', NULL, b'0', b'0', NULL, '经理快速审批', 1, 'MANAGER_APPROVAL', 2, 3, 2);
INSERT INTO `workflow_template_steps` VALUES (6, 'OPERATOR', b'0', NULL, b'0', b'0', NULL, '紧急执行', 2, 'EXECUTION', 4, 3, 2);
INSERT INTO `workflow_template_steps` VALUES (7, 'OPERATOR', b'0', NULL, b'0', b'1', NULL, '维护审核', 1, 'DEPARTMENT_REVIEW', 12, 3, 3);
INSERT INTO `workflow_template_steps` VALUES (8, 'OPERATOR', b'0', NULL, b'0', b'0', NULL, '执行维护', 2, 'EXECUTION', 24, 3, 3);
INSERT INTO `workflow_template_steps` VALUES (9, 'OPERATOR', b'0', NULL, b'0', b'0', NULL, '故障分析', 1, 'DEPARTMENT_REVIEW', 1, 3, 4);
INSERT INTO `workflow_template_steps` VALUES (10, 'OPERATOR', b'0', NULL, b'0', b'0', NULL, '应急修复', 2, 'EXECUTION', 2, 3, 4);
INSERT INTO `workflow_template_steps` VALUES (11, 'MANAGER', b'0', NULL, b'0', b'0', NULL, '验证确认', 3, 'VERIFICATION', 1, 3, 4);
INSERT INTO `workflow_template_steps` VALUES (14, 'MANAGER', b'0', NULL, b'0', b'0', NULL, '1', 1, 'DEPARTMENT_REVIEW', 1, NULL, 6);

-- ----------------------------
-- Table structure for workflow_templates
-- ----------------------------
DROP TABLE IF EXISTS `workflow_templates`;
CREATE TABLE `workflow_templates`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_active` bit(1) NULL DEFAULT NULL,
  `template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `work_order_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK6ohawur72g565ytlvbqt9111a`(`template_name`) USING BTREE,
  INDEX `idx_workflow_templates_type_active`(`work_order_type`, `is_active`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of workflow_templates
-- ----------------------------
INSERT INTO `workflow_templates` VALUES (1, '2025-06-28 06:24:27.954887', '适用于功能需求和配置变更的标准审批流程', b'1', '标准需求工单流程', '2025-06-28 06:24:27.954887', 'REQUEST');
INSERT INTO `workflow_templates` VALUES (2, '2025-06-28 06:24:27.988181', '适用于紧急变更的快速审批流程', b'1', '紧急变更流程', '2025-06-28 06:24:27.988181', 'EMERGENCY');
INSERT INTO `workflow_templates` VALUES (3, '2025-06-28 06:24:27.995181', '适用于例行维护的简化流程', b'1', '例行维护流程', '2025-06-28 06:24:27.995181', 'MAINTENANCE');
INSERT INTO `workflow_templates` VALUES (4, '2025-06-28 06:24:28.001180', '适用于系统故障和服务中断的快速处理流程', b'1', '故障处理流程', '2025-06-28 06:38:56.486728', 'INCIDENT');
INSERT INTO `workflow_templates` VALUES (6, '2025-06-28 06:42:42.959806', '111', b'0', '需求测试', '2025-06-28 06:42:47.825092', 'REQUEST');

SET FOREIGN_KEY_CHECKS = 1;
