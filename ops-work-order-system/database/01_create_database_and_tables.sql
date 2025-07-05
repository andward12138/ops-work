-- ====================================================
-- 运维工单系统数据库初始化脚本
-- 版本: 1.0
-- 创建时间: 2024
-- 
-- 使用方法：
-- 1. 在Navicat中连接到MySQL服务器
-- 2. 直接执行此SQL文件，或者复制粘贴到查询窗口执行
-- 3. 执行完成后，数据库和所有表结构将自动创建
-- ====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `ops_work_order_system` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 切换到创建的数据库
USE `ops_work_order_system`;

-- 设置SQL模式，确保兼容性
SET sql_mode = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';

-- ====================================================
-- 部门管理相关表
-- ====================================================

-- 部门表
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `code` VARCHAR(50) UNIQUE COMMENT '部门代码',
    `level` INT DEFAULT 1 COMMENT '部门层级',
    `type` VARCHAR(20) DEFAULT 'DEPARTMENT' COMMENT '部门类型',
    `parent_id` INT DEFAULT NULL COMMENT '父部门ID',
    `description` TEXT COMMENT '部门描述',
    `contact_person` VARCHAR(50) COMMENT '联系人',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`parent_id`) REFERENCES `departments`(`id`) ON DELETE SET NULL,
    INDEX `idx_departments_code` (`code`),
    INDEX `idx_departments_parent` (`parent_id`),
    INDEX `idx_departments_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 用户表
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `department_id` INT COMMENT '部门ID',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`department_id`) REFERENCES `departments`(`id`) ON DELETE SET NULL,
    INDEX `idx_users_username` (`username`),
    INDEX `idx_users_department` (`department_id`),
    INDEX `idx_users_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 部门联系人表
DROP TABLE IF EXISTS `department_contacts`;
CREATE TABLE `department_contacts` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '联系人ID',
    `department_id` INT NOT NULL COMMENT '部门ID',
    `name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `position` VARCHAR(50) COMMENT '职位',
    `phone` VARCHAR(20) NOT NULL COMMENT '办公电话',
    `mobile` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `is_primary` BOOLEAN DEFAULT FALSE COMMENT '是否主要联系人',
    `is_emergency` BOOLEAN DEFAULT FALSE COMMENT '是否紧急联系人',
    `working_hours` VARCHAR(50) COMMENT '工作时间',
    `remark` TEXT COMMENT '备注',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`department_id`) REFERENCES `departments`(`id`) ON DELETE CASCADE,
    INDEX `idx_department_contacts_phone` (`phone`),
    INDEX `idx_department_contacts_dept` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门联系人表';

-- 部门权限表
DROP TABLE IF EXISTS `department_permissions`;
CREATE TABLE `department_permissions` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    `department_id` INT NOT NULL COMMENT '部门ID',
    `permission_type` VARCHAR(50) NOT NULL COMMENT '权限类型',
    `granted_by` VARCHAR(50) COMMENT '授权人',
    `granted_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    `expires_at` DATETIME COMMENT '过期时间',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `remark` TEXT COMMENT '备注',
    UNIQUE KEY `unique_dept_perm` (`department_id`, `permission_type`),
    FOREIGN KEY (`department_id`) REFERENCES `departments`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门权限表';

-- ====================================================
-- 工单管理相关表
-- ====================================================

-- 工单表
DROP TABLE IF EXISTS `work_orders`;
CREATE TABLE `work_orders` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '工单ID',
    `title` VARCHAR(200) NOT NULL COMMENT '工单标题',
    `description` TEXT COMMENT '工单描述',
    `priority` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `type` VARCHAR(20) DEFAULT 'GENERAL' COMMENT '工单类型',
    `created_by` INT COMMENT '创建人',
    `assigned_to` INT COMMENT '分配给',
    `department` VARCHAR(100) COMMENT '处理部门',
    `deadline` DATETIME COMMENT '截止时间',
    `is_overdue` BOOLEAN DEFAULT FALSE COMMENT '是否超期',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`created_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`assigned_to`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_work_orders_status` (`status`),
    INDEX `idx_work_orders_created_at` (`created_at`),
    INDEX `idx_work_orders_deadline` (`deadline`),
    INDEX `idx_work_orders_priority` (`priority`),
    INDEX `idx_work_orders_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- ====================================================
-- 工作流管理相关表
-- ====================================================

-- 工作流模板表
DROP TABLE IF EXISTS `workflow_templates`;
CREATE TABLE `workflow_templates` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    `template_name` VARCHAR(100) NOT NULL UNIQUE COMMENT '模板名称',
    `description` TEXT COMMENT '模板描述',
    `work_order_type` VARCHAR(20) COMMENT '适用工单类型',
    `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_workflow_templates_type` (`work_order_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流模板表';

-- 工作流模板步骤表
DROP TABLE IF EXISTS `workflow_template_steps`;
CREATE TABLE `workflow_template_steps` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '步骤ID',
    `template_id` INT NOT NULL COMMENT '模板ID',
    `step_order` INT NOT NULL COMMENT '步骤顺序',
    `step_name` VARCHAR(100) NOT NULL COMMENT '步骤名称',
    `step_type` VARCHAR(50) NOT NULL COMMENT '步骤类型',
    `assignee_role` VARCHAR(20) COMMENT '分配角色',
    `assignee_department_id` INT COMMENT '分配部门',
    `is_skippable` BOOLEAN DEFAULT FALSE COMMENT '是否可跳过',
    `is_parallel` BOOLEAN DEFAULT FALSE COMMENT '是否并行',
    `parallel_group` INT COMMENT '并行组',
    `time_limit_hours` INT COMMENT '时限(小时)',
    `auto_approve` BOOLEAN DEFAULT FALSE COMMENT '自动审批',
    `conditions` TEXT COMMENT '条件',
    FOREIGN KEY (`template_id`) REFERENCES `workflow_templates`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`assignee_department_id`) REFERENCES `departments`(`id`) ON DELETE SET NULL,
    INDEX `idx_workflow_template_steps_template` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流模板步骤表';

