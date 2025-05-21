package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.OperationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRecordRepository extends JpaRepository<OperationRecord, Long> {
    // 根据工单 ID 查找操作记录
    List<OperationRecord> findByWorkOrderId(Long workOrderId);

    // 根据操作人 ID 查找操作记录
    List<OperationRecord> findByOperatorId(Long operatorId);
    
    // 预加载操作人的查询
    @Query("SELECT o FROM OperationRecord o LEFT JOIN FETCH o.operator WHERE o.workOrder.id = ?1")
    List<OperationRecord> findByWorkOrderIdWithOperator(Long workOrderId);
}
