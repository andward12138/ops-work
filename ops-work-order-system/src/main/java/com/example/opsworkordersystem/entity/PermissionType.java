package com.example.opsworkordersystem.entity;

public enum PermissionType {
    // 工单相关权限
    CREATE_WORK_ORDER,      // 创建工单
    VIEW_WORK_ORDER,        // 查看工单
    EDIT_WORK_ORDER,        // 编辑工单
    DELETE_WORK_ORDER,      // 删除工单
    ASSIGN_WORK_ORDER,      // 分配工单
    
    // 审批相关权限
    APPROVE_WORK_ORDER,     // 审批工单
    REJECT_WORK_ORDER,      // 拒绝工单
    REASSIGN_WORK_ORDER,    // 重新分配工单
    
    // 部门管理权限
    MANAGE_DEPARTMENT,      // 管理部门
    VIEW_DEPARTMENT,        // 查看部门
    MANAGE_CONTACTS,        // 管理联系人
    
    // 用户管理权限
    MANAGE_USERS,           // 管理用户
    VIEW_USERS,             // 查看用户
    
    // 报表权限
    VIEW_REPORTS,           // 查看报表
    EXPORT_REPORTS,         // 导出报表
    
    // 系统管理权限
    SYSTEM_ADMIN,           // 系统管理
    CONFIG_WORKFLOW         // 配置工作流
} 