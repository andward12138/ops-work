package com.example.opsworkordersystem.controller.api;

import com.example.opsworkordersystem.entity.ApprovalRecord;

import java.util.List;

public interface ApprovalRecordController {

    List<ApprovalRecord> getApprovalRecordsByWorkOrderId(Long workOrderId);

    ApprovalRecord createApprovalRecord(ApprovalRecord approvalRecord);
}
