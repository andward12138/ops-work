package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.OperationRecord;
import com.example.opsworkordersystem.service.OperationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operation-records")
public class OperationRecordController {

    @Autowired
    private OperationRecordService operationRecordService;

    // 根据工单 ID 查询操作记录
    @GetMapping("/work-order/{workOrderId}")
    public List<OperationRecord> getOperationRecordsByWorkOrderId(@PathVariable Long workOrderId) {
        return operationRecordService.getOperationRecordsByWorkOrderId(workOrderId);
    }

    // 根据操作人 ID 查询操作记录
    @GetMapping("/operator/{operatorId}")
    public List<OperationRecord> getOperationRecordsByOperatorId(@PathVariable Long operatorId) {
        return operationRecordService.getOperationRecordsByOperatorId(operatorId);
    }

    // 创建操作记录
    @PostMapping
    public OperationRecord createOperationRecord(@RequestBody OperationRecord operationRecord) {
        return operationRecordService.createOperationRecord(operationRecord);
    }
}
