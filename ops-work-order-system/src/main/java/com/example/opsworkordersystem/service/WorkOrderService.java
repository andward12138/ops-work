package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.entity.Status;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.entity.WorkflowStep;
import com.example.opsworkordersystem.repository.UserRepository;
import com.example.opsworkordersystem.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WorkflowService workflowService;

    // 创建工单
    @Transactional
    @CacheEvict(value = {"workOrders", "statistics"}, allEntries = true)
    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        // 设置创建时间和更新时间
        if (workOrder.getCreatedAt() == null) {
            workOrder.setCreatedAt(LocalDateTime.now());
        }
        workOrder.setUpdatedAt(LocalDateTime.now());
        
        // 如果未设置状态，默认设为待处理
        if (workOrder.getStatus() == null) {
            workOrder.setStatus(Status.PENDING);
        }
        
        // 确保isOverdue不为null
        if (workOrder.getIsOverdue() == null) {
            workOrder.setIsOverdue(Boolean.FALSE);
        }
        
        // 如果有截止时间，检查是否已超时
        if (workOrder.getDeadline() != null) {
            workOrder.checkIfOverdue();
        }
        
        // 确保createdBy和assignedTo关系在事务内加载完全
        if (workOrder.getCreatedBy() != null) {
            Integer createdById = workOrder.getCreatedBy().getId();
            if (createdById != null) {
                User createdBy = userRepository.findById(createdById)
                    .orElse(workOrder.getCreatedBy());
                workOrder.setCreatedBy(createdBy);
            }
        }
        
        if (workOrder.getAssignedTo() != null) {
            Integer assignedToId = workOrder.getAssignedTo().getId();
            if (assignedToId != null) {
                User assignedTo = userRepository.findById(assignedToId)
                    .orElse(workOrder.getAssignedTo());
                workOrder.setAssignedTo(assignedTo);
            }
        }
        
        // 保存工单
        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
        
        // 初始化工作流
        if (savedWorkOrder.getType() != null) {
            try {
                List<WorkflowStep> workflowSteps = workflowService.initializeWorkflow(savedWorkOrder, savedWorkOrder.getType());
                // 工作流初始化成功
                if (!workflowSteps.isEmpty()) {
                    // 更新工单状态为审批中
                    savedWorkOrder.setStatus(Status.IN_PROGRESS);
                    savedWorkOrder = workOrderRepository.save(savedWorkOrder);
                }
            } catch (Exception e) {
                // 如果没有找到适配的工作流模板，继续正常处理
                System.err.println("工作流初始化失败: " + e.getMessage());
            }
        }
        
        // 使用预加载的查询方法获取完整的工单（包括关联关系）
        return workOrderRepository.findSavedWorkOrderById(savedWorkOrder.getId())
                .orElse(savedWorkOrder);
    }

    // 根据状态查询工单
    @Cacheable(value = "workOrders", key = "'status_' + #status")
    public List<WorkOrder> getWorkOrdersByStatus(Status status) {
        return workOrderRepository.findByStatusWithUsers(status);  // 使用预加载的方法
    }

    // 根据创建人查询工单
    public List<WorkOrder> getWorkOrdersByCreator(Integer createdById) {
        return workOrderRepository.findByCreatedById(createdById);
    }
    
    // 根据ID获取工单详情
    @Cacheable(value = "workOrders", key = "'id_' + #id")
    public Optional<WorkOrder> getWorkOrderById(Integer id) {
        return workOrderRepository.findByIdWithUsers(id);
    }
    
    // 获取所有工单
    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAllWithUsers();  // 使用预加载的方法
    }
    
    // 更新工单状态
    @CacheEvict(value = {"workOrders", "statistics"}, allEntries = true)
    public WorkOrder updateWorkOrderStatus(Integer id, Status status) {
        Optional<WorkOrder> workOrderOpt = workOrderRepository.findByIdWithUsers(id);
        if (workOrderOpt.isPresent()) {
            WorkOrder workOrder = workOrderOpt.get();
            workOrder.setStatus(status);
            workOrder.setUpdatedAt(LocalDateTime.now());
            return workOrderRepository.save(workOrder);
        }
        return null;
    }
    
    // 根据用户名和状态查询工单
    public List<WorkOrder> getWorkOrdersAssignedToUserByStatus(String username, Status status) {
        // 先查询用户ID
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return workOrderRepository.findByAssignedToIdAndStatus(user.getId(), status);
        }
        return Collections.emptyList();
    }
    
    // 检查并更新超时工单状态 - 优化版本，使用分页避免内存溢出
    @CacheEvict(value = {"workOrders", "statistics"}, allEntries = true)
    public List<WorkOrder> checkAndUpdateOverdueWorkOrders() {
        // 使用分页查询避免一次性加载所有数据
        Pageable pageable = PageRequest.of(0, 1000);
        List<WorkOrder> allActiveWorkOrders = workOrderRepository.findAll(pageable)
            .getContent()
            .stream()
            .filter(wo -> wo.getStatus() != Status.COMPLETED && wo.getStatus() != Status.REJECTED)
            .toList();
        
        List<WorkOrder> overdueWorkOrders = new ArrayList<>();
        
        for (WorkOrder workOrder : allActiveWorkOrders) {
            if (workOrder.getDeadline() != null && LocalDateTime.now().isAfter(workOrder.getDeadline())) {
                workOrder.setIsOverdue(true);
                // 如果工单已超时，且状态不是OVERDUE，则更新状态
                if (workOrder.getStatus() != Status.OVERDUE) {
                    workOrder.setStatus(Status.OVERDUE);
                    workOrder.setUpdatedAt(LocalDateTime.now());
                    
                    // 将工单添加到超时工单列表
                    overdueWorkOrders.add(workOrderRepository.save(workOrder));
                }
            }
        }
        
        return overdueWorkOrders;
    }
    
    // 获取超时工单
    public List<WorkOrder> getOverdueWorkOrders() {
        return workOrderRepository.findByStatus(Status.OVERDUE);
    }
}
