package com.example.opsworkordersystem.dto;

import java.time.LocalDate;

/**
 * 每日工单统计DTO
 */
public class DailyStatisticsDTO {
    private LocalDate date;
    private long totalWorkOrders;
    private long newCount; // 新建工单数量
    private long pendingCount;
    private long approvedCount;
    private long inProgressCount;
    private long completedCount;
    private long rejectedCount;
    private long overdueCount;
    private double completionRate;
    private Double averageProcessingHours; // 平均处理时间（小时）

    public DailyStatisticsDTO() {}

    public DailyStatisticsDTO(LocalDate date, long totalWorkOrders, long pendingCount, 
                             long approvedCount, long inProgressCount, long completedCount, 
                             long rejectedCount, long overdueCount) {
        this.date = date;
        this.totalWorkOrders = totalWorkOrders;
        this.newCount = totalWorkOrders; // 当天创建的工单都是新建工单
        this.pendingCount = pendingCount;
        this.approvedCount = approvedCount;
        this.inProgressCount = inProgressCount;
        this.completedCount = completedCount;
        this.rejectedCount = rejectedCount;
        this.overdueCount = overdueCount;
        
        // 计算完成率
        if (totalWorkOrders > 0) {
            this.completionRate = (double) completedCount / totalWorkOrders * 100;
        } else {
            this.completionRate = 0.0;
        }
    }

    // 带平均处理时间的构造函数
    public DailyStatisticsDTO(LocalDate date, long totalWorkOrders, long pendingCount, 
                             long approvedCount, long inProgressCount, long completedCount, 
                             long rejectedCount, long overdueCount, Double averageProcessingHours) {
        this(date, totalWorkOrders, pendingCount, approvedCount, inProgressCount, 
             completedCount, rejectedCount, overdueCount);
        this.averageProcessingHours = averageProcessingHours;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getTotalWorkOrders() {
        return totalWorkOrders;
    }

    public void setTotalWorkOrders(long totalWorkOrders) {
        this.totalWorkOrders = totalWorkOrders;
        updateCompletionRate();
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(long approvedCount) {
        this.approvedCount = approvedCount;
    }

    public long getInProgressCount() {
        return inProgressCount;
    }

    public void setInProgressCount(long inProgressCount) {
        this.inProgressCount = inProgressCount;
    }

    public long getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(long completedCount) {
        this.completedCount = completedCount;
        updateCompletionRate();
    }

    public long getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(long rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public long getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(long overdueCount) {
        this.overdueCount = overdueCount;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public long getNewCount() {
        return newCount;
    }

    public void setNewCount(long newCount) {
        this.newCount = newCount;
    }

    public Double getAverageProcessingHours() {
        return averageProcessingHours;
    }

    public void setAverageProcessingHours(Double averageProcessingHours) {
        this.averageProcessingHours = averageProcessingHours;
    }

    private void updateCompletionRate() {
        if (totalWorkOrders > 0) {
            this.completionRate = (double) completedCount / totalWorkOrders * 100;
        } else {
            this.completionRate = 0.0;
        }
    }
} 