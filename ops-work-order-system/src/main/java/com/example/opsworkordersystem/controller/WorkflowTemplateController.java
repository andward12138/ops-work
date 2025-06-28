package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.WorkflowTemplate;
import com.example.opsworkordersystem.entity.WorkflowTemplateStep;
import com.example.opsworkordersystem.repository.WorkflowTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/workflow-templates")
public class WorkflowTemplateController {

    @Autowired
    private WorkflowTemplateRepository templateRepository;

    /**
     * 获取所有工作流模板
     */
    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public List<WorkflowTemplate> getAllTemplates() {
        try {
            System.out.println("开始获取工作流模板...");
            List<WorkflowTemplate> templates = templateRepository.findAll();
            System.out.println("获取到 " + templates.size() + " 个模板");
            for (WorkflowTemplate template : templates) {
                System.out.println("模板: " + template.getTemplateName() + ", 步骤数: " + 
                    (template.getSteps() != null ? template.getSteps().size() : 0));
            }
            return templates;
        } catch (Exception e) {
            System.err.println("获取工作流模板时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 测试端点 - 获取模板数量
     */
    @GetMapping("/count")
    public String getTemplateCount() {
        try {
            long count = templateRepository.count();
            return "模板数量: " + count;
        } catch (Exception e) {
            return "错误: " + e.getMessage();
        }
    }
    
    /**
     * 获取简化的模板列表（不包含步骤）
     */
    @GetMapping("/simple")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public List<Object> getSimpleTemplates() {
        try {
            List<WorkflowTemplate> templates = templateRepository.findAll();
            List<Object> result = new ArrayList<>();
            for (WorkflowTemplate template : templates) {
                Map<String, Object> simpleTemplate = new HashMap<>();
                simpleTemplate.put("id", template.getId());
                simpleTemplate.put("templateName", template.getTemplateName());
                simpleTemplate.put("description", template.getDescription());
                simpleTemplate.put("workOrderType", template.getWorkOrderType());
                simpleTemplate.put("isActive", template.getIsActive());
                simpleTemplate.put("stepCount", template.getSteps() != null ? template.getSteps().size() : 0);
                result.add(simpleTemplate);
            }
            return result;
        } catch (Exception e) {
            System.err.println("获取简化模板列表时发生错误: " + e.getMessage());
            e.printStackTrace();
            List<Object> errorResult = new ArrayList<>();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            errorResult.add(error);
            return errorResult;
        }
    }

    /**
     * 获取激活的工作流模板
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public List<WorkflowTemplate> getActiveTemplates() {
        return templateRepository.findByIsActive(true);
    }

    /**
     * 根据ID获取工作流模板（包含步骤）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<WorkflowTemplate> getTemplateById(@PathVariable Integer id) {
        return templateRepository.findByIdWithSteps(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建工作流模板
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public WorkflowTemplate createTemplate(@RequestBody WorkflowTemplate template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        template.setIsActive(true);
        
        // 设置步骤与模板的关联
        if (template.getSteps() != null) {
            for (WorkflowTemplateStep step : template.getSteps()) {
                step.setTemplate(template);
            }
        }
        
        return templateRepository.save(template);
    }

    /**
     * 更新工作流模板
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<WorkflowTemplate> updateTemplate(
            @PathVariable Integer id, 
            @RequestBody WorkflowTemplate templateUpdate) {
        
        return templateRepository.findById(id)
                .map(template -> {
                    // 只更新非空字段
                    if (templateUpdate.getTemplateName() != null) {
                        template.setTemplateName(templateUpdate.getTemplateName());
                    }
                    if (templateUpdate.getDescription() != null) {
                        template.setDescription(templateUpdate.getDescription());
                    }
                    if (templateUpdate.getWorkOrderType() != null) {
                        template.setWorkOrderType(templateUpdate.getWorkOrderType());
                    }
                    if (templateUpdate.getIsActive() != null) {
                        template.setIsActive(templateUpdate.getIsActive());
                    }
                    template.setUpdatedAt(LocalDateTime.now());
                    
                    // 只有当传入了步骤数据时才更新步骤
                    if (templateUpdate.getSteps() != null) {
                        template.getSteps().clear();
                        for (WorkflowTemplateStep step : templateUpdate.getSteps()) {
                            step.setTemplate(template);
                            template.getSteps().add(step);
                        }
                    }
                    
                    return ResponseEntity.ok(templateRepository.save(template));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除工作流模板（软删除）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Integer id) {
        return templateRepository.findById(id)
                .map(template -> {
                    template.setIsActive(false);
                    template.setUpdatedAt(LocalDateTime.now());
                    templateRepository.save(template);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据工单类型获取适用的模板
     */
    @GetMapping("/by-type/{workOrderType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<WorkflowTemplate> getTemplateByType(@PathVariable String workOrderType) {
        return templateRepository.findByWorkOrderTypeAndIsActive(workOrderType, true)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 切换模板状态（启用/停用）
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<WorkflowTemplate> toggleTemplateStatus(@PathVariable Integer id) {
        return templateRepository.findById(id)
                .map(template -> {
                    template.setIsActive(!template.getIsActive());
                    template.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(templateRepository.save(template));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 启用模板
     */
    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<WorkflowTemplate> enableTemplate(@PathVariable Integer id) {
        return templateRepository.findById(id)
                .map(template -> {
                    template.setIsActive(true);
                    template.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(templateRepository.save(template));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 停用模板
     */
    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<WorkflowTemplate> disableTemplate(@PathVariable Integer id) {
        return templateRepository.findById(id)
                .map(template -> {
                    template.setIsActive(false);
                    template.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(templateRepository.save(template));
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 