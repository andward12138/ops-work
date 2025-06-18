-- ====================================
-- 运维工单系统性能优化索引 V8
-- 基于实际数据库结构创建
-- 兼容MySQL 5.7+ 版本
-- 执行时间: 预计2-5分钟
-- ====================================

-- 1. 工单表 (work_orders) 性能优化索引
-- 主要查询场景: 状态查询、时间范围查询、分配人查询、创建人查询

-- 状态和创建时间复合索引 (最常用)
CREATE INDEX idx_work_orders_status_created_at 
ON work_orders (status, created_at DESC);

-- 状态和截止时间复合索引 (超时工单查询)
CREATE INDEX idx_work_orders_status_deadline 
ON work_orders (status, deadline);

-- 分配人和状态复合索引 (个人工单列表)
CREATE INDEX idx_work_orders_assigned_to_status 
ON work_orders (assigned_to, status);

-- 创建人和时间复合索引 (创建历史查询)
CREATE INDEX idx_work_orders_created_by_created_at 
ON work_orders (created_by, created_at DESC);

-- 优先级和状态复合索引 (紧急工单查询)
CREATE INDEX idx_work_orders_priority_status 
ON work_orders (priority, status);

-- 超时标记和状态复合索引 (超时工单专用)
CREATE INDEX idx_work_orders_overdue_status 
ON work_orders (is_overdue, status);

-- 工单类型和状态复合索引 (按类型统计)
CREATE INDEX idx_work_orders_type_status 
ON work_orders (type, status);

-- 部门字段索引 (部门统计查询)
CREATE INDEX idx_work_orders_department 
ON work_orders (department);

-- 2. 用户表 (users) 性能优化索引
-- 主要查询场景: 部门用户查询、角色查询、活跃用户查询

-- 部门和角色复合索引 (部门人员查询)
CREATE INDEX idx_users_department_role 
ON users (department_id, role);

-- 角色索引 (权限查询)
CREATE INDEX idx_users_role 
ON users (role);

-- 邮箱索引 (登录查询优化)
CREATE INDEX idx_users_email 
ON users (email);

-- 3. 部门表 (departments) 性能优化索引
-- 主要查询场景: 层级查询、活跃部门查询、类型查询

-- 父部门和活跃状态复合索引 (层级查询)
CREATE INDEX idx_departments_parent_active 
ON departments (parent_id, is_active);

-- 部门类型和活跃状态复合索引 (按类型查询)
CREATE INDEX idx_departments_type_active 
ON departments (type, is_active);

-- 部门层级索引 (层级统计)
CREATE INDEX idx_departments_level 
ON departments (level);

-- 部门代码索引 (已存在唯一索引，无需重复创建)

-- 4. 审批记录表 (approval_records) 性能优化索引
-- 主要查询场景: 工单审批历史、审批人查询、状态查询

-- 工单ID和创建时间复合索引 (审批历史查询)
CREATE INDEX idx_approval_records_work_order_created_at 
ON approval_records (work_order_id, created_at DESC);

-- 审批人和状态复合索引 (个人审批查询)
CREATE INDEX idx_approval_records_approver_status 
ON approval_records (approver_id, status);

-- 状态和创建时间复合索引 (待审批列表)
CREATE INDEX idx_approval_records_status_created_at 
ON approval_records (status, created_at DESC);

-- 5. 转派记录表 (transfer_records) 性能优化索引
-- 主要查询场景: 转派历史、状态查询、部门转派统计

-- 工单ID和创建时间复合索引 (转派历史)
CREATE INDEX idx_transfer_records_work_order_created_at 
ON transfer_records (work_order_id, created_at DESC);

-- 请求人和状态复合索引 (个人转派记录)
CREATE INDEX idx_transfer_records_requested_by_status 
ON transfer_records (requested_by, status);

-- 源部门和目标部门复合索引 (部门间转派统计)
CREATE INDEX idx_transfer_records_from_to_department 
ON transfer_records (from_department_id, to_department_id);

-- 转派类型和状态复合索引 (按类型统计)
CREATE INDEX idx_transfer_records_type_status 
ON transfer_records (transfer_type, status);

-- 接收人索引 (接收转派查询)
CREATE INDEX idx_transfer_records_accepted_by 
ON transfer_records (accepted_by);

-- 6. 操作记录表 (operation_records) 性能优化索引
-- 主要查询场景: 工单操作历史、操作人查询

-- 工单ID和创建时间复合索引 (操作历史查询)
CREATE INDEX idx_operation_records_work_order_created_at 
ON operation_records (work_order_id, created_at DESC);

