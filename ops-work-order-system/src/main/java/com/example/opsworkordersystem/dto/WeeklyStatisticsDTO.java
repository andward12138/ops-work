package com.example.opsworkordersystem.dto;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

/**
 * 每周工单统计DTO
 */
public class WeeklyStatisticsDTO {
    private int weekNumber; // 周数
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private LocalDate weekStartDate; // 别名，和weekStart相同
    private LocalDate weekEndDate; // 别名，和weekEnd相同
    private long totalWorkOrders;
    private long totalCompleted; // 已完成总数
    private long totalInProgress; // 处理中总数
    private long totalPending; // 待处理总数
    private long totalRejected; // 已拒绝总数
    private long completedWorkOrders;
    private long overdueWorkOrders;
    private double completionRate;
    private double weeklyCompletionRate; // 别名，和completionRate相同
    private double averageResolutionTime; // 平均解决时间（小时）
    private List<DailyStatisticsDTO> dailyStatistics;

    public WeeklyStatisticsDTO() {}

    public WeeklyStatisticsDTO(LocalDate weekStart, LocalDate weekEnd, long totalWorkOrders, 
                              long completedWorkOrders, long overdueWorkOrders, 
                              double averageResolutionTime, List<DailyStatisticsDTO> dailyStatistics) {
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.weekStartDate = weekStart; // 设置别名
        this.weekEndDate = weekEnd; // 设置别名
        this.totalWorkOrders = totalWorkOrders;
        this.completedWorkOrders = completedWorkOrders;
        this.totalCompleted = completedWorkOrders; // 设置别名
        this.overdueWorkOrders = overdueWorkOrders;
        this.averageResolutionTime = averageResolutionTime;
        this.dailyStatistics = dailyStatistics;
        
        // 计算周数
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        this.weekNumber = weekStart.get(weekFields.weekOfWeekBasedYear());
        
        // 从dailyStatistics计算各状态的统计数据
        if (dailyStatistics != null && !dailyStatistics.isEmpty()) {
            this.totalInProgress = dailyStatistics.stream().mapToLong(DailyStatisticsDTO::getInProgressCount).sum();
            this.totalPending = dailyStatistics.stream().mapToLong(DailyStatisticsDTO::getPendingCount).sum();
            this.totalRejected = dailyStatistics.stream().mapToLong(DailyStatisticsDTO::getRejectedCount).sum();
        }
        
        // 计算完成率
        if (totalWorkOrders > 0) {
            this.completionRate = (double) completedWorkOrders / totalWorkOrders * 100;
        } else {
            this.completionRate = 0.0;
        }
        this.weeklyCompletionRate = this.completionRate; // 设置别名
    }

    // Getters and Setters
    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
        this.weekStart = weekStartDate;
    }

    public LocalDate getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(LocalDate weekEndDate) {
        this.weekEndDate = weekEndDate;
        this.weekEnd = weekEndDate;
    }

    public long getTotalCompleted() {
        return totalCompleted;
    }

    public void setTotalCompleted(long totalCompleted) {
        this.totalCompleted = totalCompleted;
        this.completedWorkOrders = totalCompleted;
        updateCompletionRate();
    }

    public long getTotalInProgress() {
        return totalInProgress;
    }

    public void setTotalInProgress(long totalInProgress) {
        this.totalInProgress = totalInProgress;
    }

    public long getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(long totalPending) {
        this.totalPending = totalPending;
    }

    public long getTotalRejected() {
        return totalRejected;
    }

    public void setTotalRejected(long totalRejected) {
        this.totalRejected = totalRejected;
    }

    public double getWeeklyCompletionRate() {
        return weeklyCompletionRate;
    }

    public void setWeeklyCompletionRate(double weeklyCompletionRate) {
        this.weeklyCompletionRate = weeklyCompletionRate;
        this.completionRate = weeklyCompletionRate;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(LocalDate weekStart) {
        this.weekStart = weekStart;
    }

    public LocalDate getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(LocalDate weekEnd) {
        this.weekEnd = weekEnd;
    }

    public long getTotalWorkOrders() {
        return totalWorkOrders;
    }

    public void setTotalWorkOrders(long totalWorkOrders) {
        this.totalWorkOrders = totalWorkOrders;
        updateCompletionRate();
    }

    public long getCompletedWorkOrders() {
        return completedWorkOrders;
    }

    public void setCompletedWorkOrders(long completedWorkOrders) {
        this.completedWorkOrders = completedWorkOrders;
        updateCompletionRate();
    }

    public long getOverdueWorkOrders() {
        return overdueWorkOrders;
    }

    public void setOverdueWorkOrders(long overdueWorkOrders) {
        this.overdueWorkOrders = overdueWorkOrders;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public double getAverageResolutionTime() {
        return averageResolutionTime;
    }

    public void setAverageResolutionTime(double averageResolutionTime) {
        this.averageResolutionTime = averageResolutionTime;
    }

    public List<DailyStatisticsDTO> getDailyStatistics() {
        return dailyStatistics;
    }

    public void setDailyStatistics(List<DailyStatisticsDTO> dailyStatistics) {
        this.dailyStatistics = dailyStatistics;
    }

    private void updateCompletionRate() {
        if (totalWorkOrders > 0) {
            this.completionRate = (double) completedWorkOrders / totalWorkOrders * 100;
        } else {
            this.completionRate = 0.0;
        }
    }
} 