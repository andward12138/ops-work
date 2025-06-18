-- ================================================================
-- 快速测试数据插入脚本
-- ================================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 完全清空所有表
TRUNCATE TABLE workflow_steps;
TRUNCATE TABLE operation_records;
TRUNCATE TABLE approval_records;
TRUNCATE TABLE transfer_records;
TRUNCATE TABLE work_orders;
DELETE FROM users WHERE id > 3;
TRUNCATE TABLE department_contacts;
TRUNCATE TABLE department_permissions;
DELETE FROM departments WHERE id > 6;

-- 重置auto_increment
ALTER TABLE departments AUTO_INCREMENT = 7;
ALTER TABLE users AUTO_INCREMENT = 4;
ALTER TABLE work_orders AUTO_INCREMENT = 1;
ALTER TABLE workflow_steps AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- ================================================================
-- 插入部门数据
-- ================================================================

-- 省级部门 (ID: 7-9)
INSERT INTO departments (name, description, level, type, contact_person, contact_phone, code, is_active, created_at) VALUES
('HeNan Power Company', 'HeNan Power Company HQ', 1, 'PROVINCIAL', 'Manager Li', '0371-12345678', 'PROV101', 1, NOW()),
('HeNan Power Dispatch Center', 'Provincial Dispatch Center', 1, 'OPERATIONAL', 'Director Wang', '0371-12345679', 'PROV102', 1, NOW()),
('HeNan Power Information Center', 'Provincial IT Center', 1, 'SUPPORT', 'Director Zhang', '0371-12345680', 'PROV103', 1, NOW());

-- 市级部门 (ID: 10-14)
INSERT INTO departments (name, description, level, type, parent_id, contact_person, contact_phone, code, is_active, created_at) VALUES
('Zhengzhou Power Supply Company', 'Zhengzhou Power Supply', 2, 'MUNICIPAL', 7, 'Manager Chen', '0371-22345678', 'ZZ101', 1, NOW()),
('Luoyang Power Supply Company', 'Luoyang Power Supply', 2, 'MUNICIPAL', 7, 'Manager Liu', '0379-22345678', 'LY101', 1, NOW()),
('Kaifeng Power Supply Company', 'Kaifeng Power Supply', 2, 'MUNICIPAL', 7, 'Manager Zhao', '0378-22345678', 'KF101', 1, NOW()),
('Zhengzhou Dispatch Sub-center', 'Zhengzhou Dispatch', 2, 'OPERATIONAL', 8, 'Dispatcher Wu', '0371-22345679', 'ZZ102', 1, NOW()),
('Zhengzhou Information Sub-center', 'Zhengzhou IT', 2, 'SUPPORT', 9, 'Supervisor Sun', '0371-22345680', 'ZZ103', 1, NOW());

-- 县级部门 (ID: 15-20)
INSERT INTO departments (name, description, level, type, parent_id, contact_person, contact_phone, code, is_active, created_at) VALUES
('Zhongyuan District Power Station', 'Zhongyuan District', 3, 'COUNTY', 10, 'Chief Ma', '0371-32345678', 'ZY101', 1, NOW()),
('Jinshui District Power Station', 'Jinshui District', 3, 'COUNTY', 10, 'Chief Tian', '0371-32345679', 'JS101', 1, NOW()),
('Erqi District Power Station', 'Erqi District', 3, 'COUNTY', 10, 'Chief He', '0371-32345680', 'EQ101', 1, NOW()),
('Luolong District Power Station', 'Luolong District', 3, 'COUNTY', 11, 'Chief Hu', '0379-32345678', 'LL101', 1, NOW()),
('Xigong District Power Station', 'Xigong District', 3, 'COUNTY', 11, 'Chief Guo', '0379-32345679', 'XG101', 1, NOW()),
('Longting District Power Station', 'Longting District', 3, 'COUNTY', 12, 'Chief Zhu', '0378-32345678', 'LT101', 1, NOW());

