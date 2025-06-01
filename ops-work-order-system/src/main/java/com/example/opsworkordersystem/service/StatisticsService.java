package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.dto.*;
import com.example.opsworkordersystem.entity.*;
import com.example.opsworkordersystem.repository.WorkOrderRepository;
import com.example.opsworkordersystem.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 获取每日工单统计
     */
    public List<DailyStatisticsDTO> getDailyStatistics(LocalDate startDate, LocalDate endDate) {
        List<DailyStatisticsDTO> dailyStats = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            
            List<WorkOrder> dayWorkOrders = workOrderRepository.findByCreatedAtBetween(dayStart, dayEnd);
            
            long totalCount = dayWorkOrders.size();
            long pendingCount = dayWorkOrders.stream().mapToLong(wo -> wo.getStatus() == Status.PENDING ? 1 : 0).sum();
            long approvedCount = dayWorkOrders.stream().mapToLong(wo -> wo.getStatus() == Status.APPROVED ? 1 : 0).sum();
            long inProgressCount = dayWorkOrders.stream().mapToLong(wo -> wo.getStatus() == Status.IN_PROGRESS ? 1 : 0).sum();
            long completedCount = dayWorkOrders.stream().mapToLong(wo -> wo.getStatus() == Status.COMPLETED ? 1 : 0).sum();
            long rejectedCount = dayWorkOrders.stream().mapToLong(wo -> wo.getStatus() == Status.REJECTED ? 1 : 0).sum();
            long overdueCount = dayWorkOrders.stream().mapToLong(wo -> Boolean.TRUE.equals(wo.getIsOverdue()) ? 1 : 0).sum();
            
            DailyStatisticsDTO dailyStat = new DailyStatisticsDTO(date, totalCount, pendingCount, 
                    approvedCount, inProgressCount, completedCount, rejectedCount, overdueCount);
            dailyStats.add(dailyStat);
        }
        
        return dailyStats;
    }

    /**
     * 获取每周工单统计
     */
    public List<WeeklyStatisticsDTO> getWeeklyStatistics(LocalDate startDate, LocalDate endDate) {
        List<WeeklyStatisticsDTO> weeklyStats = new ArrayList<>();
        
        LocalDate weekStart = startDate;
        while (!weekStart.isAfter(endDate)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(endDate)) {
                weekEnd = endDate;
            }
            
            List<DailyStatisticsDTO> dailyStats = getDailyStatistics(weekStart, weekEnd);
            
            long totalWorkOrders = dailyStats.stream().mapToLong(DailyStatisticsDTO::getTotalWorkOrders).sum();
            long completedWorkOrders = dailyStats.stream().mapToLong(DailyStatisticsDTO::getCompletedCount).sum();
            long overdueWorkOrders = dailyStats.stream().mapToLong(DailyStatisticsDTO::getOverdueCount).sum();
            
            // 计算平均解决时间
            double avgResolutionTime = calculateAverageResolutionTime(weekStart.atStartOfDay(), weekEnd.plusDays(1).atStartOfDay());
            
            WeeklyStatisticsDTO weeklyStat = new WeeklyStatisticsDTO(weekStart, weekEnd, 
                    totalWorkOrders, completedWorkOrders, overdueWorkOrders, avgResolutionTime, dailyStats);
            weeklyStats.add(weeklyStat);
            
            weekStart = weekStart.plusDays(7);
        }
        
        return weeklyStats;
    }

    /**
     * 获取超时预警列表
     */
    public List<OverdueWarningDTO> getOverdueWarnings() {
        List<WorkOrder> allWorkOrders = workOrderRepository.findAllWithUsers();
        
        return allWorkOrders.stream()
                .filter(wo -> wo.getDeadline() != null && 
                             LocalDateTime.now().isAfter(wo.getDeadline()) &&
                             wo.getStatus() != Status.COMPLETED &&
                             wo.getStatus() != Status.REJECTED)
                .map(wo -> new OverdueWarningDTO(
                    wo.getId(),
                    wo.getTitle(),
                    wo.getPriority(),
                    wo.getStatus(),
                    wo.getAssignedTo() != null ? wo.getAssignedTo().getUsername() : "未分配",
                    wo.getAssignedTo() != null && wo.getAssignedTo().getDepartment() != null ? 
                        wo.getAssignedTo().getDepartment().getName() : "未知部门",
                    wo.getDeadline(),
                    wo.getCreatedAt()
                ))
                .sorted((w1, w2) -> Long.compare(w2.getOverdueHours(), w1.getOverdueHours()))
                .collect(Collectors.toList());
    }

    /**
     * 获取部门效率分析
     */
    public List<DepartmentEfficiencyDTO> getDepartmentEfficiency(LocalDate startDate, LocalDate endDate) {
        List<Department> departments = departmentRepository.findByIsActive(true);
        List<DepartmentEfficiencyDTO> efficiencyList = new ArrayList<>();
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        
        for (Department dept : departments) {
            List<WorkOrder> deptWorkOrders = workOrderRepository.findByAssignedToDepartmentAndCreatedAtBetween(
                    dept.getId(), start, end);
            
            if (deptWorkOrders.isEmpty()) {
                continue;
            }
            
            long totalCount = deptWorkOrders.size();
            long completedCount = deptWorkOrders.stream()
                    .mapToLong(wo -> wo.getStatus() == Status.COMPLETED ? 1 : 0).sum();
            long overdueCount = deptWorkOrders.stream()
                    .mapToLong(wo -> Boolean.TRUE.equals(wo.getIsOverdue()) ? 1 : 0).sum();
            
            // 计算部门平均解决时间
            double avgResolutionTime = calculateDepartmentAverageResolutionTime(deptWorkOrders);
            
            DepartmentEfficiencyDTO efficiency = new DepartmentEfficiencyDTO(
                    dept.getId(), dept.getName(), totalCount, completedCount, 
                    overdueCount, avgResolutionTime);
            
            efficiencyList.add(efficiency);
        }
        
        // 按效率评分排序
        return efficiencyList.stream()
                .sorted((e1, e2) -> Double.compare(e2.getEfficiency(), e1.getEfficiency()))
                .collect(Collectors.toList());
    }

    /**
     * 获取首页统计摘要
     */
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // 今日统计
            LocalDate today = LocalDate.now();
            List<DailyStatisticsDTO> todayStatsList = getDailyStatistics(today, today);
            if (!todayStatsList.isEmpty()) {
                summary.put("today", todayStatsList.get(0));
            } else {
                summary.put("today", new DailyStatisticsDTO());
            }
            
            // 本周统计
            LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
            List<WeeklyStatisticsDTO> thisWeekStatsList = getWeeklyStatistics(weekStart, today);
            if (!thisWeekStatsList.isEmpty()) {
                summary.put("thisWeek", thisWeekStatsList.get(0));
            } else {
                summary.put("thisWeek", new WeeklyStatisticsDTO());
            }
            
            // 超时预警数量
            List<OverdueWarningDTO> overdueWarnings = getOverdueWarnings();
            summary.put("overdueCount", overdueWarnings.size());
            summary.put("urgentOverdueCount", overdueWarnings.stream()
                    .mapToLong(w -> "紧急".equals(w.getWarningLevel()) ? 1 : 0).sum());
            
            // 部门效率Top 5
            List<DepartmentEfficiencyDTO> topDepartments = getDepartmentEfficiency(today.minusDays(30), today)
                    .stream().limit(5).collect(Collectors.toList());
            summary.put("topDepartments", topDepartments);
            
        } catch (Exception e) {
            // 如果出现异常，返回空的统计数据
            summary.put("today", new DailyStatisticsDTO());
            summary.put("thisWeek", new WeeklyStatisticsDTO());
            summary.put("overdueCount", 0);
            summary.put("urgentOverdueCount", 0);
            summary.put("topDepartments", new ArrayList<>());
        }
        
        return summary;
    }

    /**
     * 计算平均解决时间
     */
    private double calculateAverageResolutionTime(LocalDateTime start, LocalDateTime end) {
        List<WorkOrder> completedOrders = workOrderRepository
                .findByStatusAndCreatedAtBetween(Status.COMPLETED, start, end);
        
        if (completedOrders.isEmpty()) {
            return 0.0;
        }
        
        double totalHours = completedOrders.stream()
                .filter(wo -> wo.getUpdatedAt() != null)
                .mapToDouble(wo -> ChronoUnit.HOURS.between(wo.getCreatedAt(), wo.getUpdatedAt()))
                .sum();
        
        return totalHours / completedOrders.size();
    }

    /**
     * 计算部门平均解决时间
     */
    private double calculateDepartmentAverageResolutionTime(List<WorkOrder> workOrders) {
        List<WorkOrder> completedOrders = workOrders.stream()
                .filter(wo -> wo.getStatus() == Status.COMPLETED && wo.getUpdatedAt() != null)
                .collect(Collectors.toList());
        
        if (completedOrders.isEmpty()) {
            return 0.0;
        }
        
        double totalHours = completedOrders.stream()
                .mapToDouble(wo -> ChronoUnit.HOURS.between(wo.getCreatedAt(), wo.getUpdatedAt()))
                .sum();
        
        return totalHours / completedOrders.size();
    }

    /**
     * 获取月度统计对比
     */
    public Map<String, Object> getMonthlyComparison() {
        Map<String, Object> comparison = new HashMap<>();
        
        LocalDate thisMonthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate thisMonthEnd = LocalDate.now();
        LocalDate lastMonthStart = thisMonthStart.minusMonths(1);
        LocalDate lastMonthEnd = thisMonthStart.minusDays(1);
        
        List<DailyStatisticsDTO> thisMonthStats = getDailyStatistics(thisMonthStart, thisMonthEnd);
        List<DailyStatisticsDTO> lastMonthStats = getDailyStatistics(lastMonthStart, lastMonthEnd);
        
        long thisMonthTotal = thisMonthStats.stream().mapToLong(DailyStatisticsDTO::getTotalWorkOrders).sum();
        long thisMonthCompleted = thisMonthStats.stream().mapToLong(DailyStatisticsDTO::getCompletedCount).sum();
        long lastMonthTotal = lastMonthStats.stream().mapToLong(DailyStatisticsDTO::getTotalWorkOrders).sum();
        long lastMonthCompleted = lastMonthStats.stream().mapToLong(DailyStatisticsDTO::getCompletedCount).sum();
        
        comparison.put("thisMonthTotal", thisMonthTotal);
        comparison.put("thisMonthCompleted", thisMonthCompleted);
        comparison.put("lastMonthTotal", lastMonthTotal);
        comparison.put("lastMonthCompleted", lastMonthCompleted);
        
        // 计算增长率
        if (lastMonthTotal > 0) {
            double totalGrowthRate = ((double) (thisMonthTotal - lastMonthTotal) / lastMonthTotal) * 100;
            comparison.put("totalGrowthRate", totalGrowthRate);
        } else {
            comparison.put("totalGrowthRate", 0.0);
        }
        
        if (lastMonthCompleted > 0) {
            double completionGrowthRate = ((double) (thisMonthCompleted - lastMonthCompleted) / lastMonthCompleted) * 100;
            comparison.put("completionGrowthRate", completionGrowthRate);
        } else {
            comparison.put("completionGrowthRate", 0.0);
        }
        
        return comparison;
    }
} 