-- ====================================================
-- 运维工单系统数据库初始化脚本（基于实际数据库结构）
-- 版本: 2.0
-- 创建时间: 2024
-- 数据库版本: MySQL 8.0.30+
-- 字符集: UTF8MB4
-- 
-- 使用方法：
-- 1. 在Navicat中连接到MySQL服务器
-- 2. 直接执行此SQL文件，或者复制粘贴到查询窗口执行
-- 3. 执行完成后，数据库和所有表结构将自动创建
-- ====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `ops_work_order_system` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_0900_ai_ci;

-- 切换到创建的数据库
USE `ops_work_order_system`;

-- 设置字符集和外键检查
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================
-- 部门管理相关表
-- ====================================================

-- 部门表
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `code` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门代码',
    `contact_person` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
    `created_at` DATETIME(6) NULL DEFAULT NULL COMMENT '创建时间',
    `description` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门描述',
    `is_active` BIT(1) NULL DEFAULT NULL COMMENT '是否启用',
    `level` INT NULL DEFAULT NULL COMMENT '部门层级',
    `name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
    `type` ENUM('BUSINESS','COUNTY','MUNICIPAL','OPERATIONAL','PROVINCIAL','SUPPORT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门类型',
    `updated_at` DATETIME(6) NULL DEFAULT NULL COMMENT '更新时间',
    `parent_id` INT NULL DEFAULT NULL COMMENT '父部门ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `UKl7tivi5261wxdnvo6cct9gg6t` (`code`) USING BTREE,
    INDEX `idx_departments_parent_active` (`parent_id`, `is_active`) USING BTREE,
    INDEX `idx_departments_type_active` (`type`, `is_active`) USING BTREE,
    INDEX `idx_departments_level` (`level`) USING BTREE,
    CONSTRAINT `FK63q917a0aq92i7gcw6h7f1jrv` FOREIGN KEY (`parent_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '部门表';

-- 用户表
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `created_at` DATETIME(6) NULL DEFAULT NULL COMMENT '创建时间',
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `updated_at` DATETIME(6) NULL DEFAULT NULL COMMENT '更新时间',
    `email` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
    `password` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
    `phone` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
    `username` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
    `role` ENUM('ADMIN','MANAGER','OPERATOR') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色',
    `department_id` INT NULL DEFAULT NULL COMMENT '部门ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `UKr43af9ap4edm43mmtq01oddj6` (`username`) USING BTREE,
    INDEX `idx_users_department_role` (`department_id`, `role`) USING BTREE,
    INDEX `idx_users_role` (`role`) USING BTREE,
    INDEX `idx_users_email` (`email`) USING BTREE,
    CONSTRAINT `FKsbg59w8q63i0oo53rlgvlcnjq` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '用户表';

-- 部门联系人表
DROP TABLE IF EXISTS `department_contacts`;
CREATE TABLE `department_contacts` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '联系人ID',
    `created_at` DATETIME(6) NULL DEFAULT NULL COMMENT '创建时间',
    `email` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
    `is_active` BIT(1) NULL DEFAULT NULL COMMENT '是否启用',
    `is_emergency` BIT(1) NULL DEFAULT NULL COMMENT '是否紧急联系人',
    `is_primary` BIT(1) NULL DEFAULT NULL COMMENT '是否主要联系人',
    `mobile` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
    `name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人姓名',
    `phone` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '办公电话',
    `position` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职位',
    `remark` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
    `updated_at` DATETIME(6) NULL DEFAULT NULL COMMENT '更新时间',
    `working_hours` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工作时间',
    `department_id` INT NOT NULL COMMENT '部门ID',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_department_contacts_dept_active` (`department_id`, `is_active`) USING BTREE,
    INDEX `idx_department_contacts_emergency_active` (`is_emergency`, `is_active`) USING BTREE,
    INDEX `idx_department_contacts_primary` (`is_primary`, `department_id`) USING BTREE,
    CONSTRAINT `FKhr29u6qa6u2rqd74h247ck8bk` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '部门联系人表';

