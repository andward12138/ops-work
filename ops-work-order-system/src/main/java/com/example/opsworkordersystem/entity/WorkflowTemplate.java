package com.example.opsworkordersystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workflow_templates")
public class WorkflowTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "template_name", nullable = false, unique = true)
    private String templateName; // 模板名称

    @Column(name = "description")
    private String description; // 模板描述

    @Column(name = "work_order_type")
    private String workOrderType; // 适用的工单类型

    @Column(name = "is_active")
    private Boolean isActive = true; // 是否激活

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("stepOrder ASC")
    private List<WorkflowTemplateStep> steps = new ArrayList<>(); // 模板步骤

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 构造方法
    public WorkflowTemplate() {}

    public WorkflowTemplate(String templateName, String description, String workOrderType) {
        this.templateName = templateName;
        this.description = description;
        this.workOrderType = workOrderType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 业务方法
    public void addStep(WorkflowTemplateStep step) {
        steps.add(step);
        step.setTemplate(this);
    }

    public void removeStep(WorkflowTemplateStep step) {
        steps.remove(step);
        step.setTemplate(null);
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkOrderType() {
        return workOrderType;
    }

    public void setWorkOrderType(String workOrderType) {
        this.workOrderType = workOrderType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<WorkflowTemplateStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowTemplateStep> steps) {
        this.steps = steps;
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
} 