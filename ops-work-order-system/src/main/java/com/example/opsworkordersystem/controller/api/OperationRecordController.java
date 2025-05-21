package com.example.opsworkordersystem.controller.api;

import com.example.opsworkordersystem.entity.OperationRecord;

import java.util.List;

public interface OperationRecordController {

    List<OperationRecord> getOperationRecordsByWorkOrderId(Long workOrderId);

    List<OperationRecord> getOperationRecordsByOperatorId(Long operatorId);

    OperationRecord createOperationRecord(OperationRecord operationRecord);
}
