-- ====================================================
-- 运维工单系统测试数据脚本
-- 版本: 1.0
-- 
-- 使用方法：
-- 1. 请先执行 01_create_database_and_tables.sql 创建数据库结构
-- 2. 在Navicat中执行此SQL文件，或者复制粘贴到查询窗口执行
-- 3. 此脚本会插入一些测试数据，便于系统功能测试和开发调试
-- 
-- 注意：此脚本为可选执行，仅用于测试环境
-- ====================================================

-- 切换到数据库
USE `ops_work_order_system`;

-- ====================================================
-- 插入测试部门数据
-- ====================================================

-- 插入更多部门数据
INSERT INTO `departments` (`name`, `code`, `level`, `type`, `parent_id`, `description`, `contact_person`, `contact_phone`, `is_active`) VALUES
('技术部', 'TECH', 1, 'DEPARTMENT', NULL, '技术开发部门', '张技术', '010-1234-5678', TRUE),
('运维部', 'OPS', 1, 'DEPARTMENT', NULL, '运维管理部门', '李运维', '010-1234-5679', TRUE),
('产品部', 'PRODUCT', 1, 'DEPARTMENT', NULL, '产品管理部门', '王产品', '010-1234-5680', TRUE),
('网络组', 'NET_TEAM', 2, 'TEAM', 3, '网络运维小组', '赵网络', '010-1234-5681', TRUE),
('系统组', 'SYS_TEAM', 2, 'TEAM', 3, '系统运维小组', '钱系统', '010-1234-5682', TRUE),
('数据库组', 'DB_TEAM', 2, 'TEAM', 3, '数据库运维小组', '孙数据', '010-1234-5683', TRUE);

-- ====================================================
-- 插入测试用户数据
-- ====================================================

-- 插入测试用户（密码都是：123456）
INSERT INTO `users` (`username`, `password`, `role`, `email`, `phone`, `department_id`) VALUES
('tech_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iIt.n9uGPmZNxMEHgEJ8Z9qPOhO6', 'MANAGER', 'tech@example.com', '13800001001', 2),
('ops_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iIt.n9uGPmZNxMEHgEJ8Z9qPOhO6', 'MANAGER', 'ops@example.com', '13800001002', 3),
('product_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iIt.n9uGPmZNxMEHgEJ8Z9qPOhO6', 'MANAGER', 'product@example.com', '13800001003', 4),
('network_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iIt.n9uGPmZNxMEHgEJ8Z9qPOhO6', 'OPERATOR', 'network@example.com', '13800001004', 5),
('system_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iIt.n9uGPmZNxMEHgEJ8Z9qPOhO6', 'OPERATOR', 'system@example.com', '13800001005', 6),
('db_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iIt.n9uGPmZNxMEHgEJ8Z9qPOhO6', 'OPERATOR', 'dba@example.com', '13800001006', 7),
('test_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iIt.n9uGPmZNxMEHgEJ8Z9qPOhO6', 'USER', 'test@example.com', '13800001007', 2);

-- ====================================================
-- 插入部门联系人数据
-- ====================================================

INSERT INTO `department_contacts` (`department_id`, `name`, `position`, `phone`, `mobile`, `email`, `is_primary`, `is_emergency`, `working_hours`) VALUES
(2, '张技术', '技术部经理', '010-1234-5678', '13800001001', 'tech@example.com', TRUE, FALSE, '9:00-18:00'),
(2, '李开发', '高级开发工程师', '010-1234-5679', '13800001008', 'dev@example.com', FALSE, TRUE, '9:00-18:00'),
(3, '李运维', '运维部经理', '010-1234-5680', '13800001002', 'ops@example.com', TRUE, FALSE, '9:00-18:00'),
(3, '王运维', '运维工程师', '010-1234-5681', '13800001009', 'ops2@example.com', FALSE, TRUE, '24小时待命'),
(5, '赵网络', '网络组长', '010-1234-5682', '13800001004', 'network@example.com', TRUE, TRUE, '24小时待命'),
(6, '钱系统', '系统组长', '010-1234-5683', '13800001005', 'system@example.com', TRUE, TRUE, '24小时待命'),
(7, '孙数据', '数据库组长', '010-1234-5684', '13800001006', 'dba@example.com', TRUE, TRUE, '24小时待命');

-- ====================================================
-- 插入工作流模板数据
-- ====================================================

INSERT INTO `workflow_templates` (`template_name`, `description`, `work_order_type`, `is_active`) VALUES
('标准运维工单流程', '适用于一般运维工单的标准审批流程', 'OPERATION', TRUE),
('紧急变更流程', '适用于紧急变更的快速审批流程', 'EMERGENCY', TRUE),
('例行维护流程', '适用于例行维护的简化流程', 'MAINTENANCE', TRUE),
('数据库变更流程', '适用于数据库变更的专门流程', 'DATABASE', TRUE),
('网络变更流程', '适用于网络变更的专门流程', 'NETWORK', TRUE);

-- ====================================================
-- 插入工作流模板步骤数据
-- ====================================================

