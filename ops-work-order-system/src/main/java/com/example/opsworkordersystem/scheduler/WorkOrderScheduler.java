package com.example.opsworkordersystem.scheduler;

import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.service.WorkOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class WorkOrderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(WorkOrderScheduler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private WorkOrderService workOrderService;

    // 每小时执行一次检查超时工单
    @Scheduled(fixedRate = 3600000) // 1小时 = 3600000毫秒
    public void checkOverdueWorkOrders() {
        logger.info("开始检查超时工单 - {}", LocalDateTime.now().format(formatter));
        
        List<WorkOrder> overdueWorkOrders = workOrderService.checkAndUpdateOverdueWorkOrders();
        
        if (!overdueWorkOrders.isEmpty()) {
            logger.info("发现{}个超时工单，已更新状态为OVERDUE", overdueWorkOrders.size());
            for (WorkOrder workOrder : overdueWorkOrders) {
                logger.info("工单 #{} '{}' 已超时 (截止时间: {})", 
                    workOrder.getId(), 
                    workOrder.getTitle(),
                    workOrder.getDeadline().format(formatter));
            }
        } else {
            logger.info("未发现超时工单");
        }
    }

    // 每天凌晨1点生成统计报告
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailyReport() {
        logger.info("开始生成每日工单报告 - {}", LocalDateTime.now().format(formatter));
        
        // 这里可以添加生成报告的逻辑
        // ...
        
        logger.info("每日工单报告生成完成");
    }

    // 每周一凌晨2点生成周报
    @Scheduled(cron = "0 0 2 ? * MON")
    public void generateWeeklyReport() {
        logger.info("开始生成每周工单报告 - {}", LocalDateTime.now().format(formatter));
        
        // 这里可以添加生成周报的逻辑
        // ...
        
        logger.info("每周工单报告生成完成");
    }
} 