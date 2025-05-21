package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {
    // 根据工单ID查询审批记录
    List<ApprovalRecord> findByWorkOrderId(Long workOrderId);
    
    // 预加载审批人的查询
    @Query("SELECT a FROM ApprovalRecord a LEFT JOIN FETCH a.approver WHERE a.workOrder.id = ?1")
    List<ApprovalRecord> findByWorkOrderIdWithApprover(Long workOrderId);
}
