package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByStatus(Status status);
    List<WorkOrder> findByCreatedById(Long userId);
    List<WorkOrder> findByAssignedToId(Long userId);
    List<WorkOrder> findByAssignedToIdAndStatus(Long userId, Status status);
    
    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.assignedTo")
    List<WorkOrder> findAllWithUsers();
    
    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.assignedTo WHERE w.status = ?1")
    List<WorkOrder> findByStatusWithUsers(Status status);
    
    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.assignedTo WHERE w.id = ?1")
    Optional<WorkOrder> findByIdWithUsers(Long id);
} 