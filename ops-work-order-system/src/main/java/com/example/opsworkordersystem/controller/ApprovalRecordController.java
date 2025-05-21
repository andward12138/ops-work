package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.ApprovalRecord;
import com.example.opsworkordersystem.service.ApprovalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/approval-records")
public class ApprovalRecordController {

    @Autowired
    private ApprovalRecordService approvalRecordService;

    // 根据工单 ID 查询审批记录
    @GetMapping("/work-order/{workOrderId}")
    public List<ApprovalRecord> getApprovalRecordsByWorkOrderId(@PathVariable Long workOrderId) {
        return approvalRecordService.getApprovalRecordsByWorkOrderId(workOrderId);
    }

    // 创建审批记录
    @PostMapping
    public ApprovalRecord createApprovalRecord(@RequestBody ApprovalRecord approvalRecord) {
        return approvalRecordService.createApprovalRecord(approvalRecord);
    }
}
