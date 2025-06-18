-- 性能优化索引脚本
-- 用于提升运维工单系统查询性能

-- 1. 工单表核心查询索引
CREATE INDEX IF NOT EXISTS idx_work_orders_status_created_at ON work_orders(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_work_orders_assigned_to_status ON work_orders(assigned_to_id, status);
CREATE INDEX IF NOT EXISTS idx_work_orders_created_by_created_at ON work_orders(created_by_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_work_orders_type_status ON work_orders(type, status);
CREATE INDEX IF NOT EXISTS idx_work_orders_deadline_status ON work_orders(deadline, status);
CREATE INDEX IF NOT EXISTS idx_work_orders_is_overdue ON work_orders(is_overdue);

-- 2. 用户表查询优化索引
CREATE INDEX IF NOT EXISTS idx_users_role_department ON users(role, department_id);
CREATE INDEX IF NOT EXISTS idx_users_username_role ON users(username, role);
CREATE INDEX IF NOT EXISTS idx_users_department_id ON users(department_id);

-- 3. 部门表查询优化索引
CREATE INDEX IF NOT EXISTS idx_departments_parent_id_active ON departments(parent_id, is_active);
CREATE INDEX IF NOT EXISTS idx_departments_type_active ON departments(type, is_active);
CREATE INDEX IF NOT EXISTS idx_departments_name_active ON departments(name, is_active);

-- 4. 工作流步骤表查询优化索引
CREATE INDEX IF NOT EXISTS idx_workflow_steps_work_order_order ON workflow_steps(work_order_id, step_order);
CREATE INDEX IF NOT EXISTS idx_workflow_steps_assignee_status ON workflow_steps(assignee_id, status);
CREATE INDEX IF NOT EXISTS idx_workflow_steps_status_deadline ON workflow_steps(status, deadline);
CREATE INDEX IF NOT EXISTS idx_workflow_steps_dept_status ON workflow_steps(assignee_department_id, status);

-- 5. 审批记录表查询优化索引
CREATE INDEX IF NOT EXISTS idx_approval_records_work_order_created ON approval_records(work_order_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_approval_records_approver_status ON approval_records(approver_id, status);
CREATE INDEX IF NOT EXISTS idx_approval_records_status_approved_at ON approval_records(status, approved_at DESC);

-- 6. 操作记录表查询优化索引
CREATE INDEX IF NOT EXISTS idx_operation_records_work_order_created ON operation_records(work_order_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_operation_records_operator_action ON operation_records(operator_id, action);
CREATE INDEX IF NOT EXISTS idx_operation_records_created_at ON operation_records(created_at DESC);

-- 7. 统计查询专用索引
CREATE INDEX IF NOT EXISTS idx_work_orders_created_at_status_type ON work_orders(created_at, status, type);
CREATE INDEX IF NOT EXISTS idx_work_orders_updated_at_status ON work_orders(updated_at, status);

-- 8. 复合查询优化索引
CREATE INDEX IF NOT EXISTS idx_work_orders_dept_created_status ON work_orders(department, created_at DESC, status);
CREATE INDEX IF NOT EXISTS idx_work_orders_priority_status_created ON work_orders(priority, status, created_at DESC);

-- 9. 转派记录高频查询索引（如果还没有的话）
CREATE INDEX IF NOT EXISTS idx_transfer_records_to_user_status_created ON transfer_records(to_user_id, status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_transfer_records_work_order_status ON transfer_records(work_order_id, status);

-- 10. 设备令牌表索引（如果有的话）
-- CREATE INDEX IF NOT EXISTS idx_device_tokens_user_active ON device_tokens(user_id, is_active);
-- CREATE INDEX IF NOT EXISTS idx_device_tokens_fcm_token ON device_tokens(fcm_token);

-- 添加分析统计信息，帮助MySQL优化器选择最佳执行计划
ANALYZE TABLE work_orders;
ANALYZE TABLE users;
ANALYZE TABLE departments;
ANALYZE TABLE workflow_steps;
ANALYZE TABLE approval_records;
ANALYZE TABLE operation_records;
ANALYZE TABLE transfer_records;

-- 为大表创建分区（可选，用于数据量很大的情况）
-- ALTER TABLE work_orders PARTITION BY RANGE (YEAR(created_at)) (
--     PARTITION p2023 VALUES LESS THAN (2024),
--     PARTITION p2024 VALUES LESS THAN (2025),
--     PARTITION p2025 VALUES LESS THAN (2026),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- 添加索引注释说明
ALTER TABLE work_orders COMMENT = '工单表 - 已优化查询索引';
ALTER TABLE users COMMENT = '用户表 - 已优化查询索引';
ALTER TABLE departments COMMENT = '部门表 - 已优化查询索引';
ALTER TABLE workflow_steps COMMENT = '工作流步骤表 - 已优化查询索引';
ALTER TABLE approval_records COMMENT = '审批记录表 - 已优化查询索引';
ALTER TABLE operation_records COMMENT = '操作记录表 - 已优化查询索引';
ALTER TABLE transfer_records COMMENT = '转派记录表 - 已优化查询索引'; 