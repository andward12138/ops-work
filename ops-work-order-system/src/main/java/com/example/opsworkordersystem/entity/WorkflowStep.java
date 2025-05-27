package com.example.opsworkordersystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workflow_steps")
public class WorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder; // 步骤顺序

    @Column(name = "step_name", nullable = false)
    private String stepName; // 步骤名称

    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false)
    private WorkflowStepType stepType; // 步骤类型

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee; // 当前处理人

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_role")
    private Role assigneeRole; // 处理人角色

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_department_id")
    private Department assigneeDepartment; // 处理部门

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkflowStepStatus status = WorkflowStepStatus.PENDING; // 步骤状态

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "comments")
    private String comments; // 处理意见

    @Column(name = "is_skippable")
    private Boolean isSkippable = false; // 是否可跳过

    @Column(name = "is_parallel")
    private Boolean isParallel = false; // 是否并行处理

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 构造方法
    public WorkflowStep() {}

    public WorkflowStep(WorkOrder workOrder, Integer stepOrder, String stepName, 
                       WorkflowStepType stepType) {
        this.workOrder = workOrder;
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.stepType = stepType;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public WorkflowStepType getStepType() {
        return stepType;
    }

    public void setStepType(WorkflowStepType stepType) {
        this.stepType = stepType;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Role getAssigneeRole() {
        return assigneeRole;
    }

    public void setAssigneeRole(Role assigneeRole) {
        this.assigneeRole = assigneeRole;
    }

    public Department getAssigneeDepartment() {
        return assigneeDepartment;
    }

    public void setAssigneeDepartment(Department assigneeDepartment) {
        this.assigneeDepartment = assigneeDepartment;
    }

    public WorkflowStepStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStepStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getIsSkippable() {
        return isSkippable;
    }

    public void setIsSkippable(Boolean isSkippable) {
        this.isSkippable = isSkippable;
    }

    public Boolean getIsParallel() {
        return isParallel;
    }

    public void setIsParallel(Boolean isParallel) {
        this.isParallel = isParallel;
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

    // 业务方法
    public boolean isOverdue() {
        return deadline != null && LocalDateTime.now().isAfter(deadline) && 
               status == WorkflowStepStatus.PENDING;
    }

    public void startStep() {
        this.status = WorkflowStepStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void completeStep(String comments) {
        this.status = WorkflowStepStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.comments = comments;
    }

    public void rejectStep(String comments) {
        this.status = WorkflowStepStatus.REJECTED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.comments = comments;
    }
} 