-- 修复部门状态和字符编码问题
-- 执行此脚本前请备份数据库

USE ops_work_order_system;

-- 1. 修复所有部门的激活状态
UPDATE departments SET is_active = TRUE WHERE is_active = FALSE OR is_active IS NULL;

-- 2. 修复中文字符编码问题的部门名称
UPDATE departments SET 
    name = '总部',
    description = '公司总部'
WHERE id = 1;

UPDATE departments SET 
    name = '技术部', 
    description = '技术开发部门'
WHERE id = 2;

UPDATE departments SET 
    name = '运维部',
    description = '运维管理部门' 
WHERE id = 3;

UPDATE departments SET 
    name = '网络组',
    description = '网络运维小组'
WHERE id = 4;

UPDATE departments SET 
    name = '系统组',
    description = '系统运维小组'
WHERE id = 5;

UPDATE departments SET 
    name = '测试部门',
    description = '测试部门'
WHERE id = 6;

-- 3. 验证修复结果
SELECT id, name, description, is_active FROM departments ORDER BY id;

-- 4. 显示活跃部门数量
SELECT COUNT(*) as active_departments_count FROM departments WHERE is_active = TRUE; 