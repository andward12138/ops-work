package com.example.opsworkordersystem.entity;

/**
 * 工单类型枚举
 * 
 * 包含四种工单类型：
 * - INCIDENT: 故障工单 - 系统故障、服务中断等紧急问题
 * - REQUEST: 需求工单 - 功能需求、配置变更等普通请求
 * - MAINTENANCE: 维护工单 - 计划维护、系统升级等预定操作
 * - EMERGENCY: 应急工单 - 紧急事件、安全事故等最高优先级
 */
public enum WorkOrderType {
    
    INCIDENT("故障工单", "系统故障、服务中断等问题处理", 2, true),
    REQUEST("需求工单", "功能需求、配置变更等普通请求", 5, false),
    MAINTENANCE("维护工单", "计划维护、系统升级等预定操作", 7, false),
    EMERGENCY("应急工单", "紧急事件、安全事故等最高优先级", 1, true);
    
    private final String displayName;
    private final String description;
    private final int defaultDeadlineHours; // 默认截止时间（小时）
    private final boolean requiresImmediate; // 是否需要立即处理
    
    WorkOrderType(String displayName, String description, int defaultDeadlineHours, boolean requiresImmediate) {
        this.displayName = displayName;
        this.description = description;
        this.defaultDeadlineHours = defaultDeadlineHours;
        this.requiresImmediate = requiresImmediate;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getDefaultDeadlineHours() {
        return defaultDeadlineHours;
    }
    
    public boolean isRequiresImmediate() {
        return requiresImmediate;
    }
    
    /**
     * 获取工单类型对应的默认优先级
     */
    public Priority getDefaultPriority() {
        switch (this) {
            case EMERGENCY:
                return Priority.URGENT;
            case INCIDENT:
                return Priority.HIGH;
            case MAINTENANCE:
                return Priority.MEDIUM;
            case REQUEST:
                return Priority.MEDIUM;
            default:
                return Priority.MEDIUM;
        }
    }
    
    /**
     * 获取工单类型对应的默认状态
     */
    public Status getDefaultStatus() {
        switch (this) {
            case EMERGENCY:
                return Status.APPROVED; // 应急工单直接进入已批准状态
            case INCIDENT:
                return Status.PENDING; // 故障工单需要审批
            case MAINTENANCE:
                return Status.PENDING; // 维护工单需要审批
            case REQUEST:
                return Status.PENDING; // 需求工单需要审批
            default:
                return Status.PENDING;
        }
    }
    
    /**
     * 检查是否需要审批流程
     */
    public boolean requiresApproval() {
        return this != EMERGENCY; // 应急工单跳过审批
    }
    
    /**
     * 获取工单类型的CSS样式类
     */
    public String getCssClass() {
        switch (this) {
            case EMERGENCY:
                return "badge-danger";
            case INCIDENT:
                return "badge-warning";
            case MAINTENANCE:
                return "badge-info";
            case REQUEST:
                return "badge-secondary";
            default:
                return "badge-secondary";
        }
    }
    
    /**
     * 获取工单类型的图标
     */
    public String getIcon() {
        switch (this) {
            case EMERGENCY:
                return "bi-exclamation-triangle-fill";
            case INCIDENT:
                return "bi-bug-fill";
            case MAINTENANCE:
                return "bi-tools";
            case REQUEST:
                return "bi-lightbulb";
            default:
                return "bi-file-text";
        }
    }
} 