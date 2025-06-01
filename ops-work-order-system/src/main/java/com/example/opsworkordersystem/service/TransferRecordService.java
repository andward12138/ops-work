package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.dto.TransferRecordDTO;
import com.example.opsworkordersystem.entity.*;
import com.example.opsworkordersystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransferRecordService {

    @Autowired
    private TransferRecordRepository transferRecordRepository;
    
    @Autowired
    private WorkOrderRepository workOrderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 创建转派记录
     */
    public TransferRecord createTransferRecord(Integer workOrderId, Integer requestedById, 
                                             TransferType transferType, String transferReason,
                                             Integer toUserId, Integer toDepartmentId, 
                                             Boolean isAssistance) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
            .orElseThrow(() -> new RuntimeException("工单不存在: " + workOrderId));
        
        User requestedBy = userRepository.findById(requestedById)
            .orElseThrow(() -> new RuntimeException("请求用户不存在: " + requestedById));
        
        TransferRecord transferRecord = new TransferRecord(workOrder, requestedBy, transferType);
        transferRecord.setTransferReason(transferReason);
        transferRecord.setIsAssistance(isAssistance != null ? isAssistance : false);
        
        // 设置原始信息
        transferRecord.setFromUser(workOrder.getAssignedTo());
        if (workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getDepartment() != null) {
            transferRecord.setFromDepartment(workOrder.getAssignedTo().getDepartment());
        }
        
        // 设置目标信息
        if (toUserId != null) {
            User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("目标用户不存在: " + toUserId));
            transferRecord.setToUser(toUser);
            if (toUser.getDepartment() != null) {
                transferRecord.setToDepartment(toUser.getDepartment());
            }
        } else if (toDepartmentId != null) {
            Department toDepartment = departmentRepository.findById(toDepartmentId)
                .orElseThrow(() -> new RuntimeException("目标部门不存在: " + toDepartmentId));
            transferRecord.setToDepartment(toDepartment);
        }
        
        return transferRecordRepository.save(transferRecord);
    }

    /**
     * 接受转派
     */
    public TransferRecord acceptTransfer(Integer transferRecordId, Integer acceptedById) {
        TransferRecord transferRecord = transferRecordRepository.findById(transferRecordId)
            .orElseThrow(() -> new RuntimeException("转派记录不存在: " + transferRecordId));
        
        if (transferRecord.getStatus() != TransferStatus.PENDING) {
            throw new RuntimeException("转派记录状态不正确，无法接受");
        }
        
        User acceptedBy = userRepository.findById(acceptedById)
            .orElseThrow(() -> new RuntimeException("接受用户不存在: " + acceptedById));
        
        transferRecord.acceptTransfer(acceptedBy);
        
        // 更新工单分配
        WorkOrder workOrder = transferRecord.getWorkOrder();
        if (transferRecord.getToUser() != null) {
            workOrder.setAssignedTo(transferRecord.getToUser());
        }
        workOrderRepository.save(workOrder);
        
        return transferRecordRepository.save(transferRecord);
    }

    /**
     * 拒绝转派
     */
    public TransferRecord rejectTransfer(Integer transferRecordId) {
        TransferRecord transferRecord = transferRecordRepository.findById(transferRecordId)
            .orElseThrow(() -> new RuntimeException("转派记录不存在: " + transferRecordId));
        
        if (transferRecord.getStatus() != TransferStatus.PENDING) {
            throw new RuntimeException("转派记录状态不正确，无法拒绝");
        }
        
        transferRecord.rejectTransfer();
        return transferRecordRepository.save(transferRecord);
    }

    /**
     * 完成转派
     */
    public TransferRecord completeTransfer(Integer transferRecordId) {
        TransferRecord transferRecord = transferRecordRepository.findById(transferRecordId)
            .orElseThrow(() -> new RuntimeException("转派记录不存在: " + transferRecordId));
        
        if (transferRecord.getStatus() != TransferStatus.ACCEPTED) {
            throw new RuntimeException("转派记录状态不正确，无法完成");
        }
        
        transferRecord.completeTransfer();
        return transferRecordRepository.save(transferRecord);
    }

    /**
     * 取消转派
     */
    public TransferRecord cancelTransfer(Integer transferRecordId) {
        TransferRecord transferRecord = transferRecordRepository.findById(transferRecordId)
            .orElseThrow(() -> new RuntimeException("转派记录不存在: " + transferRecordId));
        
        if (transferRecord.getStatus() != TransferStatus.PENDING) {
            throw new RuntimeException("只能取消待处理的转派记录");
        }
        
        transferRecord.cancelTransfer();
        return transferRecordRepository.save(transferRecord);
    }

    /**
     * 获取工单的转派历史 - 优化版本，限制查询数量
     */
    @Transactional(readOnly = true)
    public List<TransferRecordDTO> getWorkOrderTransferHistory(Integer workOrderId) {
        // 只获取最近10条记录，避免查询过多数据
        Pageable pageable = PageRequest.of(0, 10);
        List<TransferRecord> transferRecords = transferRecordRepository.findByWorkOrderIdOrderByCreatedAtDesc(workOrderId);
        
        // 限制返回数量
        return transferRecords.stream()
            .limit(10)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 获取用户的待处理转派记录 - 优化版本
     */
    @Transactional(readOnly = true)
    public List<TransferRecordDTO> getPendingTransfersForUser(Integer userId) {
        List<TransferRecord> transferRecords = transferRecordRepository.findByStatusAndToUserIdOrderByCreatedAtDesc(
            TransferStatus.PENDING, userId);
        
        // 限制返回数量，避免过多数据
        return transferRecords.stream()
            .limit(20)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 获取部门的待处理转派记录 - 优化版本
     */
    @Transactional(readOnly = true)
    public List<TransferRecordDTO> getPendingTransfersForDepartment(Integer departmentId) {
        List<TransferRecord> transferRecords = transferRecordRepository.findByStatusAndToDepartmentIdOrderByCreatedAtDesc(
            TransferStatus.PENDING, departmentId);
        
        // 限制返回数量
        return transferRecords.stream()
            .limit(20)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 获取用户发起的转派记录 - 优化版本
     */
    @Transactional(readOnly = true)
    public List<TransferRecordDTO> getUserRequestedTransfers(Integer userId) {
        List<TransferRecord> transferRecords = transferRecordRepository.findByRequestedByIdOrderByCreatedAtDesc(userId);
        
        // 限制返回数量
        return transferRecords.stream()
            .limit(20)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 获取所有转派记录 - 优化版本，使用分页
     */
    @Transactional(readOnly = true)
    public List<TransferRecordDTO> getAllTransferRecords() {
        // 使用新的高性能分页查询
        Pageable pageable = PageRequest.of(0, 50);
        return transferRecordRepository.findAllWithDetails(pageable)
            .getContent()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 获取转派统计信息 - 高性能批量统计版本
     */
    @Transactional(readOnly = true)
    public TransferStatisticsDTO getTransferStatistics() {
        TransferStatisticsDTO stats = new TransferStatisticsDTO();
        
        // 使用批量统计查询，一次查询获取所有状态统计
        List<Object[]> statusStats = transferRecordRepository.getStatusStatistics();
        
        // 初始化所有统计为0
        stats.setPendingCount(0);
        stats.setAcceptedCount(0);
        stats.setCompletedCount(0);
        stats.setRejectedCount(0);
        stats.setCancelledCount(0);
        
        // 处理查询结果
        for (Object[] stat : statusStats) {
            TransferStatus status = (TransferStatus) stat[0];
            Long count = (Long) stat[1];
            
            switch (status) {
                case PENDING:
                    stats.setPendingCount(count);
                    break;
                case ACCEPTED:
                    stats.setAcceptedCount(count);
                    break;
                case COMPLETED:
                    stats.setCompletedCount(count);
                    break;
                case REJECTED:
                    stats.setRejectedCount(count);
                    break;
                case CANCELLED:
                    stats.setCancelledCount(count);
                    break;
            }
        }
        
        return stats;
    }

    /**
     * 权限检查：是否可以转派工单
     */
    public boolean canTransferWorkOrder(Integer workOrderId, Integer userId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElse(null);
        if (workOrder == null) return false;
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;
        
        // 检查是否是工单创建者、分配者或管理员
        return workOrder.getCreatedBy().getId().equals(userId) ||
               (workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getId().equals(userId)) ||
               user.getRole() == Role.ADMIN ||
               user.getRole() == Role.MANAGER;
    }

    /**
     * 转换为DTO - 优化版本，减少空值检查
     */
    private TransferRecordDTO convertToDTO(TransferRecord transferRecord) {
        TransferRecordDTO dto = new TransferRecordDTO();
        dto.setId(transferRecord.getId());
        
        // 工单信息
        if (transferRecord.getWorkOrder() != null) {
            dto.setWorkOrderId(transferRecord.getWorkOrder().getId());
            dto.setWorkOrderTitle(transferRecord.getWorkOrder().getTitle());
        }
        
        // 来源信息
        if (transferRecord.getFromUser() != null) {
            dto.setFromUserId(transferRecord.getFromUser().getId());
            dto.setFromUserName(transferRecord.getFromUser().getUsername());
        }
        
        if (transferRecord.getFromDepartment() != null) {
            dto.setFromDepartmentId(transferRecord.getFromDepartment().getId());
            dto.setFromDepartmentName(transferRecord.getFromDepartment().getName());
        }
        
        // 目标信息
        if (transferRecord.getToUser() != null) {
            dto.setToUserId(transferRecord.getToUser().getId());
            dto.setToUserName(transferRecord.getToUser().getUsername());
        }
        
        if (transferRecord.getToDepartment() != null) {
            dto.setToDepartmentId(transferRecord.getToDepartment().getId());
            dto.setToDepartmentName(transferRecord.getToDepartment().getName());
        }
        
        // 基本信息
        dto.setTransferType(transferRecord.getTransferType());
        dto.setTransferReason(transferRecord.getTransferReason());
        dto.setTransferComments(transferRecord.getTransferComments());
        dto.setIsAssistance(transferRecord.getIsAssistance());
        dto.setStatus(transferRecord.getStatus());
        
        // 申请人信息
        if (transferRecord.getRequestedBy() != null) {
            dto.setRequestedById(transferRecord.getRequestedBy().getId());
            dto.setRequestedByName(transferRecord.getRequestedBy().getUsername());
        }
        
        // 接受人信息
        if (transferRecord.getAcceptedBy() != null) {
            dto.setAcceptedById(transferRecord.getAcceptedBy().getId());
            dto.setAcceptedByName(transferRecord.getAcceptedBy().getUsername());
        }
        
        // 时间信息
        dto.setRequestedAt(transferRecord.getRequestedAt());
        dto.setAcceptedAt(transferRecord.getAcceptedAt());
        dto.setCompletedAt(transferRecord.getCompletedAt());
        
        return dto;
    }

    // 内部统计DTO类
    public static class TransferStatisticsDTO {
        private long pendingCount;
        private long acceptedCount;
        private long completedCount;
        private long rejectedCount;
        private long cancelledCount;

        // Getters and Setters
        public long getPendingCount() { return pendingCount; }
        public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }

        public long getAcceptedCount() { return acceptedCount; }
        public void setAcceptedCount(long acceptedCount) { this.acceptedCount = acceptedCount; }

        public long getCompletedCount() { return completedCount; }
        public void setCompletedCount(long completedCount) { this.completedCount = completedCount; }

        public long getRejectedCount() { return rejectedCount; }
        public void setRejectedCount(long rejectedCount) { this.rejectedCount = rejectedCount; }

        public long getCancelledCount() { return cancelledCount; }
        public void setCancelledCount(long cancelledCount) { this.cancelledCount = cancelledCount; }
    }
} 