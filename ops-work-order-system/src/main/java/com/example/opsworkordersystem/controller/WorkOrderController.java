package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.ApprovalRecord;
import com.example.opsworkordersystem.entity.OperationRecord;
import com.example.opsworkordersystem.entity.Priority;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.repository.UserRepository;
import com.example.opsworkordersystem.service.ApprovalRecordService;
import com.example.opsworkordersystem.service.OperationRecordService;
import com.example.opsworkordersystem.service.WorkOrderService;
import com.example.opsworkordersystem.entity.Status;  // 引入 Status 枚举
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/work-orders")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;
    
    @Autowired
    private ApprovalRecordService approvalRecordService;
    
    @Autowired
    private OperationRecordService operationRecordService;
    
    @Autowired
    private UserRepository userRepository;

    // 显示工单列表页面
    @GetMapping
    public String listWorkOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String keywords,
            Model model) {
        
        List<WorkOrder> workOrders;
        
        // 根据状态过滤
        if (status != null && !status.isEmpty()) {
            try {
                Status statusEnum = Status.valueOf(status);
                workOrders = workOrderService.getWorkOrdersByStatus(statusEnum);
            } catch (IllegalArgumentException e) {
                // 如果状态参数不合法，则返回所有工单
                workOrders = workOrderService.getAllWorkOrders();
            }
        } else {
            // 如果没有状态参数，则返回所有工单
            workOrders = workOrderService.getAllWorkOrders();
        }
        
        // 根据优先级过滤 (如果状态已经过滤过了，就在过滤结果上继续过滤)
        if (priority != null && !priority.isEmpty() && !workOrders.isEmpty()) {
            try {
                Priority priorityEnum = Priority.valueOf(priority);
                workOrders = workOrders.stream()
                    .filter(wo -> wo.getPriority() == priorityEnum)
                    .toList();
            } catch (IllegalArgumentException e) {
                // 优先级参数不合法，不做处理
            }
        }
        
        // 根据关键词过滤 (标题和描述)
        if (keywords != null && !keywords.isEmpty() && !workOrders.isEmpty()) {
            String lowerKeywords = keywords.toLowerCase();
            workOrders = workOrders.stream()
                .filter(wo -> 
                    (wo.getTitle() != null && wo.getTitle().toLowerCase().contains(lowerKeywords)) || 
                    (wo.getDescription() != null && wo.getDescription().toLowerCase().contains(lowerKeywords))
                )
                .toList();
        }
        
        // 将筛选条件保存到模型中，用于回显到表单
        model.addAttribute("statusFilter", status);
        model.addAttribute("priorityFilter", priority);
        model.addAttribute("keywordsFilter", keywords);
        model.addAttribute("workOrders", workOrders);
        
        return "work-orders/list";
    }
    
    // 显示工单详情页面
    @GetMapping("/{id}")
    public String getWorkOrderDetails(@PathVariable Long id, Model model) {
        Optional<WorkOrder> workOrderOpt = workOrderService.getWorkOrderById(id);
        
        if (workOrderOpt.isPresent()) {
            WorkOrder workOrder = workOrderOpt.get();
            model.addAttribute("workOrder", workOrder);
            
            // 获取审批记录
            List<ApprovalRecord> approvalRecords = approvalRecordService.getApprovalRecordsByWorkOrderId(id);
            model.addAttribute("approvalRecords", approvalRecords);
            
            // 获取操作记录
            List<OperationRecord> operationRecords = operationRecordService.getOperationRecordsByWorkOrderId(id);
            model.addAttribute("operationRecords", operationRecords);
            
            return "work-orders/details";
        } else {
            return "redirect:/work-orders?error=WorkOrderNotFound";
        }
    }

    // API 创建工单
    @PostMapping("/api")
    @ResponseBody
    public WorkOrder createWorkOrder(@RequestBody Map<String, Object> payload) {
        WorkOrder workOrder = new WorkOrder();
        
        // 设置基本信息
        workOrder.setTitle((String) payload.get("title"));
        workOrder.setDescription((String) payload.get("description"));
        workOrder.setPriority(Priority.valueOf((String) payload.get("priority")));
        workOrder.setStatus(Status.valueOf((String) payload.get("status")));
        workOrder.setDepartment((String) payload.get("department"));
        
        // 获取当前登录用户作为创建人
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            Optional<User> currentUser = userRepository.findByUsername(username);
            if (currentUser.isPresent()) {
                workOrder.setCreatedBy(currentUser.get());
            }
        }
        
        // 设置指派的操作人员（如果提供了）
        Object assignedToIdObj = payload.get("assignedToId");
        if (assignedToIdObj != null && !assignedToIdObj.toString().isEmpty()) {
            try {
                Integer assignedToId = Integer.valueOf(assignedToIdObj.toString());
                Optional<User> assignedUser = userRepository.findById(assignedToId);
                if (assignedUser.isPresent()) {
                    workOrder.setAssignedTo(assignedUser.get());
                }
            } catch (NumberFormatException e) {
                // 忽略无效的ID格式
            }
        }
        
        // 处理自定义截止时间
        String deadlineStr = (String) payload.get("deadline");
        if (deadlineStr != null && !deadlineStr.isEmpty()) {
            try {
                LocalDateTime deadline = LocalDateTime.parse(deadlineStr.substring(0, 19)); // 去掉毫秒部分
                workOrder.setDeadline(deadline);
            } catch (Exception e) {
                // 如果解析失败，使用基于优先级的默认截止时间
                workOrder.calculateDeadlineBasedOnPriority();
            }
        } else {
            // 没有自定义截止时间，计算基于优先级的默认截止时间
            workOrder.calculateDeadlineBasedOnPriority();
        }
        
        return workOrderService.createWorkOrder(workOrder);
    }
    
    // API 更新工单状态
    @PutMapping("/api/{id}/status")
    @ResponseBody
    public ResponseEntity<WorkOrder> updateWorkOrderStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        
        String statusStr = request.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Status status = Status.valueOf(statusStr);
            WorkOrder updatedWorkOrder = workOrderService.updateWorkOrderStatus(id, status);
            
            if (updatedWorkOrder != null) {
                return ResponseEntity.ok(updatedWorkOrder);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // API 根据状态查询工单
    @GetMapping("/api/status/{status}")
    @ResponseBody
    public List<WorkOrder> getWorkOrdersByStatus(@PathVariable String status) {
        // 将状态转换为 Status 枚举类型
        Status statusEnum = Status.valueOf(status.toUpperCase());
        return workOrderService.getWorkOrdersByStatus(statusEnum);
    }

    // API 根据创建人查询工单
    @GetMapping("/api/creator/{createdById}")
    @ResponseBody
    public List<WorkOrder> getWorkOrdersByCreator(@PathVariable Long createdById) {
        return workOrderService.getWorkOrdersByCreator(createdById);
    }
}
