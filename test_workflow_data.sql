-- 测试数据脚本 - 用于测试工作流功能
-- 请在执行 ops_work_order_system.sql 之后执行此脚本

USE ops_work_order_system;

-- 插入示例部门数据（如果不存在）
INSERT IGNORE INTO `departments` (`id`, `name`, `description`, `type`, `created_at`, `updated_at`) VALUES
(1, '总部', '公司总部', 'HEADQUARTERS', NOW(), NOW()),
(2, '技术部', '技术开发部门', 'DEPARTMENT', NOW(), NOW()),
(3, '运维部', '运维管理部门', 'DEPARTMENT', NOW(), NOW()),
(4, '网络组', '网络运维小组', 'TEAM', NOW(), NOW()),
(5, '系统组', '系统运维小组', 'TEAM', NOW(), NOW());

-- 设置部门层级关系
UPDATE `departments` SET `parent_id` = 1 WHERE `id` IN (2, 3);
UPDATE `departments` SET `parent_id` = 3 WHERE `id` IN (4, 5);

-- 更新现有用户的部门信息
UPDATE `users` SET `department_id` = 2 WHERE `username` = 'admin';
UPDATE `users` SET `department_id` = 3 WHERE `username` = 'operator';
UPDATE `users` SET `department_id` = 3 WHERE `username` = 'manager';

-- 插入示例工作流模板（如果不存在）
INSERT IGNORE INTO `workflow_templates` (`template_name`, `description`, `work_order_type`, `created_at`, `updated_at`) VALUES
('标准运维工单流程', '适用于一般运维工单的标准审批流程', 'OPERATION', NOW(), NOW()),
('紧急变更流程', '适用于紧急变更的快速审批流程', 'EMERGENCY', NOW(), NOW()),
('例行维护流程', '适用于例行维护的简化流程', 'MAINTENANCE', NOW(), NOW()),
('并行审批流程', '测试并行审批功能的流程', 'PARALLEL_TEST', NOW(), NOW());

-- 插入标准运维工单流程的步骤
SET @template_id = (SELECT id FROM `workflow_templates` WHERE `template_name` = '标准运维工单流程');
DELETE FROM `workflow_template_steps` WHERE `template_id` = @template_id;
INSERT INTO `workflow_template_steps` 
(`template_id`, `step_order`, `step_name`, `step_type`, `assignee_role`, `assignee_department_id`, `time_limit_hours`) VALUES
(@template_id, 1, '部门初审', 'DEPARTMENT_REVIEW', 'MANAGER', 3, 24),
(@template_id, 2, '经理审批', 'MANAGER_APPROVAL', 'MANAGER', 3, 48),
(@template_id, 3, '执行操作', 'EXECUTION', 'OPERATOR', 3, 72),
(@template_id, 4, '完成确认', 'COMPLETION', 'USER', NULL, 24);

-- 插入紧急变更流程的步骤
SET @template_id = (SELECT id FROM `workflow_templates` WHERE `template_name` = '紧急变更流程');
DELETE FROM `workflow_template_steps` WHERE `template_id` = @template_id;
INSERT INTO `workflow_template_steps` 
(`template_id`, `step_order`, `step_name`, `step_type`, `assignee_role`, `assignee_department_id`, `time_limit_hours`, `auto_approve`) VALUES
(@template_id, 1, '经理快速审批', 'MANAGER_APPROVAL', 'MANAGER', 3, 2, TRUE),
(@template_id, 2, '紧急执行', 'EXECUTION', 'OPERATOR', 3, 4, FALSE);

-- 插入并行审批流程的步骤
SET @template_id = (SELECT id FROM `workflow_templates` WHERE `template_name` = '并行审批流程');
DELETE FROM `workflow_template_steps` WHERE `template_id` = @template_id;
INSERT INTO `workflow_template_steps` 
(`template_id`, `step_order`, `step_name`, `step_type`, `assignee_role`, `assignee_department_id`, `time_limit_hours`, `is_parallel`, `parallel_group`) VALUES
(@template_id, 1, '技术部审核', 'DEPARTMENT_REVIEW', 'MANAGER', 2, 24, TRUE, 1),
(@template_id, 1, '运维部审核', 'DEPARTMENT_REVIEW', 'MANAGER', 3, 24, TRUE, 1),
(@template_id, 2, '主管审批', 'DIRECTOR_APPROVAL', 'ADMIN', 1, 48, FALSE, NULL),
(@template_id, 3, '执行操作', 'EXECUTION', 'OPERATOR', 3, 72, FALSE, NULL);

-- 创建测试工单
INSERT INTO `work_orders` (`title`, `description`, `priority`, `status`, `type`, `created_by`, `assigned_to`, `department`, `created_at`, `updated_at`) VALUES
('测试标准运维流程', '这是一个测试标准运维流程的工单', 'MEDIUM', 'PENDING', 'OPERATION', 1, 2, '运维部', NOW(), NOW()),
('测试紧急变更流程', '这是一个测试紧急变更流程的工单', 'HIGH', 'PENDING', 'EMERGENCY', 1, 2, '运维部', NOW(), NOW()),
('测试并行审批流程', '这是一个测试并行审批流程的工单', 'HIGH', 'PENDING', 'PARALLEL_TEST', 1, 2, '运维部', NOW(), NOW());

-- 输出测试信息
SELECT '测试数据已创建成功！' AS message;
SELECT '已创建以下工作流模板：' AS message;
SELECT template_name, work_order_type, description FROM workflow_templates WHERE is_active = TRUE;
SELECT '已创建以下测试工单：' AS message;
SELECT id, title, type, status FROM work_orders WHERE type IN ('OPERATION', 'EMERGENCY', 'PARALLEL_TEST'); 