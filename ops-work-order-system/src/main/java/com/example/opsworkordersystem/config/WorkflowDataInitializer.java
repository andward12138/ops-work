package com.example.opsworkordersystem.config;

import com.example.opsworkordersystem.entity.*;
import com.example.opsworkordersystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WorkflowDataInitializer implements CommandLineRunner {

    @Autowired
    private WorkflowTemplateRepository templateRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeWorkflowTemplates();
    }

    private void initializeWorkflowTemplates() {
        // 检查是否已经有工作流模板，如果有就跳过初始化
        if (templateRepository.count() > 0) {
            System.out.println("工作流模板已存在，跳过初始化");
            return;
        }
        
        // 清理可能存在的旧数据（防止约束冲突）
        try {
            templateRepository.deleteAll();
            System.out.println("清理旧的工作流模板数据");
        } catch (Exception e) {
            System.out.println("清理旧数据时出现异常，继续初始化: " + e.getMessage());
        }

        System.out.println("开始初始化工作流模板数据...");

        try {
            // 确保有部门数据
            Department defaultDept = departmentRepository.findByName("运维部").orElse(null);
            if (defaultDept == null) {
                // 创建默认部门
                defaultDept = new Department();
                defaultDept.setName("运维部");
                defaultDept.setCode("OPS");
                defaultDept.setType(DepartmentType.OPERATIONAL);
                defaultDept.setIsActive(true);
                defaultDept.setCreatedAt(LocalDateTime.now());
                defaultDept.setUpdatedAt(LocalDateTime.now());
                defaultDept = departmentRepository.save(defaultDept);
            }

            // 创建标准需求工单流程
            WorkflowTemplate standardTemplate = templateRepository.findByTemplateName("标准需求工单流程").orElse(null);
            if (standardTemplate == null) {
                standardTemplate = new WorkflowTemplate();
                standardTemplate.setTemplateName("标准需求工单流程");
                standardTemplate.setDescription("适用于功能需求和配置变更的标准审批流程");
                standardTemplate.setWorkOrderType("REQUEST");
                standardTemplate.setIsActive(true);
                standardTemplate.setCreatedAt(LocalDateTime.now());
                standardTemplate.setUpdatedAt(LocalDateTime.now());

                // 添加步骤
                WorkflowTemplateStep step1 = new WorkflowTemplateStep();
                step1.setStepOrder(1);
                step1.setStepName("部门初审");
                step1.setStepType(WorkflowStepType.DEPARTMENT_REVIEW);
                step1.setAssigneeRole(Role.MANAGER);
                step1.setAssigneeDepartment(defaultDept);
                step1.setTimeLimitHours(24);
                step1.setIsSkippable(false);
                step1.setIsParallel(false);
                step1.setAutoApprove(false);
                standardTemplate.addStep(step1);

                WorkflowTemplateStep step2 = new WorkflowTemplateStep();
                step2.setStepOrder(2);
                step2.setStepName("经理审批");
                step2.setStepType(WorkflowStepType.MANAGER_APPROVAL);
                step2.setAssigneeRole(Role.MANAGER);
                step2.setAssigneeDepartment(defaultDept);
                step2.setTimeLimitHours(48);
                step2.setIsSkippable(false);
                step2.setIsParallel(false);
                step2.setAutoApprove(false);
                standardTemplate.addStep(step2);

                WorkflowTemplateStep step3 = new WorkflowTemplateStep();
                step3.setStepOrder(3);
                step3.setStepName("执行操作");
                step3.setStepType(WorkflowStepType.EXECUTION);
                step3.setAssigneeRole(Role.OPERATOR);
                step3.setAssigneeDepartment(defaultDept);
                step3.setTimeLimitHours(72);
                step3.setIsSkippable(false);
                step3.setIsParallel(false);
                step3.setAutoApprove(false);
                standardTemplate.addStep(step3);

                WorkflowTemplateStep step4 = new WorkflowTemplateStep();
                step4.setStepOrder(4);
                step4.setStepName("完成确认");
                step4.setStepType(WorkflowStepType.COMPLETION);
                step4.setAssigneeRole(Role.USER);
                step4.setTimeLimitHours(24);
                step4.setIsSkippable(false);
                step4.setIsParallel(false);
                step4.setAutoApprove(false);
                standardTemplate.addStep(step4);

                templateRepository.save(standardTemplate);
            }

            // 创建紧急变更流程
            WorkflowTemplate emergencyTemplate = new WorkflowTemplate();
            emergencyTemplate.setTemplateName("紧急变更流程");
            emergencyTemplate.setDescription("适用于紧急变更的快速审批流程");
            emergencyTemplate.setWorkOrderType("EMERGENCY");
            emergencyTemplate.setIsActive(true);
            emergencyTemplate.setCreatedAt(LocalDateTime.now());
            emergencyTemplate.setUpdatedAt(LocalDateTime.now());

            WorkflowTemplateStep emergencyStep1 = new WorkflowTemplateStep();
            emergencyStep1.setStepOrder(1);
            emergencyStep1.setStepName("经理快速审批");
            emergencyStep1.setStepType(WorkflowStepType.MANAGER_APPROVAL);
            emergencyStep1.setAssigneeRole(Role.MANAGER);
            emergencyStep1.setAssigneeDepartment(defaultDept);
            emergencyStep1.setTimeLimitHours(2);
            emergencyStep1.setIsSkippable(false);
            emergencyStep1.setIsParallel(false);
            emergencyStep1.setAutoApprove(true);
            emergencyTemplate.addStep(emergencyStep1);

            WorkflowTemplateStep emergencyStep2 = new WorkflowTemplateStep();
            emergencyStep2.setStepOrder(2);
            emergencyStep2.setStepName("紧急执行");
            emergencyStep2.setStepType(WorkflowStepType.EXECUTION);
            emergencyStep2.setAssigneeRole(Role.OPERATOR);
            emergencyStep2.setAssigneeDepartment(defaultDept);
            emergencyStep2.setTimeLimitHours(4);
            emergencyStep2.setIsSkippable(false);
            emergencyStep2.setIsParallel(false);
            emergencyStep2.setAutoApprove(false);
            emergencyTemplate.addStep(emergencyStep2);

            templateRepository.save(emergencyTemplate);

            // 创建例行维护流程
            WorkflowTemplate maintenanceTemplate = new WorkflowTemplate();
            maintenanceTemplate.setTemplateName("例行维护流程");
            maintenanceTemplate.setDescription("适用于例行维护的简化流程");
            maintenanceTemplate.setWorkOrderType("MAINTENANCE");
            maintenanceTemplate.setIsActive(true);
            maintenanceTemplate.setCreatedAt(LocalDateTime.now());
            maintenanceTemplate.setUpdatedAt(LocalDateTime.now());

            WorkflowTemplateStep maintenanceStep1 = new WorkflowTemplateStep();
            maintenanceStep1.setStepOrder(1);
            maintenanceStep1.setStepName("维护审核");
            maintenanceStep1.setStepType(WorkflowStepType.DEPARTMENT_REVIEW);
            maintenanceStep1.setAssigneeRole(Role.OPERATOR);
            maintenanceStep1.setAssigneeDepartment(defaultDept);
            maintenanceStep1.setTimeLimitHours(12);
            maintenanceStep1.setIsSkippable(true);
            maintenanceStep1.setIsParallel(false);
            maintenanceStep1.setAutoApprove(false);
            maintenanceTemplate.addStep(maintenanceStep1);

            WorkflowTemplateStep maintenanceStep2 = new WorkflowTemplateStep();
            maintenanceStep2.setStepOrder(2);
            maintenanceStep2.setStepName("执行维护");
            maintenanceStep2.setStepType(WorkflowStepType.EXECUTION);
            maintenanceStep2.setAssigneeRole(Role.OPERATOR);
            maintenanceStep2.setAssigneeDepartment(defaultDept);
            maintenanceStep2.setTimeLimitHours(24);
            maintenanceStep2.setIsSkippable(false);
            maintenanceStep2.setIsParallel(false);
            maintenanceStep2.setAutoApprove(false);
            maintenanceTemplate.addStep(maintenanceStep2);

            templateRepository.save(maintenanceTemplate);

            // 创建故障处理流程
            WorkflowTemplate incidentTemplate = new WorkflowTemplate();
            incidentTemplate.setTemplateName("故障处理流程");
            incidentTemplate.setDescription("适用于系统故障和服务中断的快速处理流程");
            incidentTemplate.setWorkOrderType("INCIDENT");
            incidentTemplate.setIsActive(true);
            incidentTemplate.setCreatedAt(LocalDateTime.now());
            incidentTemplate.setUpdatedAt(LocalDateTime.now());

            WorkflowTemplateStep incidentStep1 = new WorkflowTemplateStep();
            incidentStep1.setStepOrder(1);
            incidentStep1.setStepName("故障分析");
            incidentStep1.setStepType(WorkflowStepType.DEPARTMENT_REVIEW);
            incidentStep1.setAssigneeRole(Role.OPERATOR);
            incidentStep1.setAssigneeDepartment(defaultDept);
            incidentStep1.setTimeLimitHours(1);
            incidentStep1.setIsSkippable(false);
            incidentStep1.setIsParallel(false);
            incidentStep1.setAutoApprove(false);
            incidentTemplate.addStep(incidentStep1);

            WorkflowTemplateStep incidentStep2 = new WorkflowTemplateStep();
            incidentStep2.setStepOrder(2);
            incidentStep2.setStepName("应急修复");
            incidentStep2.setStepType(WorkflowStepType.EXECUTION);
            incidentStep2.setAssigneeRole(Role.OPERATOR);
            incidentStep2.setAssigneeDepartment(defaultDept);
            incidentStep2.setTimeLimitHours(2);
            incidentStep2.setIsSkippable(false);
            incidentStep2.setIsParallel(false);
            incidentStep2.setAutoApprove(false);
            incidentTemplate.addStep(incidentStep2);

            WorkflowTemplateStep incidentStep3 = new WorkflowTemplateStep();
            incidentStep3.setStepOrder(3);
            incidentStep3.setStepName("验证确认");
            incidentStep3.setStepType(WorkflowStepType.VERIFICATION);
            incidentStep3.setAssigneeRole(Role.MANAGER);
            incidentStep3.setAssigneeDepartment(defaultDept);
            incidentStep3.setTimeLimitHours(1);
            incidentStep3.setIsSkippable(false);
            incidentStep3.setIsParallel(false);
            incidentStep3.setAutoApprove(false);
            incidentTemplate.addStep(incidentStep3);

            templateRepository.save(incidentTemplate);

            // 创建一个停用的测试模板来测试显示功能
            WorkflowTemplate disabledTemplate = templateRepository.findByTemplateName("已停用测试模板").orElse(null);
            if (disabledTemplate == null) {
                disabledTemplate = new WorkflowTemplate();
                disabledTemplate.setTemplateName("已停用测试模板");
                disabledTemplate.setDescription("这是一个用于测试显示功能的停用模板");
                disabledTemplate.setWorkOrderType("REQUEST");
                disabledTemplate.setIsActive(false); // 设置为停用状态
                disabledTemplate.setCreatedAt(LocalDateTime.now());
                disabledTemplate.setUpdatedAt(LocalDateTime.now());

                // 添加一个简单步骤
                WorkflowTemplateStep disabledStep = new WorkflowTemplateStep();
                disabledStep.setStepOrder(1);
                disabledStep.setStepName("测试步骤");
                disabledStep.setStepType(WorkflowStepType.DEPARTMENT_REVIEW);
                disabledStep.setAssigneeRole(Role.USER);
                disabledStep.setTimeLimitHours(24);
                disabledStep.setIsSkippable(true);
                disabledStep.setIsParallel(false);
                disabledStep.setAutoApprove(false);
                disabledTemplate.addStep(disabledStep);

                templateRepository.save(disabledTemplate);
                System.out.println("创建了1个停用的测试模板");
            }

            System.out.println("工作流模板初始化完成：创建了5个基础模板（包含1个停用模板）");

        } catch (Exception e) {
            System.err.println("工作流模板初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 