-- 操作人和操作类型复合索引 (个人操作统计)
CREATE INDEX idx_operation_records_operator_action 
ON operation_records (operator_id, action);

-- 操作类型和创建时间复合索引 (按操作类型统计)
CREATE INDEX idx_operation_records_action_created_at 
ON operation_records (action, created_at DESC);

-- 7. 工作流步骤表 (workflow_steps) 性能优化索引
-- 主要查询场景: 工单流程查询、待办任务查询

-- 工单ID和步骤顺序复合索引 (流程查询)
CREATE INDEX idx_workflow_steps_work_order_step_order 
ON workflow_steps (work_order_id, step_order);

-- 分配人和状态复合索引 (个人待办查询)
CREATE INDEX idx_workflow_steps_assignee_status 
ON workflow_steps (assignee_id, status);

-- 分配部门和状态复合索引 (部门待办查询)
CREATE INDEX idx_workflow_steps_department_status 
ON workflow_steps (assignee_department_id, status);

-- 状态和创建时间复合索引 (待办列表)
CREATE INDEX idx_workflow_steps_status_created_at 
ON workflow_steps (status, created_at DESC);

-- 步骤类型和状态复合索引 (按类型统计)
CREATE INDEX idx_workflow_steps_type_status 
ON workflow_steps (step_type, status);

-- 8. 部门权限表 (department_permissions) 性能优化索引
-- 主要查询场景: 部门权限查询、权限类型统计

-- 部门ID和权限类型复合索引 (权限查询)
CREATE INDEX idx_department_permissions_dept_type 
ON department_permissions (department_id, permission_type);

-- 权限类型和活跃状态复合索引 (权限统计)
CREATE INDEX idx_department_permissions_type_active 
ON department_permissions (permission_type, is_active);

-- 9. 部门联系人表 (department_contacts) 性能优化索引
-- 主要查询场景: 部门联系人查询、紧急联系人查询

-- 部门ID和活跃状态复合索引 (部门联系人)
CREATE INDEX idx_department_contacts_dept_active 
ON department_contacts (department_id, is_active);

-- 紧急联系人和活跃状态复合索引 (紧急联系人查询)
CREATE INDEX idx_department_contacts_emergency_active 
ON department_contacts (is_emergency, is_active);

-- 主要联系人索引 (主联系人查询)
CREATE INDEX idx_department_contacts_primary 
ON department_contacts (is_primary, department_id);

-- 10. 工作流模板表 (workflow_templates) 性能优化索引
-- 主要查询场景: 模板查询、工单类型查询

-- 工单类型和活跃状态复合索引 (模板查询)
CREATE INDEX idx_workflow_templates_type_active 
ON workflow_templates (work_order_type, is_active);

-- 11. 工作流模板步骤表 (workflow_template_steps) 性能优化索引
-- 主要查询场景: 模板步骤查询

-- 模板ID和步骤顺序复合索引 (模板步骤查询)
CREATE INDEX idx_workflow_template_steps_template_order 
ON workflow_template_steps (template_id, step_order);

-- 分配部门和步骤类型复合索引 (部门模板查询)
CREATE INDEX idx_workflow_template_steps_dept_type 
ON workflow_template_steps (assignee_department_id, step_type);

-- 12. 设备令牌表 (device_tokens) 性能优化索引
-- 主要查询场景: 用户设备查询、活跃令牌查询

-- 用户ID和活跃状态复合索引 (用户设备查询)
CREATE INDEX idx_device_tokens_user_active 
ON device_tokens (user_id, is_active);

-- 设备类型和活跃状态复合索引 (设备统计)
CREATE INDEX idx_device_tokens_type_active 
ON device_tokens (device_type, is_active);

-- 最后使用时间索引 (清理过期令牌)
CREATE INDEX idx_device_tokens_last_used 
ON device_tokens (last_used_at);

-- ====================================
-- 索引创建完成统计
-- ====================================

-- 显示已创建的索引统计信息
SELECT 
    TABLE_NAME as '表名',
    INDEX_NAME as '索引名',
    COLUMN_NAME as '索引字段',
    CARDINALITY as '基数',
    INDEX_TYPE as '索引类型'
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'ops_work_order_system'
    AND INDEX_NAME LIKE 'idx_%'
ORDER BY TABLE_NAME, INDEX_NAME;

-- 输出优化完成信息
SELECT 
    '数据库性能优化完成!' as '状态',
    COUNT(*) as '已创建索引数量'
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'ops_work_order_system'
    AND INDEX_NAME LIKE 'idx_%'; 