-- 工作流步骤表
DROP TABLE IF EXISTS `workflow_steps`;
CREATE TABLE `workflow_steps` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '步骤ID',
    `work_order_id` INT NOT NULL COMMENT '工单ID',
    `step_order` INT NOT NULL COMMENT '步骤顺序',
    `step_name` VARCHAR(100) NOT NULL COMMENT '步骤名称',
    `step_type` VARCHAR(50) NOT NULL COMMENT '步骤类型',
    `assignee_id` INT COMMENT '分配人',
    `assignee_role` VARCHAR(20) COMMENT '分配角色',
    `assignee_department_id` INT COMMENT '分配部门',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `started_at` DATETIME COMMENT '开始时间',
    `completed_at` DATETIME COMMENT '完成时间',
    `deadline` DATETIME COMMENT '截止时间',
    `comments` TEXT COMMENT '备注',
    `is_skippable` BOOLEAN DEFAULT FALSE COMMENT '是否可跳过',
    `is_parallel` BOOLEAN DEFAULT FALSE COMMENT '是否并行',
    `parallel_group` INT COMMENT '并行组',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`work_order_id`) REFERENCES `work_orders`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`assignee_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`assignee_department_id`) REFERENCES `departments`(`id`) ON DELETE SET NULL,
    INDEX `idx_workflow_steps_status` (`status`),
    INDEX `idx_workflow_steps_work_order` (`work_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流步骤表';

-- ====================================================
-- 审批和操作记录相关表
-- ====================================================

-- 审批记录表
DROP TABLE IF EXISTS `approval_records`;
CREATE TABLE `approval_records` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '审批记录ID',
    `work_order_id` INT NOT NULL COMMENT '工单ID',
    `approver_id` INT COMMENT '审批人',
    `status` VARCHAR(20) NOT NULL COMMENT '审批状态',
    `comments` TEXT COMMENT '审批意见',
    `approved_at` DATETIME COMMENT '审批时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`work_order_id`) REFERENCES `work_orders`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`approver_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_approval_records_status` (`status`),
    INDEX `idx_approval_records_work_order` (`work_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批记录表';

-- 操作记录表
DROP TABLE IF EXISTS `operation_records`;
CREATE TABLE `operation_records` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '操作记录ID',
    `work_order_id` INT NOT NULL COMMENT '工单ID',
    `operator_id` INT COMMENT '操作人',
    `action` VARCHAR(20) NOT NULL COMMENT '操作类型',
    `comments` TEXT COMMENT '操作说明',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    FOREIGN KEY (`work_order_id`) REFERENCES `work_orders`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`operator_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_operation_records_work_order` (`work_order_id`),
    INDEX `idx_operation_records_action` (`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作记录表';

-- 转派记录表
DROP TABLE IF EXISTS `transfer_records`;
CREATE TABLE `transfer_records` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '转派记录ID',
    `work_order_id` INT NOT NULL COMMENT '工单ID',
    `from_user_id` INT COMMENT '转派人',
    `to_user_id` INT COMMENT '接收人',
    `from_department_id` INT COMMENT '转派部门',
    `to_department_id` INT COMMENT '接收部门',
    `transfer_type` VARCHAR(20) NOT NULL COMMENT '转派类型',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `reason` TEXT COMMENT '转派原因',
    `requested_by_id` INT NOT NULL COMMENT '请求人',
    `requested_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '请求时间',
    `accepted_by_id` INT COMMENT '接收确认人',
    `accepted_at` DATETIME COMMENT '接收时间',
    `completed_at` DATETIME COMMENT '完成时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`work_order_id`) REFERENCES `work_orders`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`from_user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`to_user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`from_department_id`) REFERENCES `departments`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`to_department_id`) REFERENCES `departments`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`requested_by_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`accepted_by_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
    INDEX `idx_transfer_records_status` (`status`),
    INDEX `idx_transfer_records_work_order` (`work_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='转派记录表';

-- 设备令牌表（用于推送通知）
DROP TABLE IF EXISTS `device_tokens`;
CREATE TABLE `device_tokens` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '令牌ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `token` VARCHAR(255) NOT NULL COMMENT '设备令牌',
    `device_type` VARCHAR(50) COMMENT '设备类型',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `last_used_at` DATETIME COMMENT '最后使用时间',
    UNIQUE KEY `unique_token` (`token`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    INDEX `idx_device_tokens_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备令牌表';

-- ====================================================
-- 插入初始数据
-- ====================================================

-- 插入系统管理部门
INSERT INTO `departments` (`name`, `code`, `level`, `type`, `description`, `is_active`) VALUES
('系统管理部', 'SYS_ADMIN', 1, 'ADMINISTRATIVE', '系统管理部门，负责系统维护和用户管理', TRUE);

-- 插入默认管理员用户
-- 密码: admin123 (使用BCrypt加密)
INSERT INTO `users` (`username`, `password`, `role`, `email`, `department_id`) VALUES
('admin', '$2a$10$rAM0X6PtXZU0U1WwIQhO2.1Z1nhG6S2CgXWY1YB7REqeKF6jzFE1W', 'ADMIN', 'admin@example.com', 1);

-- ====================================================
-- 执行完成提示
-- ====================================================

SELECT '数据库初始化完成！' as message;
SELECT '数据库名称: ops_work_order_system' as info;
SELECT '默认管理员账号: admin' as login_info;
SELECT '默认管理员密码: admin123' as password_info;
SELECT '请及时修改默认密码！' as warning; 