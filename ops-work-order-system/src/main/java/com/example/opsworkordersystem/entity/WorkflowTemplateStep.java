package com.example.opsworkordersystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "workflow_template_steps")
public class WorkflowTemplateStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    @JsonBackReference
    private WorkflowTemplate template;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder; // 步骤顺序

    @Column(name = "step_name", nullable = false)
    private String stepName; // 步骤名称

    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false)
    private WorkflowStepType stepType; // 步骤类型

    @Enumerated(EnumType.STRING)
    @Column(name = "assignee_role")
    private Role assigneeRole; // 处理人角色

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_department_id")
    private Department assigneeDepartment; // 处理部门

    @Column(name = "is_skippable")
    private Boolean isSkippable = false; // 是否可跳过

    @Column(name = "is_parallel")
    private Boolean isParallel = false; // 是否并行处理

    @Column(name = "parallel_group")
    private Integer parallelGroup; // 并行组（相同组的步骤并行执行）

    @Column(name = "time_limit_hours")
    private Integer timeLimitHours; // 时限（小时）

    @Column(name = "auto_approve")
    private Boolean autoApprove = false; // 是否自动通过（超时后）

    @Column(name = "conditions")
    private String conditions; // 执行条件（JSON格式）

    // 构造方法
    public WorkflowTemplateStep() {}

    public WorkflowTemplateStep(WorkflowTemplate template, Integer stepOrder, String stepName, 
                               WorkflowStepType stepType) {
        this.template = template;
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

    public WorkflowTemplate getTemplate() {
        return template;
    }

    public void setTemplate(WorkflowTemplate template) {
        this.template = template;
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

    public Integer getParallelGroup() {
        return parallelGroup;
    }

    public void setParallelGroup(Integer parallelGroup) {
        this.parallelGroup = parallelGroup;
    }

    public Integer getTimeLimitHours() {
        return timeLimitHours;
    }

    public void setTimeLimitHours(Integer timeLimitHours) {
        this.timeLimitHours = timeLimitHours;
    }

    public Boolean getAutoApprove() {
        return autoApprove;
    }

    public void setAutoApprove(Boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
} 