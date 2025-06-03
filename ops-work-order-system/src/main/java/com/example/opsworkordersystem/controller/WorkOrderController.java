package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.ApprovalRecord;
import com.example.opsworkordersystem.entity.OperationRecord;
import com.example.opsworkordersystem.entity.Priority;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.entity.WorkOrderType;
import com.example.opsworkordersystem.repository.UserRepository;
import com.example.opsworkordersystem.service.ApprovalRecordService;
import com.example.opsworkordersystem.service.OperationRecordService;
import com.example.opsworkordersystem.service.WorkOrderService;
import com.example.opsworkordersystem.service.WorkOrderTypeService;
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
import java.util.HashMap;

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
    
    @Autowired
    private WorkOrderTypeService workOrderTypeService;

    // 显示工单列表页面
    @GetMapping
    public String listWorkOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keywords,
            Model model) {
        
        List<WorkOrder> workOrders;
        
        // 根据状态过滤工单，使用预加载查询
        if (status != null && !status.isEmpty()) {
            try {
                Status statusEnum = Status.valueOf(status);
                workOrders = workOrderService.getWorkOrdersByStatus(statusEnum); // 已使用预加载
            } catch (IllegalArgumentException e) {
                // 如果状态参数不合法，则返回所有工单
                workOrders = workOrderService.getAllWorkOrders(); // 已使用预加载
            }
        } else {
            // 如果没有状态参数，则返回所有工单
            workOrders = workOrderService.getAllWorkOrders(); // 已使用预加载
        }
        
        // 根据工单类型过滤
        if (type != null && !type.isEmpty() && !workOrders.isEmpty()) {
            try {
                WorkOrderType typeEnum = WorkOrderType.valueOf(type);
                workOrders = workOrders.stream()
                    .filter(wo -> wo.getWorkOrderType() == typeEnum)
                    .toList();
            } catch (IllegalArgumentException e) {
                // 类型参数不合法，不做处理
            }
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
        model.addAttribute("typeFilter", type);
        model.addAttribute("keywordsFilter", keywords);
        model.addAttribute("workOrders", workOrders);
        
        return "work-orders/list";
    }
    
    // 显示工单详情页面
    @GetMapping("/{id}")
    public String getWorkOrderDetails(@PathVariable Integer id, Model model) {
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
    public ResponseEntity<?> createWorkOrder(@RequestBody Map<String, Object> payload) {
        try {
            // 记录收到的请求数据
            System.out.println("接收到创建工单请求，数据: " + payload);
            
            // 检查必填字段
            if (payload.get("title") == null || payload.get("description") == null) {
                System.out.println("请求缺少必要字段: title=" + payload.get("title") + ", description=" + payload.get("description"));
                throw new IllegalArgumentException("缺少必要字段: 标题和描述是必填项");
            }
            
            WorkOrder workOrder = new WorkOrder();
            
            // 设置基本信息
            workOrder.setTitle((String) payload.get("title"));
            workOrder.setDescription((String) payload.get("description"));
            
            // 处理工单类型字段
            String workOrderTypeStr = (String) payload.get("workOrderType");
            WorkOrderType workOrderType = null;
            System.out.println("WorkOrderType字段值: '" + workOrderTypeStr + "'");
            if (workOrderTypeStr != null && !workOrderTypeStr.isEmpty()) {
                try {
                    workOrderType = WorkOrderType.valueOf(workOrderTypeStr.trim().toUpperCase());
                    workOrder.setWorkOrderType(workOrderType);
                    System.out.println("成功设置工单类型: " + workOrderType);
                    
                    // 根据工单类型设置默认属性
                    workOrderTypeService.setDefaultPropertiesByType(workOrder, workOrderType);
                } catch (IllegalArgumentException e) {
                    System.out.println("无效的工单类型值: '" + workOrderTypeStr + "'");
                    throw new IllegalArgumentException("无效的工单类型值: " + workOrderTypeStr);
                }
            }
            
            // 处理优先级字段（如果前端手动修改了优先级，则覆盖默认值）
            String priorityStr = (String) payload.get("priority");
            System.out.println("Priority字段值: '" + priorityStr + "'");
            if (priorityStr != null && !priorityStr.isEmpty()) {
                try {
                    Priority priority = Priority.valueOf(priorityStr.trim().toUpperCase());
                    workOrder.setPriority(priority);
                    System.out.println("成功设置优先级: " + priority);
                } catch (IllegalArgumentException e) {
                    System.out.println("无效的优先级值: '" + priorityStr + "'");
                    throw new IllegalArgumentException("无效的优先级值: " + priorityStr);
                }
            } else if (workOrderType == null) {
                // 只有在没有设置工单类型的情况下才使用默认优先级
                System.out.println("未提供优先级，使用默认值: MEDIUM");
                workOrder.setPriority(Priority.MEDIUM);
            }
            
            // 处理状态字段
            String statusStr = (String) payload.get("status");
            if (statusStr != null && !statusStr.isEmpty()) {
                workOrder.setStatus(Status.valueOf(statusStr));
            } else if (workOrderType != null) {
                // 如果设置了工单类型，使用类型的默认状态
                workOrder.setStatus(workOrderType.getDefaultStatus());
            } else {
                // 默认状态
                workOrder.setStatus(Status.PENDING);
            }
            
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
                    // 如果解析失败，根据工单类型或优先级计算默认截止时间
                    if (workOrderType != null) {
                        workOrder.calculateDeadlineBasedOnType();
                    } else {
                        workOrder.calculateDeadlineBasedOnPriority();
                    }
                }
            } else {
                // 没有自定义截止时间，根据工单类型或优先级计算默认截止时间
                if (workOrderType != null) {
                    workOrder.calculateDeadlineBasedOnType();
                } else {
                    workOrder.calculateDeadlineBasedOnPriority();
                }
            }
            
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            workOrder.setCreatedAt(now);
            workOrder.setUpdatedAt(now);
            
            WorkOrder createdWorkOrder = workOrderService.createWorkOrder(workOrder);
            return ResponseEntity.ok(createdWorkOrder);
        } catch (Exception e) {
            // 记录错误并返回400错误响应
            System.err.println("创建工单时发生错误: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "创建工单失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // API 更新工单状态
    @PutMapping("/api/{id}/status")
    @ResponseBody
    public ResponseEntity<WorkOrder> updateWorkOrderStatus(
            @PathVariable Integer id, 
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
    public List<WorkOrder> getWorkOrdersByCreator(@PathVariable Integer createdById) {
        return workOrderService.getWorkOrdersByCreator(createdById);
    }
}
