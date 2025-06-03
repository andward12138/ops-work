package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.WorkOrderType;
import com.example.opsworkordersystem.service.WorkOrderTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工单类型管理控制器
 * 
 * 处理工单类型相关的页面路由和API请求
 */
@Controller
@RequestMapping("/work-order-types")
public class WorkOrderTypeController {
    
    @Autowired
    private WorkOrderTypeService workOrderTypeService;
    
    /**
     * 工单类型管理主页
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("workOrderTypes", workOrderTypeService.getAllWorkOrderTypes());
        model.addAttribute("typeStatistics", workOrderTypeService.getWorkOrderTypeStatistics());
        model.addAttribute("urgentTypes", workOrderTypeService.getUrgentTypes());
        model.addAttribute("approvalRequiredTypes", workOrderTypeService.getTypesRequiringApproval());
        return "work-order-type/index";
    }
    
    /**
     * 工单类型详情页面
     */
    @GetMapping("/{typeName}")
    public String detail(@PathVariable String typeName, Model model) {
        WorkOrderType type = workOrderTypeService.getWorkOrderTypeByName(typeName);
        if (type == null) {
            return "redirect:/work-order-types?error=类型不存在";
        }
        
        model.addAttribute("workOrderType", type);
        model.addAttribute("suggestedWorkflow", workOrderTypeService.getSuggestedWorkflow(type));
        model.addAttribute("processingSuggestion", workOrderTypeService.getProcessingSuggestion(type));
        model.addAttribute("colorCode", workOrderTypeService.getTypeColorCode(type));
        
        return "work-order-type/detail";
    }
    
    /**
     * 工单类型对比页面
     */
    @GetMapping("/compare")
    public String compare(Model model) {
        List<WorkOrderType> allTypes = workOrderTypeService.getAllWorkOrderTypes();
        model.addAttribute("workOrderTypes", allTypes);
        
        // 创建对比数据
        Map<String, Map<WorkOrderType, Object>> comparisonData = new HashMap<>();
        
        // 截止时间对比
        Map<WorkOrderType, Object> deadlineComparison = new HashMap<>();
        allTypes.forEach(type -> deadlineComparison.put(type, type.getDefaultDeadlineHours() + "小时"));
        comparisonData.put("截止时间", deadlineComparison);
        
        // 默认优先级对比
        Map<WorkOrderType, Object> priorityComparison = new HashMap<>();
        allTypes.forEach(type -> priorityComparison.put(type, type.getDefaultPriority().name()));
        comparisonData.put("默认优先级", priorityComparison);
        
        // 是否需要审批对比
        Map<WorkOrderType, Object> approvalComparison = new HashMap<>();
        allTypes.forEach(type -> approvalComparison.put(type, type.requiresApproval() ? "是" : "否"));
        comparisonData.put("需要审批", approvalComparison);
        
        // 是否紧急处理对比
        Map<WorkOrderType, Object> urgentComparison = new HashMap<>();
        allTypes.forEach(type -> urgentComparison.put(type, type.isRequiresImmediate() ? "是" : "否"));
        comparisonData.put("紧急处理", urgentComparison);
        
        model.addAttribute("comparisonData", comparisonData);
        
        return "work-order-type/compare";
    }
    
    // REST API 接口
    
    /**
     * 获取所有工单类型 API
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<WorkOrderType>> getAllTypes() {
        return ResponseEntity.ok(workOrderTypeService.getAllWorkOrderTypes());
    }
    
    /**
     * 获取工单类型详情 API
     */
    @GetMapping("/api/{typeName}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTypeDetail(@PathVariable String typeName) {
        WorkOrderType type = workOrderTypeService.getWorkOrderTypeByName(typeName);
        if (type == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("displayName", type.getDisplayName());
        result.put("description", type.getDescription());
        result.put("defaultDeadlineHours", type.getDefaultDeadlineHours());
        result.put("requiresImmediate", type.isRequiresImmediate());
        result.put("requiresApproval", type.requiresApproval());
        result.put("defaultPriority", type.getDefaultPriority());
        result.put("defaultStatus", type.getDefaultStatus());
        result.put("cssClass", type.getCssClass());
        result.put("icon", type.getIcon());
        result.put("colorCode", workOrderTypeService.getTypeColorCode(type));
        result.put("suggestedWorkflow", workOrderTypeService.getSuggestedWorkflow(type));
        result.put("processingSuggestion", workOrderTypeService.getProcessingSuggestion(type));
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取紧急类型列表 API
     */
    @GetMapping("/api/urgent")
    @ResponseBody
    public ResponseEntity<List<WorkOrderType>> getUrgentTypes() {
        return ResponseEntity.ok(workOrderTypeService.getUrgentTypes());
    }
    
    /**
     * 获取需要审批的类型列表 API
     */
    @GetMapping("/api/approval-required")
    @ResponseBody
    public ResponseEntity<List<WorkOrderType>> getApprovalRequiredTypes() {
        return ResponseEntity.ok(workOrderTypeService.getTypesRequiringApproval());
    }
    
    /**
     * 验证类型转换 API
     */
    @PostMapping("/api/validate-transition")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validateTypeTransition(
            @RequestParam String fromType, 
            @RequestParam String toType) {
        
        WorkOrderType from = workOrderTypeService.getWorkOrderTypeByName(fromType);
        WorkOrderType to = workOrderTypeService.getWorkOrderTypeByName(toType);
        
        Map<String, Object> result = new HashMap<>();
        
        if (from == null || to == null) {
            result.put("valid", false);
            result.put("message", "无效的工单类型");
            return ResponseEntity.badRequest().body(result);
        }
        
        boolean isValid = workOrderTypeService.isValidTypeTransition(from, to);
        result.put("valid", isValid);
        result.put("message", isValid ? "类型转换有效" : "不允许此类型转换");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取类型统计信息 API
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTypeStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        List<WorkOrderType> allTypes = workOrderTypeService.getAllWorkOrderTypes();
        statistics.put("totalTypes", allTypes.size());
        statistics.put("urgentTypes", workOrderTypeService.getUrgentTypes().size());
        statistics.put("approvalRequiredTypes", workOrderTypeService.getTypesRequiringApproval().size());
        
        // 按默认截止时间分组统计
        Map<String, Long> deadlineGroups = allTypes.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    type -> {
                        int hours = type.getDefaultDeadlineHours();
                        if (hours <= 2) return "极紧急 (≤2小时)";
                        else if (hours <= 24) return "紧急 (≤24小时)";
                        else if (hours <= 72) return "普通 (≤3天)";
                        else return "长期 (>3天)";
                    },
                    java.util.stream.Collectors.counting()
                ));
        statistics.put("deadlineGroups", deadlineGroups);
        
        // 颜色代码映射
        Map<WorkOrderType, String> colorMapping = new HashMap<>();
        allTypes.forEach(type -> colorMapping.put(type, workOrderTypeService.getTypeColorCode(type)));
        statistics.put("colorMapping", colorMapping);
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 获取工单类型的处理建议 API
     */
    @GetMapping("/api/{typeName}/suggestions")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getTypeSuggestions(@PathVariable String typeName) {
        WorkOrderType type = workOrderTypeService.getWorkOrderTypeByName(typeName);
        if (type == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, String> suggestions = new HashMap<>();
        suggestions.put("workflow", workOrderTypeService.getSuggestedWorkflow(type));
        suggestions.put("processing", workOrderTypeService.getProcessingSuggestion(type));
        
        return ResponseEntity.ok(suggestions);
    }
} 