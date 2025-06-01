package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.ApprovalRecord;
import com.example.opsworkordersystem.entity.ApprovalStatus;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.repository.UserRepository;
import com.example.opsworkordersystem.repository.WorkOrderRepository;
import com.example.opsworkordersystem.service.ApprovalRecordService;
import com.example.opsworkordersystem.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/approval-records")
public class ApprovalRecordController {

    @Autowired
    private ApprovalRecordService approvalRecordService;
    
    @Autowired
    private WorkOrderRepository workOrderRepository;
    
    @Autowired
    private WorkOrderService workOrderService;
    
    @Autowired
    private UserRepository userRepository;

    // 根据工单 ID 查询审批记录
    @GetMapping("/work-order/{workOrderId}")
    public List<ApprovalRecord> getApprovalRecordsByWorkOrderId(@PathVariable Integer workOrderId) {
        return approvalRecordService.getApprovalRecordsByWorkOrderId(workOrderId);
    }

    // 创建审批记录
    @PostMapping
    public ApprovalRecord createApprovalRecord(@RequestBody ApprovalRecord approvalRecord) {
        return approvalRecordService.createApprovalRecord(approvalRecord);
    }
    
    // API 端点处理工单审批
    @PostMapping("/api")
    public ResponseEntity<?> createApprovalRecordAPI(@RequestBody Map<String, Object> payload) {
        try {
            // 记录请求数据
            System.out.println("接收到审批请求，数据: " + payload);
            
            // 参数验证
            if (payload.get("workOrderId") == null || payload.get("status") == null) {
                throw new IllegalArgumentException("缺少必要参数: 工单ID和审批状态是必需的");
            }
            
            // 获取工单
            Integer workOrderId = Integer.valueOf(payload.get("workOrderId").toString());
            Optional<WorkOrder> workOrderOpt = workOrderRepository.findByIdWithUsers(workOrderId);
            
            if (!workOrderOpt.isPresent()) {
                throw new IllegalArgumentException("工单不存在: ID = " + workOrderId);
            }
            
            WorkOrder workOrder = workOrderOpt.get();
            
            // 创建审批记录
            ApprovalRecord approvalRecord = new ApprovalRecord();
            approvalRecord.setWorkOrder(workOrder);
            
            // 设置审批状态
            String statusStr = (String) payload.get("status");
            ApprovalStatus status = ApprovalStatus.valueOf(statusStr);
            approvalRecord.setStatus(status);
            
            // 设置审批意见
            String comments = (String) payload.get("comments");
            approvalRecord.setComments(comments);
            
            // 设置审批时间
            approvalRecord.setCreatedAt(LocalDateTime.now());
            
            // 设置审批人（当前登录用户）
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            User approver = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalStateException("无法获取当前用户信息"));
            
            approvalRecord.setApprover(approver);
            
            // 保存审批记录
            ApprovalRecord savedRecord = approvalRecordService.createApprovalRecord(approvalRecord);
            
            // 如果审批通过，更新工单状态为已批准
            if (status == ApprovalStatus.APPROVED && workOrder.getStatus() == com.example.opsworkordersystem.entity.Status.PENDING) {
                workOrderService.updateWorkOrderStatus(workOrderId, com.example.opsworkordersystem.entity.Status.APPROVED);
            }
            
            // 如果审批拒绝，更新工单状态为已拒绝
            if (status == ApprovalStatus.REJECTED) {
                workOrderService.updateWorkOrderStatus(workOrderId, com.example.opsworkordersystem.entity.Status.REJECTED);
            }
            
            return ResponseEntity.ok(savedRecord);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "审批失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
