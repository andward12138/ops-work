package com.example.opsworkordersystem.dto;

import com.example.opsworkordersystem.entity.TransferStatus;
import com.example.opsworkordersystem.entity.TransferType;
import java.time.LocalDateTime;

public class TransferRecordDTO {
    private Integer id;
    private Integer workOrderId;
    private String workOrderTitle;
    private Integer fromUserId;
    private String fromUserName;
    private Integer fromDepartmentId;
    private String fromDepartmentName;
    private Integer toUserId;
    private String toUserName;
    private Integer toDepartmentId;
    private String toDepartmentName;
    private TransferType transferType;
    private String transferReason;
    private String transferComments;
    private Boolean isAssistance;
    private TransferStatus status;
    private Integer requestedById;
    private String requestedByName;
    private Integer acceptedById;
    private String acceptedByName;
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;

    // 构造方法
    public TransferRecordDTO() {}

    public TransferRecordDTO(Integer id, Integer workOrderId, String workOrderTitle,
                           Integer fromUserId, String fromUserName,
                           Integer fromDepartmentId, String fromDepartmentName,
                           Integer toUserId, String toUserName,
                           Integer toDepartmentId, String toDepartmentName,
                           TransferType transferType, String transferReason,
                           Boolean isAssistance, TransferStatus status,
                           Integer requestedById, String requestedByName,
                           LocalDateTime requestedAt) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.workOrderTitle = workOrderTitle;
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.fromDepartmentId = fromDepartmentId;
        this.fromDepartmentName = fromDepartmentName;
        this.toUserId = toUserId;
        this.toUserName = toUserName;
        this.toDepartmentId = toDepartmentId;
        this.toDepartmentName = toDepartmentName;
        this.transferType = transferType;
        this.transferReason = transferReason;
        this.isAssistance = isAssistance;
        this.status = status;
        this.requestedById = requestedById;
        this.requestedByName = requestedByName;
        this.requestedAt = requestedAt;
    }

    // 获取状态的中文名称
    public String getStatusName() {
        if (status == null) return "未知";
        switch (status) {
            case PENDING: return "待处理";
            case ACCEPTED: return "已接受";
            case REJECTED: return "已拒绝";
            case COMPLETED: return "已完成";
            case CANCELLED: return "已取消";
            default: return "未知";
        }
    }

    // 获取转派类型的中文名称
    public String getTransferTypeName() {
        if (transferType == null) return "未知";
        switch (transferType) {
            case DEPARTMENT_TRANSFER: return "部门转派";
            case USER_TRANSFER: return "用户转派";
            case ASSISTANCE_REQUEST: return "协助请求";
            default: return "未知";
        }
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getWorkOrderId() { return workOrderId; }
    public void setWorkOrderId(Integer workOrderId) { this.workOrderId = workOrderId; }

    public String getWorkOrderTitle() { return workOrderTitle; }
    public void setWorkOrderTitle(String workOrderTitle) { this.workOrderTitle = workOrderTitle; }

    public Integer getFromUserId() { return fromUserId; }
    public void setFromUserId(Integer fromUserId) { this.fromUserId = fromUserId; }

    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }

    public Integer getFromDepartmentId() { return fromDepartmentId; }
    public void setFromDepartmentId(Integer fromDepartmentId) { this.fromDepartmentId = fromDepartmentId; }

    public String getFromDepartmentName() { return fromDepartmentName; }
    public void setFromDepartmentName(String fromDepartmentName) { this.fromDepartmentName = fromDepartmentName; }

    public Integer getToUserId() { return toUserId; }
    public void setToUserId(Integer toUserId) { this.toUserId = toUserId; }

    public String getToUserName() { return toUserName; }
    public void setToUserName(String toUserName) { this.toUserName = toUserName; }

    public Integer getToDepartmentId() { return toDepartmentId; }
    public void setToDepartmentId(Integer toDepartmentId) { this.toDepartmentId = toDepartmentId; }

    public String getToDepartmentName() { return toDepartmentName; }
    public void setToDepartmentName(String toDepartmentName) { this.toDepartmentName = toDepartmentName; }

    public TransferType getTransferType() { return transferType; }
    public void setTransferType(TransferType transferType) { this.transferType = transferType; }

    public String getTransferReason() { return transferReason; }
    public void setTransferReason(String transferReason) { this.transferReason = transferReason; }

    public String getTransferComments() { return transferComments; }
    public void setTransferComments(String transferComments) { this.transferComments = transferComments; }

    public Boolean getIsAssistance() { return isAssistance; }
    public void setIsAssistance(Boolean isAssistance) { this.isAssistance = isAssistance; }

    public TransferStatus getStatus() { return status; }
    public void setStatus(TransferStatus status) { this.status = status; }

    public Integer getRequestedById() { return requestedById; }
    public void setRequestedById(Integer requestedById) { this.requestedById = requestedById; }

    public String getRequestedByName() { return requestedByName; }
    public void setRequestedByName(String requestedByName) { this.requestedByName = requestedByName; }

    public Integer getAcceptedById() { return acceptedById; }
    public void setAcceptedById(Integer acceptedById) { this.acceptedById = acceptedById; }

    public String getAcceptedByName() { return acceptedByName; }
    public void setAcceptedByName(String acceptedByName) { this.acceptedByName = acceptedByName; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
} 