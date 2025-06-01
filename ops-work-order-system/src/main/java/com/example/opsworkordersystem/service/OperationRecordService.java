package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.entity.OperationRecord;
import com.example.opsworkordersystem.repository.OperationRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationRecordService {

    @Autowired
    private OperationRecordRepository operationRecordRepository;

    // 根据工单 ID 查找操作记录
    public List<OperationRecord> getOperationRecordsByWorkOrderId(Integer workOrderId) {
        return operationRecordRepository.findByWorkOrderIdWithOperator(workOrderId);
    }

    // 根据操作人 ID 查找操作记录
    public List<OperationRecord> getOperationRecordsByOperatorId(Integer operatorId) {
        return operationRecordRepository.findByOperatorId(operatorId);
    }

    // 创建操作记录
    public OperationRecord createOperationRecord(OperationRecord operationRecord) {
        return operationRecordRepository.save(operationRecord);
    }
}
