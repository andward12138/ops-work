package com.example.opsworkordersystem.controller.api;

import com.example.opsworkordersystem.entity.WorkOrder;

import java.util.List;

public interface WorkOrderController {

    WorkOrder createWorkOrder(WorkOrder workOrder);

    List<WorkOrder> getWorkOrdersByStatus(String status);

    List<WorkOrder> getWorkOrdersByCreator(Long createdById);
}
