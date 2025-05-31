package com.example.opsworkordersystem.scheduler;

import com.example.opsworkordersystem.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkflowScheduler {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 每小时检查一次超时的工作流步骤
     */
    @Scheduled(fixedDelay = 3600000) // 1小时 = 3600000毫秒
    public void processOverdueWorkflowSteps() {
        System.out.println("开始处理超时的工作流步骤...");
        try {
            workflowService.processOverdueSteps();
            System.out.println("超时工作流步骤处理完成");
        } catch (Exception e) {
            System.err.println("处理超时工作流步骤时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 