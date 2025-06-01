-- 转派记录性能优化索引
-- 为提高转派管理查询性能而添加的复合索引

-- 1. 状态相关查询索引
CREATE INDEX idx_transfer_records_status ON transfer_records(status);
CREATE INDEX idx_transfer_records_status_created_at ON transfer_records(status, created_at DESC);

-- 2. 用户相关查询索引
CREATE INDEX idx_transfer_records_requested_by ON transfer_records(requested_by_id);
CREATE INDEX idx_transfer_records_requested_by_created_at ON transfer_records(requested_by_id, created_at DESC);
CREATE INDEX idx_transfer_records_to_user ON transfer_records(to_user_id);
CREATE INDEX idx_transfer_records_to_user_status ON transfer_records(to_user_id, status);
CREATE INDEX idx_transfer_records_accepted_by ON transfer_records(accepted_by_id);
CREATE INDEX idx_transfer_records_from_user ON transfer_records(from_user_id);

-- 3. 部门相关查询索引
CREATE INDEX idx_transfer_records_to_department ON transfer_records(to_department_id);
CREATE INDEX idx_transfer_records_to_department_status ON transfer_records(to_department_id, status);
CREATE INDEX idx_transfer_records_from_department ON transfer_records(from_department_id);

-- 4. 工单相关查询索引
CREATE INDEX idx_transfer_records_work_order ON transfer_records(work_order_id);
CREATE INDEX idx_transfer_records_work_order_created_at ON transfer_records(work_order_id, created_at DESC);

-- 5. 时间相关查询索引
CREATE INDEX idx_transfer_records_created_at ON transfer_records(created_at DESC);
CREATE INDEX idx_transfer_records_requested_at ON transfer_records(requested_at DESC);
CREATE INDEX idx_transfer_records_accepted_at ON transfer_records(accepted_at DESC);
CREATE INDEX idx_transfer_records_completed_at ON transfer_records(completed_at DESC);

-- 6. 协助标记查询索引
CREATE INDEX idx_transfer_records_is_assistance ON transfer_records(is_assistance);
CREATE INDEX idx_transfer_records_is_assistance_created_at ON transfer_records(is_assistance, created_at DESC);

-- 7. 复合查询索引（最常用的查询组合）
CREATE INDEX idx_transfer_records_status_to_user_created_at ON transfer_records(status, to_user_id, created_at DESC);
CREATE INDEX idx_transfer_records_status_to_dept_created_at ON transfer_records(status, to_department_id, created_at DESC);
CREATE INDEX idx_transfer_records_requested_by_status ON transfer_records(requested_by_id, status);

-- 8. 统计查询优化索引
CREATE INDEX idx_transfer_records_status_count ON transfer_records(status, id);

-- 为work_orders表添加性能索引（如果还没有的话）
CREATE INDEX IF NOT EXISTS idx_work_orders_assigned_to ON work_orders(assigned_to_id);
CREATE INDEX IF NOT EXISTS idx_work_orders_created_by ON work_orders(created_by_id);
CREATE INDEX IF NOT EXISTS idx_work_orders_status ON work_orders(status);
CREATE INDEX IF NOT EXISTS idx_work_orders_created_at ON work_orders(created_at DESC);

-- 为users表添加性能索引（如果还没有的话）
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_department ON users(department_id);

-- 为departments表添加性能索引（如果还没有的话）
CREATE INDEX IF NOT EXISTS idx_departments_name ON departments(name);

-- 添加注释说明索引用途
COMMENT ON INDEX idx_transfer_records_status IS '转派状态查询优化';
COMMENT ON INDEX idx_transfer_records_status_to_user_created_at IS '用户待处理转派查询优化';
COMMENT ON INDEX idx_transfer_records_status_to_dept_created_at IS '部门待处理转派查询优化';
COMMENT ON INDEX idx_transfer_records_work_order_created_at IS '工单转派历史查询优化';
COMMENT ON INDEX idx_transfer_records_requested_by_created_at IS '用户发起转派查询优化'; 