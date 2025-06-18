-- 创建设备令牌表用于存储移动设备的FCM推送令牌
-- V7__Create_device_tokens_table.sql

CREATE TABLE device_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '设备令牌ID',
    user_id INT NOT NULL COMMENT '用户ID',
    fcm_token VARCHAR(255) NOT NULL COMMENT 'FCM推送令牌',
    device_type ENUM('ANDROID', 'IOS', 'WEB') NOT NULL COMMENT '设备类型',
    device_info VARCHAR(500) DEFAULT NULL COMMENT '设备信息',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否活跃',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_used_at TIMESTAMP NULL DEFAULT NULL COMMENT '最后使用时间',
    
    -- 外键约束
    CONSTRAINT fk_device_tokens_user_id 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- 唯一约束
    CONSTRAINT uk_device_tokens_fcm_token 
        UNIQUE (fcm_token),
    
    -- 索引优化
    INDEX idx_device_tokens_user_id (user_id),
    INDEX idx_device_tokens_active (is_active),
    INDEX idx_device_tokens_device_type (device_type),
    INDEX idx_device_tokens_last_used (last_used_at),
    INDEX idx_device_tokens_user_active (user_id, is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='设备推送令牌表';

-- 插入示例数据（可选）
-- INSERT INTO device_tokens (user_id, fcm_token, device_type, device_info, last_used_at) VALUES
-- (1, 'example_fcm_token_admin_android', 'ANDROID', 'Android App Version 1.0 - Pixel 6', NOW()),
-- (2, 'example_fcm_token_manager_android', 'ANDROID', 'Android App Version 1.0 - Samsung Galaxy', NOW()),
-- (3, 'example_fcm_token_operator_android', 'ANDROID', 'Android App Version 1.0 - Xiaomi 13', NOW()); 