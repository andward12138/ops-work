package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.WorkflowStep;
import com.example.opsworkordersystem.entity.WorkflowStepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Integer> {
    
    // 根据工单ID查找所有步骤
    List<WorkflowStep> findByWorkOrderIdOrderByStepOrder(Integer workOrderId);
    
    // 根据工单ID和状态查找步骤
    List<WorkflowStep> findByWorkOrderIdAndStatus(Integer workOrderId, WorkflowStepStatus status);
    
    // 根据工单ID和步骤顺序查找
    Optional<WorkflowStep> findByWorkOrderIdAndStepOrder(Integer workOrderId, Integer stepOrder);
    
    // 查找工单的当前待处理步骤
    @Query("SELECT s FROM WorkflowStep s WHERE s.workOrder.id = :workOrderId AND s.status = 'PENDING' ORDER BY s.stepOrder ASC")
    List<WorkflowStep> findPendingStepsByWorkOrderId(@Param("workOrderId") Integer workOrderId);
    
    // 查找某个并行组的所有步骤
    @Query("SELECT s FROM WorkflowStep s WHERE s.workOrder.id = :workOrderId AND s.parallelGroup = :parallelGroup")
    List<WorkflowStep> findByWorkOrderIdAndParallelGroup(@Param("workOrderId") Integer workOrderId, @Param("parallelGroup") Integer parallelGroup);
    
    // 查找用户待处理的步骤
    @Query("SELECT s FROM WorkflowStep s WHERE s.assignee.id = :userId AND s.status = 'PENDING'")
    List<WorkflowStep> findPendingStepsByAssigneeId(@Param("userId") Integer userId);
    
    // 查找部门待处理的步骤
    @Query("SELECT s FROM WorkflowStep s WHERE s.assigneeDepartment.id = :departmentId AND s.status = 'PENDING'")
    List<WorkflowStep> findPendingStepsByDepartmentId(@Param("departmentId") Integer departmentId);
} 