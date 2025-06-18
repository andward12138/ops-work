package com.example.opsworkordersystem.dto;

import com.example.opsworkordersystem.entity.Priority;
import com.example.opsworkordersystem.entity.Status;

import java.time.LocalDateTime;

/**
 * 超时预警DTO
 */
public class OverdueWarningDTO {
    private Integer workOrderId;
    private String title;
    private Priority priority;
    private Status status;
    private String assignedToName;
    private String departmentName;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private long overdueHours; // 已超时小时数
    private String warningLevel; // 预警级别：轻微、严重、紧急

    public OverdueWarningDTO() {}

    public OverdueWarningDTO(Integer workOrderId, String title, Priority priority, 
                            Status status, String assignedToName, String departmentName,
                            LocalDateTime deadline, LocalDateTime createdAt) {
        this.workOrderId = workOrderId;
        this.title = title;
        this.priority = priority;
        this.status = status;
        this.assignedToName = assignedToName;
        this.departmentName = departmentName;
        this.deadline = deadline;
        this.createdAt = createdAt;
        
        calculateOverdueInfo();
    }

    // Getters and Setters
    public Integer getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Integer workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        calculateOverdueInfo();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getOverdueHours() {
        return overdueHours;
    }

    public void setOverdueHours(long overdueHours) {
        this.overdueHours = overdueHours;
    }

    public String getWarningLevel() {
        return warningLevel;
    }

    public void setWarningLevel(String warningLevel) {
        this.warningLevel = warningLevel;
    }

    private void calculateOverdueInfo() {
        if (deadline != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(deadline)) {
                // 计算超时小时数
                this.overdueHours = java.time.Duration.between(deadline, now).toHours();
                
                // 根据超时时间和优先级确定预警级别
                if (priority == Priority.HIGH || priority == Priority.URGENT) {
                    if (overdueHours <= 4) {
                        this.warningLevel = "轻微";
                    } else if (overdueHours <= 12) {
                        this.warningLevel = "严重";
                    } else {
                        this.warningLevel = "紧急";
                    }
                } else {
                    if (overdueHours <= 12) {
                        this.warningLevel = "轻微";
                    } else if (overdueHours <= 24) {
                        this.warningLevel = "严重";
                    } else {
                        this.warningLevel = "紧急";
                    }
                }
            } else {
                this.overdueHours = 0;
                this.warningLevel = "正常";
            }
        }
    }

    /**
     * 获取预警级别的CSS类名
     */
    public String getWarningLevelClass() {
        switch (warningLevel) {
            case "轻微": return "text-warning";
            case "严重": return "text-danger";
            case "紧急": return "text-danger fw-bold";
            default: return "text-success";
        }
    }

    /**
     * 获取优先级的中文名称
     */
    public String getPriorityName() {
        if (priority == null) return "未知";
        switch (priority) {
            case LOW: return "低";
            case MEDIUM: return "中";
            case HIGH: return "高";
            case URGENT: return "紧急";
            default: return "未知";
        }
    }

    /**
     * 获取状态的中文名称
     */
    public String getStatusName() {
        if (status == null) return "未知";
        switch (status) {
            case PENDING: return "待处理";
            case APPROVED: return "已批准";
            case IN_PROGRESS: return "处理中";
            case COMPLETED: return "已完成";
            case REJECTED: return "已拒绝";
            default: return "未知";
        }
    }
} 