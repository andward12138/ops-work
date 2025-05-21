package com.example.opsworkordersystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_records")
public class OperationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)  // 多对一关系，一个操作记录对应一个工单
    @JoinColumn(name = "work_order_id", nullable = false, columnDefinition = "INT")
    private WorkOrder workOrder;  // 操作对应的工单

    @ManyToOne(fetch = FetchType.LAZY)  // 多对一关系，一个操作记录对应一个操作人
    @JoinColumn(name = "operator_id", nullable = true, columnDefinition = "INT")
    private User operator;  // 操作人

    @Enumerated(EnumType.STRING)  // 操作类型，使用枚举
    @Column(name = "action", nullable = false)
    private ActionType action;  // 操作类型（如执行、回单等）

    @Column(name = "comments")
    private String comments;  // 操作备注

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 操作时间

    // 构造方法、getter、setter

    public OperationRecord() {
        // Default constructor
    }

    public OperationRecord(WorkOrder workOrder, User operator, ActionType action, String comments) {
        this.workOrder = workOrder;
        this.operator = operator;
        this.action = action;
        this.comments = comments;
        this.createdAt = LocalDateTime.now();  // 设置当前时间
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

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
