package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.entity.ApprovalRecord;
import com.example.opsworkordersystem.repository.ApprovalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalRecordService {

    @Autowired
    private ApprovalRecordRepository approvalRecordRepository;

    // 根据工单 ID 查找审批记录
    public List<ApprovalRecord> getApprovalRecordsByWorkOrderId(Long workOrderId) {
        return approvalRecordRepository.findByWorkOrderIdWithApprover(workOrderId);
    }

    // 创建审批记录
    public ApprovalRecord createApprovalRecord(ApprovalRecord approvalRecord) {
        return approvalRecordRepository.save(approvalRecord);
    }
}