-- ================================================================
-- 插入用户数据 (ID: 4-20)
-- ================================================================

INSERT INTO users (username, password, email, role, department_id, created_at) VALUES
-- 省级用户 (ID: 4-6)
('li_zong', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'lizong@power.com', 'ADMIN', 7, NOW()),
('zhang_zr', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'zhangzr@power.com', 'MANAGER', 9, NOW()),
('wang_dd', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'wangdd@power.com', 'MANAGER', 8, NOW()),

-- 市级用户 (ID: 7-11)
('chen_jl', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'chenjl@zz.power.com', 'MANAGER', 10, NOW()),
('liu_wy', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'liuwy@ly.power.com', 'MANAGER', 11, NOW()),
('zhao_hl', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'zhaohl@kf.power.com', 'MANAGER', 12, NOW()),
('wu_dd', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'wudd@zz.power.com', 'OPERATOR', 13, NOW()),
('sun_zg', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'sunzg@zz.power.com', 'OPERATOR', 14, NOW()),

-- 县级用户 (ID: 12-17)
('ma_sz', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'masz@zy.power.com', 'OPERATOR', 15, NOW()),
('tian_sz', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'tiansz@js.power.com', 'OPERATOR', 16, NOW()),
('he_szl', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'heszl@eq.power.com', 'OPERATOR', 17, NOW()),
('hu_sz', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'husz@ll.power.com', 'OPERATOR', 18, NOW()),
('guo_sz', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'guosz@xg.power.com', 'OPERATOR', 19, NOW()),
('zhu_sz', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'zhusz@lt.power.com', 'OPERATOR', 20, NOW()),

-- 技术人员 (ID: 18-20)
('tech_zhang', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'techzhang@power.com', 'OPERATOR', 9, NOW()),
('tech_li', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'techli@zz.power.com', 'OPERATOR', 14, NOW()),
('repair_chen', '$2a$12$rHv.lKmKNE5vHfJoEqBm0uNjK8iCOTUv9Y9ZVD4n5DQM8.f4Zp6Xa', 'repairchen@js.power.com', 'OPERATOR', 16, NOW());

-- ================================================================
-- 插入工单数据 (ID: 1-12)
-- ================================================================

-- 应急工单
INSERT INTO work_orders (title, description, status, priority, type, created_by, assigned_to, department, created_at, deadline, updated_at) VALUES
('Emergency Line Trip', 'Zhongyuan 10kV line tripped, affecting 3000 households', 'COMPLETED', 'HIGH', 'EMERGENCY', 12, 18, 'Zhongyuan District Power Station', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 46 HOUR), DATE_SUB(NOW(), INTERVAL 46 HOUR)),
('Transformer Protection Action', 'Jinshui substation main transformer protection activated', 'COMPLETED', 'HIGH', 'EMERGENCY', 13, 19, 'Jinshui District Power Station', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 22 HOUR), DATE_SUB(NOW(), INTERVAL 22 HOUR)),
('Dispatch System Failure', 'Kaifeng dispatch system communication interrupted', 'IN_PROGRESS', 'HIGH', 'EMERGENCY', 9, 18, 'Kaifeng Power Supply Company', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 1 HOUR), NOW());

-- 故障工单
INSERT INTO work_orders (title, description, status, priority, type, created_by, assigned_to, department, created_at, deadline, updated_at) VALUES
('Distribution Box Temperature High', 'Erqi district distribution box shows high temperature', 'COMPLETED', 'MEDIUM', 'INCIDENT', 14, 20, 'Erqi District Power Station', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 67 HOUR), DATE_SUB(NOW(), INTERVAL 67 HOUR)),
('SCADA Data Abnormal', 'Luoyang SCADA system shows abnormal data', 'COMPLETED', 'MEDIUM', 'INCIDENT', 8, 19, 'Luoyang Power Supply Company', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 113 HOUR), DATE_SUB(NOW(), INTERVAL 113 HOUR)),
('Street Lighting Failure', 'Zhongyuan street lights not working', 'IN_PROGRESS', 'MEDIUM', 'INCIDENT', 12, 18, 'Zhongyuan District Power Station', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 7 HOUR), NOW());

