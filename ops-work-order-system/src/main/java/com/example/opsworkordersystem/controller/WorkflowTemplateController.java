package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.WorkflowTemplate;
import com.example.opsworkordersystem.entity.WorkflowTemplateStep;
import com.example.opsworkordersystem.repository.WorkflowTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/workflow-templates")
public class WorkflowTemplateController {

    @Autowired
    private WorkflowTemplateRepository templateRepository;

    /**
     * 获取所有工作流模板
     */
    @GetMapping
    public List<WorkflowTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    /**
     * 获取激活的工作流模板
     */
    @GetMapping("/active")
    public List<WorkflowTemplate> getActiveTemplates() {
        return templateRepository.findByIsActive(true);
    }

    /**
     * 根据ID获取工作流模板（包含步骤）
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowTemplate> getTemplateById(@PathVariable Integer id) {
        return templateRepository.findByIdWithSteps(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建工作流模板
     */
    @PostMapping
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
    public ResponseEntity<WorkflowTemplate> updateTemplate(
            @PathVariable Integer id, 
            @RequestBody WorkflowTemplate templateUpdate) {
        
        return templateRepository.findById(id)
                .map(template -> {
                    template.setTemplateName(templateUpdate.getTemplateName());
                    template.setDescription(templateUpdate.getDescription());
                    template.setWorkOrderType(templateUpdate.getWorkOrderType());
                    template.setIsActive(templateUpdate.getIsActive());
                    template.setUpdatedAt(LocalDateTime.now());
                    
                    // 更新步骤
                    template.getSteps().clear();
                    if (templateUpdate.getSteps() != null) {
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
    public ResponseEntity<WorkflowTemplate> getTemplateByType(@PathVariable String workOrderType) {
        return templateRepository.findByWorkOrderTypeAndIsActive(workOrderType, true)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 