-- 标准运维工单流程步骤
SET @template_id = (SELECT id FROM `workflow_templates` WHERE `template_name` = '标准运维工单流程');
INSERT INTO `workflow_template_steps` (`template_id`, `step_order`, `step_name`, `step_type`, `assignee_role`, `assignee_department_id`, `time_limit_hours`, `is_skippable`) VALUES
(@template_id, 1, '部门初审', 'DEPARTMENT_REVIEW', 'MANAGER', 3, 24, FALSE),
(@template_id, 2, '经理审批', 'MANAGER_APPROVAL', 'MANAGER', 3, 48, FALSE),
(@template_id, 3, '执行操作', 'EXECUTION', 'OPERATOR', 3, 72, FALSE),
(@template_id, 4, '完成确认', 'COMPLETION', 'USER', NULL, 24, TRUE);

-- 紧急变更流程步骤
SET @template_id = (SELECT id FROM `workflow_templates` WHERE `template_name` = '紧急变更流程');
INSERT INTO `workflow_template_steps` (`template_id`, `step_order`, `step_name`, `step_type`, `assignee_role`, `assignee_department_id`, `time_limit_hours`, `auto_approve`) VALUES
(@template_id, 1, '经理快速审批', 'MANAGER_APPROVAL', 'MANAGER', 3, 2, FALSE),
(@template_id, 2, '紧急执行', 'EXECUTION', 'OPERATOR', 3, 4, FALSE);

-- 数据库变更流程步骤
SET @template_id = (SELECT id FROM `workflow_templates` WHERE `template_name` = '数据库变更流程');
INSERT INTO `workflow_template_steps` (`template_id`, `step_order`, `step_name`, `step_type`, `assignee_role`, `assignee_department_id`, `time_limit_hours`, `is_parallel`, `parallel_group`) VALUES
(@template_id, 1, '技术部审核', 'DEPARTMENT_REVIEW', 'MANAGER', 2, 24, TRUE, 1),
(@template_id, 1, '运维部审核', 'DEPARTMENT_REVIEW', 'MANAGER', 3, 24, TRUE, 1),
(@template_id, 2, '数据库管理员执行', 'EXECUTION', 'OPERATOR', 7, 48, FALSE, NULL),
(@template_id, 3, '执行结果确认', 'COMPLETION', 'MANAGER', 3, 12, FALSE, NULL);

-- ====================================================
-- 插入测试工单数据
-- ====================================================

INSERT INTO `work_orders` (`title`, `description`, `priority`, `status`, `type`, `created_by`, `assigned_to`, `department`, `deadline`, `created_at`) VALUES
('服务器磁盘空间不足', '生产服务器 /var/log 目录磁盘使用率达到 95%，需要清理日志文件', 'HIGH', 'PENDING', 'OPERATION', 7, 6, '系统组', DATE_ADD(NOW(), INTERVAL 1 DAY), NOW()),
('数据库性能优化', '用户反馈系统响应较慢，需要对数据库进行性能优化', 'MEDIUM', 'IN_PROGRESS', 'DATABASE', 3, 7, '数据库组', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('网络防火墙规则更新', '新增办公区域网络访问权限，需要更新防火墙规则', 'LOW', 'PENDING', 'NETWORK', 2, 5, '网络组', DATE_ADD(NOW(), INTERVAL 7 DAY), NOW()),
('系统安全补丁更新', '操作系统存在高危漏洞，需要及时安装安全补丁', 'URGENT', 'PENDING', 'EMERGENCY', 1, 6, '系统组', DATE_ADD(NOW(), INTERVAL 0.5 DAY), NOW()),
('备份服务器配置', '新增备份服务器，需要配置备份策略和监控', 'MEDIUM', 'COMPLETED', 'MAINTENANCE', 3, 6, '系统组', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- ====================================================
-- 插入审批记录数据
-- ====================================================

INSERT INTO `approval_records` (`work_order_id`, `approver_id`, `status`, `comments`, `approved_at`) VALUES
(2, 3, 'APPROVED', '数据库性能确实需要优化，同意执行', DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(5, 3, 'APPROVED', '备份策略配置完成，验收通过', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ====================================================
-- 插入操作记录数据
-- ====================================================

INSERT INTO `operation_records` (`work_order_id`, `operator_id`, `action`, `comments`) VALUES
(2, 7, 'START', '开始执行数据库性能优化'),
(2, 7, 'UPDATE', '已完成索引优化，查询性能提升30%'),
(5, 6, 'START', '开始配置备份服务器'),
(5, 6, 'COMPLETE', '备份服务器配置完成，已启用自动备份');

-- ====================================================
-- 插入部门权限数据
-- ====================================================

INSERT INTO `department_permissions` (`department_id`, `permission_type`, `granted_by`, `granted_at`) VALUES
(2, 'SYSTEM_CONFIG', 'admin', NOW()),
(3, 'SYSTEM_MAINTENANCE', 'admin', NOW()),
(3, 'EMERGENCY_OPERATION', 'admin', NOW()),
(5, 'NETWORK_CONFIG', 'admin', NOW()),
(6, 'SYSTEM_ADMIN', 'admin', NOW()),
(7, 'DATABASE_ADMIN', 'admin', NOW());

-- ====================================================
-- 执行完成提示
-- ====================================================

SELECT '测试数据插入完成！' as message;
SELECT '已创建以下测试用户：' as users_info;
SELECT username, role, email, phone FROM users WHERE username != 'admin';
SELECT '已创建以下测试工单：' as workorders_info;
SELECT id, title, priority, status, type FROM work_orders;
SELECT '所有测试用户的默认密码都是：123456' as password_info;
SELECT '请在生产环境中及时修改所有默认密码！' as warning; 