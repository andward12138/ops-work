package com.example.opsworkordersystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_orders")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority; // 自定义 Priority 枚举类

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;  // 自定义 Status 枚举类

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = true, columnDefinition = "INT")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", nullable = true, columnDefinition = "INT")
    private User assignedTo;

    @Column(name = "department")
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private WorkOrderType workOrderType;  // 工单类型，改为枚举

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deadline")
    private LocalDateTime deadline;
    
    @Column(name = "is_overdue")
    private Boolean isOverdue = Boolean.FALSE;

    // 构造方法、getter、setter

    public WorkOrder() {
        // Default constructor
    }

    public WorkOrder(String title, String description, Priority priority, Status status, User createdBy, User assignedTo, String department) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.department = department;
    }

    // 新的构造方法，包含工单类型
    public WorkOrder(String title, String description, WorkOrderType workOrderType, User createdBy) {
        this.title = title;
        this.description = description;
        this.workOrderType = workOrderType;
        this.createdBy = createdBy;
        this.priority = workOrderType.getDefaultPriority();
        this.status = workOrderType.getDefaultStatus();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculateDeadlineBasedOnType();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public WorkOrderType getWorkOrderType() {
        return workOrderType;
    }

    public void setWorkOrderType(WorkOrderType workOrderType) {
        this.workOrderType = workOrderType;
    }

    // 保持向后兼容的getter和setter
    public String getType() {
        return workOrderType != null ? workOrderType.name() : null;
    }

    public void setType(String type) {
        if (type != null && !type.isEmpty()) {
            try {
                this.workOrderType = WorkOrderType.valueOf(type);
            } catch (IllegalArgumentException e) {
                // 如果字符串不是有效的枚举值，则设为默认值
                this.workOrderType = WorkOrderType.REQUEST;
            }
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }
    
    // 基于工单类型计算截止时间的新方法
    public void calculateDeadlineBasedOnType() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        
        if (this.workOrderType != null) {
            this.deadline = this.createdAt.plusHours(this.workOrderType.getDefaultDeadlineHours());
        } else {
            // 如果没有工单类型，则使用优先级计算
            calculateDeadlineBasedOnPriority();
        }
    }
    
    // 计算截止时间的便捷方法（保持向后兼容）
    public void calculateDeadlineBasedOnPriority() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        
        LocalDateTime now = this.createdAt;
        
        switch (this.priority) {
            case HIGH:
                // 高优先级 - 24小时内
                this.deadline = now.plusHours(24);
                break;
            case MEDIUM:
                // 中优先级 - 3天内
                this.deadline = now.plusDays(3);
                break;
            case LOW:
                // 低优先级 - 7天内
                this.deadline = now.plusDays(7);
                break;
            default:
                // 默认3天
                this.deadline = now.plusDays(3);
        }
    }
    
    // 检查是否已超时
    public boolean checkIfOverdue() {
        if (this.deadline == null) {
            return false;
        }
        
        // 如果工单已经完成或拒绝，则不考虑超时
        if (this.status == Status.COMPLETED || this.status == Status.REJECTED) {
            return false;
        }
        
        boolean isNowOverdue = LocalDateTime.now().isAfter(this.deadline);
        this.isOverdue = isNowOverdue;
        return isNowOverdue;
    }
    
    /**
     * 获取工单类型的显示名称
     */
    public String getWorkOrderTypeDisplayName() {
        return workOrderType != null ? workOrderType.getDisplayName() : "未知类型";
    }
    
    /**
     * 获取工单类型的CSS样式类
     */
    public String getWorkOrderTypeCssClass() {
        return workOrderType != null ? workOrderType.getCssClass() : "badge-secondary";
    }
    
    /**
     * 获取工单类型的图标
     */
    public String getWorkOrderTypeIcon() {
        return workOrderType != null ? workOrderType.getIcon() : "bi-file-text";
    }
    
    /**
     * 检查是否需要审批
     */
    public boolean requiresApproval() {
        return workOrderType != null ? workOrderType.requiresApproval() : true;
    }
    
    /**
     * 检查是否需要立即处理
     */
    public boolean requiresImmediate() {
        return workOrderType != null ? workOrderType.isRequiresImmediate() : false;
    }
}