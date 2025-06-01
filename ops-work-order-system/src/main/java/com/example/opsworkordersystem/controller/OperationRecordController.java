package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.OperationRecord;
import com.example.opsworkordersystem.entity.ActionType;
import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.service.OperationRecordService;
import com.example.opsworkordersystem.repository.WorkOrderRepository;
import com.example.opsworkordersystem.repository.UserRepository;
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
@RequestMapping("/operation-records")
public class OperationRecordController {

    @Autowired
    private OperationRecordService operationRecordService;
    
    @Autowired
    private WorkOrderRepository workOrderRepository;
    
    @Autowired
    private UserRepository userRepository;

    // 根据工单 ID 查询操作记录
    @GetMapping("/work-order/{workOrderId}")
    public List<OperationRecord> getOperationRecordsByWorkOrderId(@PathVariable Integer workOrderId) {
        return operationRecordService.getOperationRecordsByWorkOrderId(workOrderId);
    }

    // 根据操作人 ID 查询操作记录
    @GetMapping("/operator/{operatorId}")
    public List<OperationRecord> getOperationRecordsByOperatorId(@PathVariable Integer operatorId) {
        return operationRecordService.getOperationRecordsByOperatorId(operatorId);
    }

    // 创建操作记录
    @PostMapping
    public OperationRecord createOperationRecord(@RequestBody OperationRecord operationRecord) {
        return operationRecordService.createOperationRecord(operationRecord);
    }
    
    // API 端点处理工单操作记录
    @PostMapping("/api")
    public ResponseEntity<?> createOperationRecordAPI(@RequestBody Map<String, Object> payload) {
        try {
            // 记录请求数据
            System.out.println("接收到操作记录请求，数据: " + payload);
            
            // 参数验证
            if (payload.get("workOrderId") == null || payload.get("action") == null) {
                throw new IllegalArgumentException("缺少必要参数: 工单ID和操作类型是必需的");
            }
            
            // 获取工单
            Integer workOrderId = Integer.valueOf(payload.get("workOrderId").toString());
            Optional<WorkOrder> workOrderOpt = workOrderRepository.findByIdWithUsers(workOrderId);
            
            if (!workOrderOpt.isPresent()) {
                throw new IllegalArgumentException("工单不存在: ID = " + workOrderId);
            }
            
            WorkOrder workOrder = workOrderOpt.get();
            
            // 创建操作记录
            OperationRecord operationRecord = new OperationRecord();
            operationRecord.setWorkOrder(workOrder);
            
            // 设置操作类型
            String actionStr = (String) payload.get("action");
            ActionType action = ActionType.valueOf(actionStr);
            operationRecord.setAction(action);
            
            // 设置操作描述
            String comments = (String) payload.get("comments");
            operationRecord.setComments(comments);
            
            // 设置操作时间
            operationRecord.setCreatedAt(LocalDateTime.now());
            
            // 设置操作人（当前登录用户）
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            User operator = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalStateException("无法获取当前用户信息"));
            
            operationRecord.setOperator(operator);
            
            // 保存操作记录
            OperationRecord savedRecord = operationRecordService.createOperationRecord(operationRecord);
            
            return ResponseEntity.ok(savedRecord);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "添加操作记录失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
