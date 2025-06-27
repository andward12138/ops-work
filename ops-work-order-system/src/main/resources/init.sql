-- 创建数据库
CREATE DATABASE IF NOT EXISTS ops_work_order_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ops_work_order_system;

-- 部门表
CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE,
    level INT,
    type VARCHAR(20),
    parent_id INT,
    description TEXT,
    contact_person VARCHAR(50),
    contact_phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (parent_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户表
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    department_id INT,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 部门联系人表
CREATE TABLE department_contacts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    department_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    position VARCHAR(50),
    phone VARCHAR(20) NOT NULL,
    mobile VARCHAR(20),
    email VARCHAR(100),
    is_primary BOOLEAN DEFAULT FALSE,
    is_emergency BOOLEAN DEFAULT FALSE,
    working_hours VARCHAR(50),
    remark TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 部门权限表
CREATE TABLE department_permissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    department_id INT NOT NULL,
    permission_type VARCHAR(50) NOT NULL,
    granted_by VARCHAR(50),
    granted_at DATETIME,
    expires_at DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    remark TEXT,
    UNIQUE KEY unique_dept_perm (department_id, permission_type),
    FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 工单表
CREATE TABLE work_orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_by INT,
    assigned_to INT,
    department VARCHAR(100),
    type VARCHAR(20),
    created_at DATETIME,
    updated_at DATETIME,
    deadline DATETIME,
    is_overdue BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (assigned_to) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 工作流模板表
CREATE TABLE workflow_templates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    template_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    work_order_type VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 工作流模板步骤表
CREATE TABLE workflow_template_steps (
    id INT PRIMARY KEY AUTO_INCREMENT,
    template_id INT NOT NULL,
    step_order INT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    step_type VARCHAR(50) NOT NULL,
    assignee_role VARCHAR(20),
    assignee_department_id INT,
    is_skippable BOOLEAN DEFAULT FALSE,
    is_parallel BOOLEAN DEFAULT FALSE,
    parallel_group INT,
    time_limit_hours INT,
    auto_approve BOOLEAN DEFAULT FALSE,
    conditions TEXT,
    FOREIGN KEY (template_id) REFERENCES workflow_templates(id),
    FOREIGN KEY (assignee_department_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 工作流步骤表
CREATE TABLE workflow_steps (
    id INT PRIMARY KEY AUTO_INCREMENT,
    work_order_id INT NOT NULL,
    step_order INT NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    step_type VARCHAR(50) NOT NULL,
    assignee_id INT,
    assignee_role VARCHAR(20),
    assignee_department_id INT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    started_at DATETIME,
    completed_at DATETIME,
    deadline DATETIME,
    comments TEXT,
    is_skippable BOOLEAN DEFAULT FALSE,
    is_parallel BOOLEAN DEFAULT FALSE,
    parallel_group INT,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
    FOREIGN KEY (assignee_id) REFERENCES users(id),
    FOREIGN KEY (assignee_department_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 审批记录表
CREATE TABLE approval_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    work_order_id INT NOT NULL,
    approver_id INT,
    status VARCHAR(20) NOT NULL,
    comments TEXT,
    approved_at DATETIME,
    created_at DATETIME,
    FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
    FOREIGN KEY (approver_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 操作记录表
CREATE TABLE operation_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    work_order_id INT NOT NULL,
    operator_id INT,
    action VARCHAR(20) NOT NULL,
    comments TEXT,
    created_at DATETIME,
    FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
    FOREIGN KEY (operator_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 转派记录表
CREATE TABLE transfer_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    work_order_id INT NOT NULL,
    from_user_id INT,
    to_user_id INT,
    from_department_id INT,
    to_department_id INT,
    transfer_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    requested_by_id INT NOT NULL,
    requested_at DATETIME,
    accepted_by_id INT,
    accepted_at DATETIME,
    completed_at DATETIME,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
    FOREIGN KEY (from_user_id) REFERENCES users(id),
    FOREIGN KEY (to_user_id) REFERENCES users(id),
    FOREIGN KEY (from_department_id) REFERENCES departments(id),
    FOREIGN KEY (to_department_id) REFERENCES departments(id),
    FOREIGN KEY (requested_by_id) REFERENCES users(id),
    FOREIGN KEY (accepted_by_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 设备令牌表
CREATE TABLE device_tokens (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL,
    device_type VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_used_at DATETIME,
    UNIQUE KEY unique_token (token),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建性能优化索引
CREATE INDEX idx_work_orders_status ON work_orders(status);
CREATE INDEX idx_work_orders_created_at ON work_orders(created_at);
CREATE INDEX idx_work_orders_deadline ON work_orders(deadline);
CREATE INDEX idx_workflow_steps_status ON workflow_steps(status);
CREATE INDEX idx_approval_records_status ON approval_records(status);
CREATE INDEX idx_transfer_records_status ON transfer_records(status);
CREATE INDEX idx_departments_code ON departments(code);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_department_contacts_phone ON department_contacts(phone);

-- 创建默认管理员用户（密码需要根据实际情况修改）
INSERT INTO departments (name, code, level, type, description, is_active, created_at, updated_at)
VALUES ('系统管理部', 'ADMIN', 1, 'ADMINISTRATIVE', '系统管理部门', TRUE, NOW(), NOW());

INSERT INTO users (username, password, role, email, department_id, created_at, updated_at)
VALUES ('admin', '$2a$10$rAM0X6PtXZU0U1WwIQhO2.1Z1nhG6S2CgXWY1YB7REqeKF6jzFE1W', 'ADMIN', 'admin@example.com', 1, NOW(), NOW()); 