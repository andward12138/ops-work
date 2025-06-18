package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.ApprovalRecord;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.repository.ApprovalRecordRepository;
import com.example.opsworkordersystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/workflow/api")
public class WorkflowApiController {

    @Autowired
    private ApprovalRecordRepository approvalRecordRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * 获取当前用户的审批历史
     */
    @GetMapping("/approval-history")
    public ResponseEntity<?> getApprovalHistory() {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalStateException("无法获取当前用户信息"));

            // 查询当前用户的所有审批记录
            List<ApprovalRecord> records = approvalRecordRepository.findAll().stream()
                .filter(record -> record.getApprover() != null && record.getApprover().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());

            // 转换为前端需要的格式
            List<Map<String, Object>> response = records.stream().map(record -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", record.getId());
                item.put("workOrderId", record.getWorkOrder().getId());
                item.put("workOrderTitle", record.getWorkOrder().getTitle());
                item.put("status", record.getStatus().name());
                item.put("stepType", "MANAGER_APPROVAL"); // 简化处理，实际应该从工作流步骤获取
                item.put("comments", record.getComments());
                item.put("createdAt", record.getCreatedAt());
                item.put("processedAt", record.getApprovedAt());
                return item;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取审批历史失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalStateException("无法获取当前用户信息"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", currentUser.getId());
            userInfo.put("username", currentUser.getUsername());
            userInfo.put("email", currentUser.getEmail());
            userInfo.put("role", currentUser.getRole().name());
            if (currentUser.getDepartment() != null) {
                userInfo.put("departmentId", currentUser.getDepartment().getId());
                userInfo.put("departmentName", currentUser.getDepartment().getName());
            }

            return ResponseEntity.ok(userInfo);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取用户信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 