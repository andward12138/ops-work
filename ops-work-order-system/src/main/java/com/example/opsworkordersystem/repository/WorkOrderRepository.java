package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Integer> {
    List<WorkOrder> findByStatus(Status status);
    List<WorkOrder> findByCreatedById(Integer userId);
    List<WorkOrder> findByAssignedToId(Integer userId);
    List<WorkOrder> findByAssignedToIdAndStatus(Integer userId, Status status);
    
    // 根据创建时间范围查询
    List<WorkOrder> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // 根据状态和创建时间范围查询
    List<WorkOrder> findByStatusAndCreatedAtBetween(Status status, LocalDateTime start, LocalDateTime end);
    
    // 根据分配部门和创建时间范围查询
    @Query("SELECT w FROM WorkOrder w WHERE w.assignedTo.department.id = :departmentId AND w.createdAt BETWEEN :start AND :end")
    List<WorkOrder> findByAssignedToDepartmentAndCreatedAtBetween(@Param("departmentId") Integer departmentId, 
                                                                  @Param("start") LocalDateTime start, 
                                                                  @Param("end") LocalDateTime end);
    
    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.assignedTo")
    List<WorkOrder> findAllWithUsers();
    
    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.assignedTo WHERE w.status = ?1")
    List<WorkOrder> findByStatusWithUsers(Status status);
    
    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.assignedTo WHERE w.id = ?1")
    Optional<WorkOrder> findByIdWithUsers(Integer id);
    
    // 用于在保存后直接返回带有预加载关联的工单
    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.assignedTo WHERE w.id = ?1")
    Optional<WorkOrder> findSavedWorkOrderById(Integer id);
} 