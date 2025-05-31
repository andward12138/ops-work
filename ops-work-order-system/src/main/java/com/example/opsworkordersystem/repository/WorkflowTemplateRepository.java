package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplate, Integer> {
    
    // 根据工单类型查找激活的模板
    Optional<WorkflowTemplate> findByWorkOrderTypeAndIsActive(String workOrderType, Boolean isActive);
    
    // 查找所有激活的模板
    List<WorkflowTemplate> findByIsActive(Boolean isActive);
    
    // 根据名称查找模板
    Optional<WorkflowTemplate> findByTemplateName(String templateName);
    
    // 查询模板及其步骤
    @Query("SELECT DISTINCT t FROM WorkflowTemplate t LEFT JOIN FETCH t.steps WHERE t.id = :id")
    Optional<WorkflowTemplate> findByIdWithSteps(Integer id);
} 