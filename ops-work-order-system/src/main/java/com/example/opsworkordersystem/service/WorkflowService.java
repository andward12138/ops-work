package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.entity.*;
import com.example.opsworkordersystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkflowService {

    @Autowired
    private WorkflowTemplateRepository templateRepository;
    
    @Autowired
    private WorkflowStepRepository stepRepository;
    
    @Autowired
    private WorkOrderRepository workOrderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ApprovalRecordService approvalRecordService;
    
    /**
     * 为工单初始化工作流
     */
    public List<WorkflowStep> initializeWorkflow(WorkOrder workOrder, String workOrderType) {
        // 查找适用的工作流模板
        Optional<WorkflowTemplate> templateOpt = templateRepository
            .findByWorkOrderTypeAndIsActive(workOrderType, true);
        
        if (!templateOpt.isPresent()) {
            // 如果没有找到适配的工作流模板，返回空列表而不是抛出异常
            System.out.println("警告: 未找到工单类型 '" + workOrderType + "' 的工作流模板，跳过工作流初始化");
            return new ArrayList<>();
        }
        
        WorkflowTemplate template = templateOpt.get();
        
        // 获取模板步骤
        template = templateRepository.findByIdWithSteps(template.getId())
            .orElse(template);
        
        // 创建工作流步骤
        List<WorkflowStep> workflowSteps = new ArrayList<>();
        for (WorkflowTemplateStep templateStep : template.getSteps()) {
            WorkflowStep step = createWorkflowStep(workOrder, templateStep);
            workflowSteps.add(step);
        }
        
        // 保存所有步骤
        workflowSteps = stepRepository.saveAll(workflowSteps);
        
        // 激活第一个步骤或第一组并行步骤
        activateInitialSteps(workflowSteps);
        
        return workflowSteps;
    }
    
    /**
     * 根据模板步骤创建工作流步骤
     */
    private WorkflowStep createWorkflowStep(WorkOrder workOrder, WorkflowTemplateStep templateStep) {
        WorkflowStep step = new WorkflowStep();
        step.setWorkOrder(workOrder);
        step.setStepOrder(templateStep.getStepOrder());
        step.setStepName(templateStep.getStepName());
        step.setStepType(templateStep.getStepType());
        step.setAssigneeRole(templateStep.getAssigneeRole());
        step.setAssigneeDepartment(templateStep.getAssigneeDepartment());
        step.setIsSkippable(templateStep.getIsSkippable());
        step.setIsParallel(templateStep.getIsParallel());
        step.setParallelGroup(templateStep.getParallelGroup());
        step.setStatus(WorkflowStepStatus.PENDING);
        step.setCreatedAt(LocalDateTime.now());
        step.setUpdatedAt(LocalDateTime.now());
        
        // 设置截止时间
        if (templateStep.getTimeLimitHours() != null) {
            step.setDeadline(LocalDateTime.now().plusHours(templateStep.getTimeLimitHours()));
        }
        
        // 分配处理人
        assignStepHandler(step);
        
        return step;
    }
    
    /**
     * 激活初始步骤
     */
    private void activateInitialSteps(List<WorkflowStep> steps) {
        if (steps.isEmpty()) return;
        
        // 按步骤顺序排序
        steps.sort(Comparator.comparing(WorkflowStep::getStepOrder));
        
        // 找到第一个步骤的并行组
        WorkflowStep firstStep = steps.get(0);
        Integer firstParallelGroup = firstStep.getParallelGroup();
        
        // 激活第一个步骤或第一组并行步骤
        for (WorkflowStep step : steps) {
            if (firstParallelGroup != null && firstParallelGroup.equals(step.getParallelGroup())) {
                // 同一并行组的步骤都激活
                step.startStep();
            } else if (firstParallelGroup == null && step.getStepOrder().equals(firstStep.getStepOrder())) {
                // 非并行步骤，只激活第一个
                step.startStep();
                break;
            }
        }
        
        stepRepository.saveAll(steps);
    }
    
    /**
     * 分配步骤处理人
     */
    private void assignStepHandler(WorkflowStep step) {
        // 根据角色和部门分配处理人
        if (step.getAssigneeRole() != null && step.getAssigneeDepartment() != null) {
            try {
                // 查找部门内具有指定角色的用户
                List<User> users = userRepository.findByRoleAndDepartmentId(
                    step.getAssigneeRole(), 
                    step.getAssigneeDepartment().getId()
                );
                
                if (!users.isEmpty()) {
                    // 简单的负载均衡：选择待处理任务最少的用户
                    User assignee = selectLeastBusyUser(users);
                    step.setAssignee(assignee);
                }
            } catch (Exception e) {
                System.err.println("分配步骤处理人失败: " + e.getMessage());
                // 不抛出异常，允许工作流继续
            }
        }
    }
    
    /**
     * 选择最空闲的用户
     */
    private User selectLeastBusyUser(List<User> users) {
        Map<User, Long> taskCounts = new HashMap<>();
        
        for (User user : users) {
            long count = stepRepository.findPendingStepsByAssigneeId(user.getId()).size();
            taskCounts.put(user, count);
        }
        
        return taskCounts.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(users.get(0));
    }
    
    /**
     * 处理步骤完成
     */
    public void completeStep(Integer stepId, String comments, ApprovalStatus approvalStatus) {
        Optional<WorkflowStep> stepOpt = stepRepository.findById(stepId);
        if (!stepOpt.isPresent()) {
            System.err.println("错误: 步骤不存在，ID: " + stepId);
            return;
        }
        
        WorkflowStep step = stepOpt.get();
        
        // 更新步骤状态
        if (approvalStatus == ApprovalStatus.APPROVED) {
            step.completeStep(comments);
        } else {
            step.rejectStep(comments);
        }
        
        stepRepository.save(step);
        
        // 创建审批记录
        createApprovalRecord(step, comments, approvalStatus);
        
        // 处理工作流流转
        if (approvalStatus == ApprovalStatus.APPROVED) {
            processWorkflowTransition(step);
        } else {
            // 如果拒绝，更新工单状态
            updateWorkOrderStatus(step.getWorkOrder(), Status.REJECTED);
        }
    }
    
    /**
     * 创建审批记录
     */
    private void createApprovalRecord(WorkflowStep step, String comments, ApprovalStatus status) {
        ApprovalRecord record = new ApprovalRecord();
        record.setWorkOrder(step.getWorkOrder());
        record.setApprover(step.getAssignee());
        record.setApprovalStatus(status);
        record.setComments(comments);
        record.setApprovedAt(LocalDateTime.now());
        
        approvalRecordService.createApprovalRecord(record);
    }
    
    /**
     * 处理工作流流转
     */
    private void processWorkflowTransition(WorkflowStep completedStep) {
        WorkOrder workOrder = completedStep.getWorkOrder();
        List<WorkflowStep> allSteps = stepRepository.findByWorkOrderIdOrderByStepOrder(workOrder.getId());
        
        // 检查并行组是否全部完成
        if (completedStep.getParallelGroup() != null) {
            List<WorkflowStep> parallelSteps = allSteps.stream()
                .filter(s -> completedStep.getParallelGroup().equals(s.getParallelGroup()))
                .collect(Collectors.toList());
            
            boolean allCompleted = parallelSteps.stream()
                .allMatch(s -> s.getStatus() == WorkflowStepStatus.COMPLETED);
            
            if (!allCompleted) {
                // 并行组还有未完成的步骤，不继续流转
                return;
            }
        }
        
        // 找到下一个待激活的步骤
        List<WorkflowStep> nextSteps = findNextSteps(allSteps, completedStep);
        
        if (nextSteps.isEmpty()) {
            // 没有下一步，工作流完成
            completeWorkflow(workOrder);
        } else {
            // 激活下一步
            activateSteps(nextSteps);
        }
    }
    
    /**
     * 查找下一个待激活的步骤
     */
    private List<WorkflowStep> findNextSteps(List<WorkflowStep> allSteps, WorkflowStep currentStep) {
        List<WorkflowStep> nextSteps = new ArrayList<>();
        Integer currentOrder = currentStep.getStepOrder();
        Integer currentParallelGroup = currentStep.getParallelGroup();
        
        // 如果当前是并行组，找到该组的最大步骤顺序
        if (currentParallelGroup != null) {
            currentOrder = allSteps.stream()
                .filter(s -> currentParallelGroup.equals(s.getParallelGroup()))
                .map(WorkflowStep::getStepOrder)
                .max(Integer::compareTo)
                .orElse(currentOrder);
        }
        
        // 找到下一个步骤顺序
        Integer nextOrder = null;
        for (WorkflowStep step : allSteps) {
            if (step.getStepOrder() > currentOrder && 
                step.getStatus() == WorkflowStepStatus.PENDING) {
                if (nextOrder == null || step.getStepOrder() < nextOrder) {
                    nextOrder = step.getStepOrder();
                }
            }
        }
        
        // 收集下一个顺序的所有步骤（可能是并行的）
        if (nextOrder != null) {
            for (WorkflowStep step : allSteps) {
                if (step.getStepOrder().equals(nextOrder) && 
                    step.getStatus() == WorkflowStepStatus.PENDING) {
                    nextSteps.add(step);
                }
            }
        }
        
        return nextSteps;
    }
    
    /**
     * 激活步骤
     */
    private void activateSteps(List<WorkflowStep> steps) {
        for (WorkflowStep step : steps) {
            step.startStep();
            // 重新分配处理人（如果需要）
            if (step.getAssignee() == null) {
                assignStepHandler(step);
            }
        }
        stepRepository.saveAll(steps);
    }
    
    /**
     * 完成工作流
     */
    private void completeWorkflow(WorkOrder workOrder) {
        updateWorkOrderStatus(workOrder, Status.COMPLETED);
    }
    
    /**
     * 更新工单状态
     */
    private void updateWorkOrderStatus(WorkOrder workOrder, Status status) {
        workOrder.setStatus(status);
        workOrder.setUpdatedAt(LocalDateTime.now());
        workOrderRepository.save(workOrder);
    }
    
    /**
     * 自动处理超时步骤
     */
    public void processOverdueSteps() {
        List<WorkflowStep> overdueSteps = stepRepository.findAll().stream()
            .filter(WorkflowStep::isOverdue)
            .collect(Collectors.toList());
        
        for (WorkflowStep step : overdueSteps) {
            // 获取模板配置，检查是否自动通过
            WorkflowTemplate template = templateRepository
                .findByWorkOrderTypeAndIsActive(step.getWorkOrder().getType(), true)
                .orElse(null);
            
            if (template != null) {
                WorkflowTemplateStep templateStep = template.getSteps().stream()
                    .filter(ts -> ts.getStepOrder().equals(step.getStepOrder()))
                    .findFirst()
                    .orElse(null);
                
                if (templateStep != null && Boolean.TRUE.equals(templateStep.getAutoApprove())) {
                    // 自动通过
                    completeStep(step.getId(), "系统自动通过（超时）", ApprovalStatus.APPROVED);
                }
            }
        }
    }
    
    /**
     * 获取用户待处理的步骤
     */
    public List<WorkflowStep> getUserPendingSteps(Integer userId) {
        return stepRepository.findPendingStepsByAssigneeId(userId);
    }
    
    /**
     * 获取部门待处理的步骤
     */
    public List<WorkflowStep> getDepartmentPendingSteps(Integer departmentId) {
        return stepRepository.findPendingStepsByDepartmentId(departmentId);
    }
} 