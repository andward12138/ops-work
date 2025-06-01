package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.TransferRecord;
import com.example.opsworkordersystem.entity.TransferStatus;
import com.example.opsworkordersystem.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransferRecordRepository extends JpaRepository<TransferRecord, Integer> {
    
    // 根据工单查找转派记录 - 优化版本，使用JOIN FETCH
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.acceptedBy ab " +
           "LEFT JOIN FETCH tr.fromUser fu " +
           "LEFT JOIN FETCH tr.toUser tu " +
           "LEFT JOIN FETCH tr.fromDepartment fd " +
           "LEFT JOIN FETCH tr.toDepartment td " +
           "WHERE tr.workOrder = :workOrder " +
           "ORDER BY tr.createdAt DESC")
    List<TransferRecord> findByWorkOrderOrderByCreatedAtDesc(@Param("workOrder") WorkOrder workOrder);
    
    // 根据工单ID查找转派记录 - 优化版本，支持分页
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.acceptedBy ab " +
           "LEFT JOIN FETCH tr.fromUser fu " +
           "LEFT JOIN FETCH tr.toUser tu " +
           "LEFT JOIN FETCH tr.fromDepartment fd " +
           "LEFT JOIN FETCH tr.toDepartment td " +
           "WHERE tr.workOrder.id = :workOrderId " +
           "ORDER BY tr.createdAt DESC")
    List<TransferRecord> findByWorkOrderIdOrderByCreatedAtDesc(@Param("workOrderId") Integer workOrderId);
    
    // 查找待处理的转派记录 - 优化版本
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.toUser tu " +
           "LEFT JOIN FETCH tr.toDepartment td " +
           "WHERE tr.status = :status AND tr.toUser.id = :toUserId " +
           "ORDER BY tr.createdAt DESC")
    List<TransferRecord> findByStatusAndToUserIdOrderByCreatedAtDesc(@Param("status") TransferStatus status, @Param("toUserId") Integer toUserId);
    
    // 查找部门的待处理转派记录 - 优化版本
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.fromUser fu " +
           "LEFT JOIN FETCH tr.toDepartment td " +
           "WHERE tr.status = :status AND tr.toDepartment.id = :toDepartmentId " +
           "ORDER BY tr.createdAt DESC")
    List<TransferRecord> findByStatusAndToDepartmentIdOrderByCreatedAtDesc(@Param("status") TransferStatus status, @Param("toDepartmentId") Integer toDepartmentId);
    
    // 查找用户发起的转派记录 - 优化版本
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.acceptedBy ab " +
           "LEFT JOIN FETCH tr.fromUser fu " +
           "LEFT JOIN FETCH tr.toUser tu " +
           "LEFT JOIN FETCH tr.fromDepartment fd " +
           "LEFT JOIN FETCH tr.toDepartment td " +
           "WHERE tr.requestedBy.id = :requestedById " +
           "ORDER BY tr.createdAt DESC")
    List<TransferRecord> findByRequestedByIdOrderByCreatedAtDesc(@Param("requestedById") Integer requestedById);
    
    // 查找用户接受的转派记录 - 优化版本
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.fromUser fu " +
           "LEFT JOIN FETCH tr.toUser tu " +
           "LEFT JOIN FETCH tr.fromDepartment fd " +
           "LEFT JOIN FETCH tr.toDepartment td " +
           "WHERE tr.acceptedBy.id = :acceptedById " +
           "ORDER BY tr.createdAt DESC")
    List<TransferRecord> findByAcceptedByIdOrderByCreatedAtDesc(@Param("acceptedById") Integer acceptedById);
    
    // 根据状态查找转派记录 - 优化版本
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.acceptedBy ab " +
           "LEFT JOIN FETCH tr.fromUser fu " +
           "LEFT JOIN FETCH tr.toUser tu " +
           "LEFT JOIN FETCH tr.fromDepartment fd " +
           "LEFT JOIN FETCH tr.toDepartment td " +
           "WHERE tr.status = :status " +
           "ORDER BY tr.createdAt DESC")
    List<TransferRecord> findByStatusOrderByCreatedAtDesc(@Param("status") TransferStatus status);
    
    // 查找指定时间范围内的转派记录 - 优化版本
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.acceptedBy ab " +
           "LEFT JOIN FETCH tr.fromUser fu " +
           "LEFT JOIN FETCH tr.toUser tu " +
           "LEFT JOIN FETCH tr.fromDepartment fd " +
           "LEFT JOIN FETCH tr.toDepartment td " +
           "WHERE tr.createdAt BETWEEN :start AND :end " +
           "ORDER BY tr.createdAt DESC")
    List<TransferRecord> findByCreatedAtBetweenOrderByCreatedAtDesc(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // 分页查询所有转派记录 - 新增高性能版本
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.acceptedBy ab " +
           "LEFT JOIN FETCH tr.fromUser fu " +
           "LEFT JOIN FETCH tr.toUser tu " +
           "LEFT JOIN FETCH tr.fromDepartment fd " +
           "LEFT JOIN FETCH tr.toDepartment td " +
           "ORDER BY tr.createdAt DESC")
    Page<TransferRecord> findAllWithDetails(Pageable pageable);
    
    // 统计转派记录数量 - 使用索引优化
    @Query("SELECT COUNT(tr.id) FROM TransferRecord tr WHERE tr.status = :status")
    long countByStatus(@Param("status") TransferStatus status);
    
    // 统计用户发起的转派记录数量 - 使用索引优化
    @Query("SELECT COUNT(tr.id) FROM TransferRecord tr WHERE tr.requestedBy.id = :requestedById")
    long countByRequestedById(@Param("requestedById") Integer requestedById);
    
    // 统计部门相关的转派记录数量 - 使用索引优化
    @Query("SELECT COUNT(tr.id) FROM TransferRecord tr WHERE tr.fromDepartment.id = :departmentId OR tr.toDepartment.id = :departmentId")
    long countByDepartmentId(@Param("departmentId") Integer departmentId);
    
    // 查找协助处理记录 - 优化版本
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.toUser tu " +
           "WHERE tr.isAssistance = true " +
           "ORDER BY tr.createdAt DESC")
    List<TransferRecord> findByIsAssistanceTrueOrderByCreatedAtDesc();
    
    // 查找工单的最新转派记录 - 优化版本，支持限制数量
    @Query("SELECT tr FROM TransferRecord tr " +
           "LEFT JOIN FETCH tr.workOrder wo " +
           "LEFT JOIN FETCH tr.requestedBy rb " +
           "LEFT JOIN FETCH tr.acceptedBy ab " +
           "LEFT JOIN FETCH tr.fromUser fu " +
           "LEFT JOIN FETCH tr.toUser tu " +
           "LEFT JOIN FETCH tr.fromDepartment fd " +
           "LEFT JOIN FETCH tr.toDepartment td " +
           "WHERE tr.workOrder.id = :workOrderId " +
           "ORDER BY tr.createdAt DESC")
    List<TransferRecord> findLatestByWorkOrderId(@Param("workOrderId") Integer workOrderId);
    
    // 批量统计 - 新增高性能统计查询
    @Query("SELECT tr.status, COUNT(tr.id) FROM TransferRecord tr GROUP BY tr.status")
    List<Object[]> getStatusStatistics();
    
    // 用户相关的待处理转派数量 - 高性能版本
    @Query("SELECT COUNT(tr.id) FROM TransferRecord tr WHERE tr.status = 'PENDING' AND tr.toUser.id = :userId")
    long countPendingByUserId(@Param("userId") Integer userId);
    
    // 部门相关的待处理转派数量 - 高性能版本
    @Query("SELECT COUNT(tr.id) FROM TransferRecord tr WHERE tr.status = 'PENDING' AND tr.toDepartment.id = :departmentId")
    long countPendingByDepartmentId(@Param("departmentId") Integer departmentId);
} 