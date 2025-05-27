package com.example.opsworkordersystem.entity;

public enum WorkflowStepType {
    DEPARTMENT_REVIEW,  // 部门初审
    MANAGER_APPROVAL,   // 经理审批
    DIRECTOR_APPROVAL,  // 主管审批
    EXECUTION,          // 执行操作
    VERIFICATION,       // 验证确认
    COMPLETION          // 完成确认
} 