-- 部门权限表
DROP TABLE IF EXISTS `department_permissions`;
CREATE TABLE `department_permissions` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `expires_at` DATETIME(6) NULL DEFAULT NULL COMMENT '过期时间',
    `granted_at` DATETIME(6) NULL DEFAULT NULL COMMENT '授权时间',
    `granted_by` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '授权人',
    `is_active` BIT(1) NULL DEFAULT NULL COMMENT '是否启用',
    `permission_type` ENUM('APPROVE_WORK_ORDER','ASSIGN_WORK_ORDER','CONFIG_WORKFLOW','CREATE_WORK_ORDER','DELETE_WORK_ORDER','EDIT_WORK_ORDER','EXPORT_REPORTS','MANAGE_CONTACTS','MANAGE_DEPARTMENT','MANAGE_USERS','REASSIGN_WORK_ORDER','REJECT_WORK_ORDER','SYSTEM_ADMIN','VIEW_DEPARTMENT','VIEW_REPORTS','VIEW_USERS','VIEW_WORK_ORDER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限类型',
    `remark` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
    `department_id` INT NOT NULL COMMENT '部门ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `UK24cmpeuqvbqprsvsdfqif5p20` (`department_id`, `permission_type`) USING BTREE,
    INDEX `idx_department_permissions_dept_type` (`department_id`, `permission_type`) USING BTREE,
    INDEX `idx_department_permissions_type_active` (`permission_type`, `is_active`) USING BTREE,
    CONSTRAINT `FKnqxfum7b4w8d8t2cfr27gn7tt` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '部门权限表';

-- ====================================================
-- 工单管理相关表
-- ====================================================

-- 工单表
DROP TABLE IF EXISTS `work_orders`;
CREATE TABLE `work_orders` (
    `assigned_to` BIGINT NULL DEFAULT NULL COMMENT '分配给',
    `created_at` DATETIME(6) NULL DEFAULT NULL COMMENT '创建时间',
    `created_by` BIGINT NOT NULL COMMENT '创建人',
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '工单ID',
    `updated_at` DATETIME(6) NULL DEFAULT NULL COMMENT '更新时间',
    `department` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理部门',
    `description` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工单描述',
    `title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工单标题',
    `priority` ENUM('HIGH','LOW','MEDIUM') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '优先级',
    `status` ENUM('APPROVED','COMPLETED','IN_PROGRESS','OVERDUE','PENDING') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
    `deadline` DATETIME(6) NULL DEFAULT NULL COMMENT '截止时间',
    `is_overdue` BIT(1) NULL DEFAULT NULL COMMENT '是否超期',
    `type` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工单类型',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_work_orders_status_created_at` (`status`, `created_at`) USING BTREE,
    INDEX `idx_work_orders_status_deadline` (`status`, `deadline`) USING BTREE,
    INDEX `idx_work_orders_assigned_to_status` (`assigned_to`, `status`) USING BTREE,
    INDEX `idx_work_orders_created_by_created_at` (`created_by`, `created_at`) USING BTREE,
    INDEX `idx_work_orders_priority_status` (`priority`, `status`) USING BTREE,
    INDEX `idx_work_orders_overdue_status` (`is_overdue`, `status`) USING BTREE,
    INDEX `idx_work_orders_type_status` (`type`, `status`) USING BTREE,
    INDEX `idx_work_orders_department` (`department`) USING BTREE,
    CONSTRAINT `FKm24kvbbs7q2qem1v1sk8opca0` FOREIGN KEY (`assigned_to`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FKpjx6go2keqx7y8idesxp1kvsd` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '工单表';

-- ====================================================
-- 工作流管理相关表
-- ====================================================

-- 工作流模板表
DROP TABLE IF EXISTS `workflow_templates`;
CREATE TABLE `workflow_templates` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    `created_at` DATETIME(6) NULL DEFAULT NULL COMMENT '创建时间',
    `description` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板描述',
    `is_active` BIT(1) NULL DEFAULT NULL COMMENT '是否启用',
    `template_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
    `updated_at` DATETIME(6) NULL DEFAULT NULL COMMENT '更新时间',
    `work_order_type` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用工单类型',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `UK6ohawur72g565ytlvbqt9111a` (`template_name`) USING BTREE,
    INDEX `idx_workflow_templates_type_active` (`work_order_type`, `is_active`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '工作流模板表';

-- 工作流模板步骤表
DROP TABLE IF EXISTS `workflow_template_steps`;
CREATE TABLE `workflow_template_steps` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '步骤ID',
    `assignee_role` ENUM('ADMIN','MANAGER','OPERATOR','USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分配角色',
    `auto_approve` BIT(1) NULL DEFAULT NULL COMMENT '自动审批',
    `conditions` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '条件',
    `is_parallel` BIT(1) NULL DEFAULT NULL COMMENT '是否并行',
    `is_skippable` BIT(1) NULL DEFAULT NULL COMMENT '是否可跳过',
    `parallel_group` INT NULL DEFAULT NULL COMMENT '并行组',
    `step_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '步骤名称',
    `step_order` INT NOT NULL COMMENT '步骤顺序',
    `step_type` ENUM('COMPLETION','DEPARTMENT_REVIEW','DIRECTOR_APPROVAL','EXECUTION','MANAGER_APPROVAL','VERIFICATION') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '步骤类型',
    `time_limit_hours` INT NULL DEFAULT NULL COMMENT '时限(小时)',
    `assignee_department_id` INT NULL DEFAULT NULL COMMENT '分配部门',
    `template_id` INT NOT NULL COMMENT '模板ID',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_workflow_template_steps_template_order` (`template_id`, `step_order`) USING BTREE,
    INDEX `idx_workflow_template_steps_dept_type` (`assignee_department_id`, `step_type`) USING BTREE,
    CONSTRAINT `FKbpnw31s5h98f2i5ri92pkvtbt` FOREIGN KEY (`assignee_department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FKhko2gh77u9nvf62xglbdfeu7d` FOREIGN KEY (`template_id`) REFERENCES `workflow_templates` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '工作流模板步骤表';

-- 工作流步骤表
DROP TABLE IF EXISTS `workflow_steps`;
CREATE TABLE `workflow_steps` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '步骤ID',
    `assignee_role` ENUM('ADMIN','MANAGER','OPERATOR','USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分配角色',
    `comments` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
    `completed_at` DATETIME(6) NULL DEFAULT NULL COMMENT '完成时间',
    `created_at` DATETIME(6) NULL DEFAULT NULL COMMENT '创建时间',
    `deadline` DATETIME(6) NULL DEFAULT NULL COMMENT '截止时间',
    `is_parallel` BIT(1) NULL DEFAULT NULL COMMENT '是否并行',
    `is_skippable` BIT(1) NULL DEFAULT NULL COMMENT '是否可跳过',
    `started_at` DATETIME(6) NULL DEFAULT NULL COMMENT '开始时间',
    `status` ENUM('COMPLETED','IN_PROGRESS','PENDING','REJECTED','SKIPPED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
    `step_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '步骤名称',
    `step_order` INT NOT NULL COMMENT '步骤顺序',
    `step_type` ENUM('COMPLETION','DEPARTMENT_REVIEW','DIRECTOR_APPROVAL','EXECUTION','MANAGER_APPROVAL','VERIFICATION') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '步骤类型',
    `updated_at` DATETIME(6) NULL DEFAULT NULL COMMENT '更新时间',
    `assignee_id` INT NULL DEFAULT NULL COMMENT '分配人',
    `assignee_department_id` INT NULL DEFAULT NULL COMMENT '分配部门',
    `work_order_id` INT NOT NULL COMMENT '工单ID',
    `parallel_group` INT NULL DEFAULT NULL COMMENT '并行组',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_workflow_steps_work_order_step_order` (`work_order_id`, `step_order`) USING BTREE,
    INDEX `idx_workflow_steps_assignee_status` (`assignee_id`, `status`) USING BTREE,
    INDEX `idx_workflow_steps_department_status` (`assignee_department_id`, `status`) USING BTREE,
    INDEX `idx_workflow_steps_status_created_at` (`status`, `created_at`) USING BTREE,
    INDEX `idx_workflow_steps_type_status` (`step_type`, `status`) USING BTREE,
    CONSTRAINT `FKedr992p21uxdxxyswa8mu67ip` FOREIGN KEY (`assignee_department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '工作流步骤表';

-- ====================================================
-- 审批和操作记录相关表
-- ====================================================

-- 审批记录表
DROP TABLE IF EXISTS `approval_records`;
CREATE TABLE `approval_records` (
    `approver_id` BIGINT NULL DEFAULT NULL COMMENT '审批人',
    `created_at` DATETIME(6) NULL DEFAULT NULL COMMENT '创建时间',
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '审批记录ID',
    `work_order_id` BIGINT NOT NULL COMMENT '工单ID',
    `comments` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批意见',
    `status` ENUM('APPROVED','PENDING','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审批状态',
    `approved_at` DATETIME(6) NULL DEFAULT NULL COMMENT '审批时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_approval_records_work_order_created_at` (`work_order_id`, `created_at`) USING BTREE,
    INDEX `idx_approval_records_approver_status` (`approver_id`, `status`) USING BTREE,
    INDEX `idx_approval_records_status_created_at` (`status`, `created_at`) USING BTREE,
    CONSTRAINT `FK7o4xnr38kq9mgomjjnqi8358u` FOREIGN KEY (`work_order_id`) REFERENCES `work_orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FKpiegtiv1mkj4am4ipxx3d2iqp` FOREIGN KEY (`approver_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '审批记录表';

-- 操作记录表
DROP TABLE IF EXISTS `operation_records`;
CREATE TABLE `operation_records` (
    `created_at` DATETIME(6) NULL DEFAULT NULL COMMENT '操作时间',
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '操作记录ID',
    `operator_id` BIGINT NULL DEFAULT NULL COMMENT '操作人',
    `work_order_id` BIGINT NOT NULL COMMENT '工单ID',
    `comments` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作说明',
    `action` ENUM('COMPLETION','EXECUTION','HANDOVER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_operation_records_work_order_created_at` (`work_order_id`, `created_at`) USING BTREE,
    INDEX `idx_operation_records_operator_action` (`operator_id`, `action`) USING BTREE,
    INDEX `idx_operation_records_action_created_at` (`action`, `created_at`) USING BTREE,
    CONSTRAINT `FK2a953ucfoddmv1otak600kj9s` FOREIGN KEY (`work_order_id`) REFERENCES `work_orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FK9qqfdmgem5fpl6dm51uwk3wnt` FOREIGN KEY (`operator_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '操作记录表';

-- 转派记录表
DROP TABLE IF EXISTS `transfer_records`;
CREATE TABLE `transfer_records` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '转派记录ID',
    `accepted_at` DATETIME(6) NULL DEFAULT NULL COMMENT '接收时间',
    `completed_at` DATETIME(6) NULL DEFAULT NULL COMMENT '完成时间',
    `created_at` DATETIME(6) NULL DEFAULT NULL COMMENT '创建时间',
    `is_assistance` BIT(1) NULL DEFAULT NULL COMMENT '是否协助',
    `requested_at` DATETIME(6) NULL DEFAULT NULL COMMENT '请求时间',
    `status` ENUM('ACCEPTED','CANCELLED','COMPLETED','PENDING','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
    `transfer_comments` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '转派备注',
    `transfer_reason` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转派原因',
    `transfer_type` ENUM('ASSISTANCE_REQUEST','DEPARTMENT_TRANSFER','USER_TRANSFER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '转派类型',
    `updated_at` DATETIME(6) NULL DEFAULT NULL COMMENT '更新时间',
    `accepted_by` INT NULL DEFAULT NULL COMMENT '接收确认人',
    `from_department_id` INT NULL DEFAULT NULL COMMENT '转派部门',
    `from_user_id` INT NULL DEFAULT NULL COMMENT '转派人',
    `requested_by` INT NOT NULL COMMENT '请求人',
    `to_department_id` INT NULL DEFAULT NULL COMMENT '接收部门',
    `to_user_id` INT NULL DEFAULT NULL COMMENT '接收人',
    `work_order_id` INT NOT NULL COMMENT '工单ID',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `FKr1cjo15dxu65y2a1udwy8ukla` (`to_department_id`) USING BTREE,
    INDEX `idx_transfer_records_status` (`status`) USING BTREE,
    INDEX `idx_transfer_records_status_created_at` (`status`, `created_at`) USING BTREE,
    INDEX `idx_transfer_records_work_order_created_at` (`work_order_id`, `created_at`) USING BTREE,
    INDEX `idx_transfer_records_requested_by_status` (`requested_by`, `status`) USING BTREE,
    INDEX `idx_transfer_records_from_to_department` (`from_department_id`, `to_department_id`) USING BTREE,
    INDEX `idx_transfer_records_type_status` (`transfer_type`, `status`) USING BTREE,
    INDEX `idx_transfer_records_accepted_by` (`accepted_by`) USING BTREE,
    CONSTRAINT `FKnbdhv5e237uf38h497tuemn0f` FOREIGN KEY (`from_department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `FKr1cjo15dxu65y2a1udwy8ukla` FOREIGN KEY (`to_department_id`) REFERENCES `departments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '转派记录表';

-- 设备令牌表（用于推送通知）
DROP TABLE IF EXISTS `device_tokens`;
CREATE TABLE `device_tokens` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '令牌ID',
    `created_at` DATETIME(6) NOT NULL COMMENT '创建时间',
    `device_info` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息',
    `device_type` ENUM('ANDROID','IOS','WEB') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备类型',
    `fcm_token` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'FCM令牌',
    `is_active` BIT(1) NOT NULL COMMENT '是否启用',
    `last_used_at` DATETIME(6) NULL DEFAULT NULL COMMENT '最后使用时间',
    `updated_at` DATETIME(6) NULL DEFAULT NULL COMMENT '更新时间',
    `user_id` INT NOT NULL COMMENT '用户ID',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_device_tokens_user_active` (`user_id`, `is_active`) USING BTREE,
    INDEX `idx_device_tokens_type_active` (`device_type`, `is_active`) USING BTREE,
    INDEX `idx_device_tokens_last_used` (`last_used_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '设备令牌表';

-- ====================================================
-- 插入初始数据
-- ====================================================

-- 插入系统管理部门
INSERT INTO `departments` (`name`, `description`, `is_active`, `created_at`, `updated_at`) VALUES
('系统管理部', '系统管理部门，负责系统维护和用户管理', b'1', NOW(6), NOW(6));

-- 插入默认管理员用户
-- 密码: admin123 (使用BCrypt加密)
INSERT INTO `users` (`username`, `password`, `role`, `email`, `department_id`, `created_at`, `updated_at`) VALUES
('admin', '$2a$10$rAM0X6PtXZU0U1WwIQhO2.1Z1nhG6S2CgXWY1YB7REqeKF6jzFE1W', 'ADMIN', 'admin@example.com', 1, NOW(6), NOW(6));

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ====================================================
-- 执行完成提示
-- ====================================================

SELECT '数据库初始化完成！' as message;
SELECT '数据库名称: ops_work_order_system' as info;
SELECT '默认管理员账号: admin' as login_info;
SELECT '默认管理员密码: admin123' as password_info;
SELECT '数据库结构已与实际环境同步' as sync_info;
SELECT '请及时修改默认密码！' as warning; 