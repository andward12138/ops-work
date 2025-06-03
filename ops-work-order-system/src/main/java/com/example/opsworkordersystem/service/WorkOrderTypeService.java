package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.entity.WorkOrderType;
import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.entity.Priority;
import com.example.opsworkordersystem.entity.Status;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工单类型管理服务
 * 
 * 提供工单类型相关的业务逻辑处理
 */
@Service
public class WorkOrderTypeService {
    
    /**
     * 获取所有工单类型
     */
    public List<WorkOrderType> getAllWorkOrderTypes() {
        return Arrays.asList(WorkOrderType.values());
    }
    
    /**
     * 根据名称获取工单类型
     */
    public WorkOrderType getWorkOrderTypeByName(String name) {
        try {
            return WorkOrderType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * 获取工单类型统计信息
     */
    public Map<WorkOrderType, String> getWorkOrderTypeStatistics() {
        return Arrays.stream(WorkOrderType.values())
                .collect(Collectors.toMap(
                    type -> type,
                    type -> String.format("%s - %s，默认%d小时内完成",
                        type.getDisplayName(),
                        type.getDescription(),
                        type.getDefaultDeadlineHours())
                ));
    }
    
    /**
     * 根据工单类型设置工单的默认属性
     */
    public void setDefaultPropertiesByType(WorkOrder workOrder, WorkOrderType type) {
        if (workOrder == null || type == null) {
            return;
        }
        
        workOrder.setWorkOrderType(type);
        workOrder.setPriority(type.getDefaultPriority());
        workOrder.setStatus(type.getDefaultStatus());
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        if (workOrder.getCreatedAt() == null) {
            workOrder.setCreatedAt(now);
        }
        workOrder.setUpdatedAt(now);
        
        // 计算截止时间
        workOrder.calculateDeadlineBasedOnType();
    }
    
    /**
     * 检查工单类型是否需要特殊处理流程
     */
    public boolean requiresSpecialProcessing(WorkOrderType type) {
        return type == WorkOrderType.EMERGENCY || type == WorkOrderType.INCIDENT;
    }
    
    /**
     * 获取紧急工单类型列表
     */
    public List<WorkOrderType> getUrgentTypes() {
        return Arrays.stream(WorkOrderType.values())
                .filter(WorkOrderType::isRequiresImmediate)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取需要审批的工单类型列表
     */
    public List<WorkOrderType> getTypesRequiringApproval() {
        return Arrays.stream(WorkOrderType.values())
                .filter(WorkOrderType::requiresApproval)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据工单类型获取建议的处理流程
     */
    public String getSuggestedWorkflow(WorkOrderType type) {
        switch (type) {
            case EMERGENCY:
                return "应急处理流程：立即分配 → 紧急响应 → 快速解决 → 事后报告";
            case INCIDENT:
                return "故障处理流程：快速分析 → 应急修复 → 根本原因分析 → 预防措施";
            case MAINTENANCE:
                return "维护流程：计划审批 → 准备工作 → 执行维护 → 验证测试 → 文档更新";
            case REQUEST:
                return "需求流程：需求审批 → 方案设计 → 开发实施 → 测试验证 → 上线部署";
            default:
                return "标准流程：创建 → 审批 → 分配 → 执行 → 验证 → 完成";
        }
    }
    
    /**
     * 获取工单类型的处理建议
     */
    public String getProcessingSuggestion(WorkOrderType type) {
        switch (type) {
            case EMERGENCY:
                return "紧急工单，需要立即处理，可跳过常规审批流程，直接分配给最佳处理人员";
            case INCIDENT:
                return "故障工单，需要快速响应，优先分配给技术专家，建立临时通信渠道";
            case MAINTENANCE:
                return "维护工单，需要提前规划，考虑业务影响，选择合适的维护窗口";
            case REQUEST:
                return "需求工单，需要详细评估，明确需求范围，合理安排开发资源";
            default:
                return "按照标准流程处理，确保每个环节都有适当的审核和验证";
        }
    }
    
    /**
     * 检查工单类型是否兼容指定的优先级
     */
    public boolean isCompatibleWithPriority(WorkOrderType type, Priority priority) {
        switch (type) {
            case EMERGENCY:
                return priority == Priority.URGENT;
            case INCIDENT:
                return priority == Priority.HIGH || priority == Priority.URGENT;
            case MAINTENANCE:
                return priority != Priority.HIGH && priority != Priority.URGENT; // 维护通常不是高优先级
            case REQUEST:
                return true; // 需求可以是任何优先级
            default:
                return true;
        }
    }
    
    /**
     * 获取工单类型的颜色代码（用于图表等可视化）
     */
    public String getTypeColorCode(WorkOrderType type) {
        switch (type) {
            case EMERGENCY:
                return "#dc3545"; // 红色
            case INCIDENT:
                return "#fd7e14"; // 橙色
            case MAINTENANCE:
                return "#17a2b8"; // 青色
            case REQUEST:
                return "#6c757d"; // 灰色
            default:
                return "#6c757d";
        }
    }
    
    /**
     * 验证工单类型转换是否合法
     */
    public boolean isValidTypeTransition(WorkOrderType fromType, WorkOrderType toType) {
        // 应急工单不能转换为其他类型
        if (fromType == WorkOrderType.EMERGENCY) {
            return false;
        }
        
        // 其他类型可以转换为应急工单（升级）
        if (toType == WorkOrderType.EMERGENCY) {
            return true;
        }
        
        // 故障工单可以转换为维护工单（后续预防性维护）
        if (fromType == WorkOrderType.INCIDENT && toType == WorkOrderType.MAINTENANCE) {
            return true;
        }
        
        // 需求工单可以转换为维护工单（实施阶段）
        if (fromType == WorkOrderType.REQUEST && toType == WorkOrderType.MAINTENANCE) {
            return true;
        }
        
        // 其他情况通常不允许类型转换
        return false;
    }
} 