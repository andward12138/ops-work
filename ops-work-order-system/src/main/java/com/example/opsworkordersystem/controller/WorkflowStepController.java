package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.ApprovalStatus;
import com.example.opsworkordersystem.entity.WorkflowStep;
import com.example.opsworkordersystem.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflow-steps")
public class WorkflowStepController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 获取用户待处理的步骤
     */
    @GetMapping("/pending/user/{userId}")
    public List<WorkflowStep> getUserPendingSteps(@PathVariable Integer userId) {
        return workflowService.getUserPendingSteps(userId);
    }

    /**
     * 获取部门待处理的步骤
     */
    @GetMapping("/pending/department/{departmentId}")
    public List<WorkflowStep> getDepartmentPendingSteps(@PathVariable Integer departmentId) {
        return workflowService.getDepartmentPendingSteps(departmentId);
    }

    /**
     * 审批步骤
     */
    @PostMapping("/{stepId}/approve")
    public ResponseEntity<String> approveStep(
            @PathVariable Integer stepId,
            @RequestBody Map<String, String> request) {
        
        String comments = request.getOrDefault("comments", "");
        
        try {
            workflowService.completeStep(stepId, comments, ApprovalStatus.APPROVED);
            return ResponseEntity.ok("审批成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("审批失败: " + e.getMessage());
        }
    }

    /**
     * 拒绝步骤
     */
    @PostMapping("/{stepId}/reject")
    public ResponseEntity<String> rejectStep(
            @PathVariable Integer stepId,
            @RequestBody Map<String, String> request) {
        
        String comments = request.get("comments");
        if (comments == null || comments.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("拒绝时必须填写原因");
        }
        
        try {
            workflowService.completeStep(stepId, comments, ApprovalStatus.REJECTED);
            return ResponseEntity.ok("已拒绝");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("操作失败: " + e.getMessage());
        }
    }
} 