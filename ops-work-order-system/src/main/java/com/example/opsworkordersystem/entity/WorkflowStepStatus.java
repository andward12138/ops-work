package com.example.opsworkordersystem.entity;

public enum WorkflowStepStatus {
    PENDING,      // 待处理
    IN_PROGRESS,  // 处理中
    COMPLETED,    // 已完成
    REJECTED,     // 已拒绝
    SKIPPED       // 已跳过
} 