-- 维护工单
INSERT INTO work_orders (title, description, status, priority, type, created_by, assigned_to, department, created_at, deadline, updated_at) VALUES
('Substation Spring Maintenance', 'Jinshui 35kV substation routine maintenance', 'COMPLETED', 'LOW', 'MAINTENANCE', 13, 19, 'Jinshui District Power Station', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 170 HOUR), DATE_SUB(NOW(), INTERVAL 170 HOUR)),
('AC System Maintenance', 'Provincial dispatch center AC maintenance', 'COMPLETED', 'LOW', 'MAINTENANCE', 6, 18, 'HeNan Power Dispatch Center', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 95 HOUR), DATE_SUB(NOW(), INTERVAL 95 HOUR)),
('Line Insulator Cleaning', 'Luolong 10kV line insulator cleaning', 'IN_PROGRESS', 'LOW', 'MAINTENANCE', 16, 16, 'Luolong District Power Station', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 69 HOUR), NOW());

-- 需求工单
INSERT INTO work_orders (title, description, status, priority, type, created_by, assigned_to, department, created_at, deadline, updated_at) VALUES
('Automation System Upgrade', 'Zhengzhou distribution automation upgrade', 'IN_PROGRESS', 'LOW', 'REQUEST', 7, 19, 'Zhengzhou Information Sub-center', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_ADD(NOW(), INTERVAL 153 HOUR), NOW()),
('Display Screen Reform', 'Provincial dispatch screen system upgrade', 'APPROVED', 'LOW', 'REQUEST', 6, 5, 'HeNan Power Information Center', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 161 HOUR), NOW()),
('Smart Meter Optimization', 'Luoyang smart meter data collection optimization', 'PENDING', 'LOW', 'REQUEST', 8, 8, 'Luoyang Power Supply Company', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 165 HOUR), NOW());

-- ================================================================
-- 插入工作流步骤数据
-- ================================================================

INSERT INTO workflow_steps (work_order_id, step_order, step_name, step_type, assignee_id, assignee_department_id, status, created_at, updated_at) VALUES
-- 已完成的审批流程
(7, 1, 'Department Manager Approval', 'MANAGER_APPROVAL', 7, 10, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
(7, 2, 'Safety Department Review', 'DEPARTMENT_REVIEW', 4, 1, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(7, 3, 'Technical Execution', 'EXECUTION', 19, 16, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 170 HOUR)),

(8, 1, 'Information Center Approval', 'DIRECTOR_APPROVAL', 5, 9, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(8, 2, 'Technical Execution', 'EXECUTION', 18, 9, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 95 HOUR)),

-- 待审批流程
(11, 1, 'Information Director Approval', 'DIRECTOR_APPROVAL', 5, 9, 'PENDING', DATE_SUB(NOW(), INTERVAL 7 DAY), NULL),
(11, 2, 'General Manager Approval', 'MANAGER_APPROVAL', 4, 7, 'PENDING', DATE_SUB(NOW(), INTERVAL 7 DAY), NULL),
(11, 3, 'Technical Implementation', 'EXECUTION', 18, 9, 'PENDING', DATE_SUB(NOW(), INTERVAL 7 DAY), NULL);

-- ================================================================
-- 验证数据
-- ================================================================

SELECT 'Test Data Summary:' as info;
SELECT 'Departments:' as type, COUNT(*) as count FROM departments WHERE id > 6;
SELECT 'Users:' as type, COUNT(*) as count FROM users WHERE id > 3;
SELECT 'Work Orders:' as type, COUNT(*) as count FROM work_orders;
SELECT 'Workflow Steps:' as type, COUNT(*) as count FROM workflow_steps;
SELECT 'SUCCESS: Test data inserted!' as result; 