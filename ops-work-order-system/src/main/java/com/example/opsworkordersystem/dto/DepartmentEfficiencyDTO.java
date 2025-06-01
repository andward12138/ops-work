package com.example.opsworkordersystem.dto;

/**
 * 部门效率分析DTO
 */
public class DepartmentEfficiencyDTO {
    private Integer departmentId;
    private String departmentName;
    private long totalWorkOrders;
    private long completedWorkOrders;
    private long overdueWorkOrders;
    private double completionRate;
    private double averageResolutionTime; // 平均解决时间（小时）
    private double efficiency; // 效率评分

    public DepartmentEfficiencyDTO() {}

    public DepartmentEfficiencyDTO(Integer departmentId, String departmentName, 
                                  long totalWorkOrders, long completedWorkOrders, 
                                  long overdueWorkOrders, double averageResolutionTime) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.totalWorkOrders = totalWorkOrders;
        this.completedWorkOrders = completedWorkOrders;
        this.overdueWorkOrders = overdueWorkOrders;
        this.averageResolutionTime = averageResolutionTime;
        
        // 计算完成率
        if (totalWorkOrders > 0) {
            this.completionRate = (double) completedWorkOrders / totalWorkOrders * 100;
        } else {
            this.completionRate = 0.0;
        }
        
        // 计算效率评分（考虑完成率和超时率）
        calculateEfficiency();
    }

    // Getters and Setters
    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public long getTotalWorkOrders() {
        return totalWorkOrders;
    }

    public void setTotalWorkOrders(long totalWorkOrders) {
        this.totalWorkOrders = totalWorkOrders;
        updateCompletionRate();
        calculateEfficiency();
    }

    public long getCompletedWorkOrders() {
        return completedWorkOrders;
    }

    public void setCompletedWorkOrders(long completedWorkOrders) {
        this.completedWorkOrders = completedWorkOrders;
        updateCompletionRate();
        calculateEfficiency();
    }

    public long getOverdueWorkOrders() {
        return overdueWorkOrders;
    }

    public void setOverdueWorkOrders(long overdueWorkOrders) {
        this.overdueWorkOrders = overdueWorkOrders;
        calculateEfficiency();
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
        calculateEfficiency();
    }

    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    private void updateCompletionRate() {
        if (totalWorkOrders > 0) {
            this.completionRate = (double) completedWorkOrders / totalWorkOrders * 100;
        } else {
            this.completionRate = 0.0;
        }
    }

    private void calculateEfficiency() {
        if (totalWorkOrders == 0) {
            this.efficiency = 0.0;
            return;
        }
        
        // 效率评分算法：
        // 基础分 = 完成率 * 0.6
        // 超时惩罚 = (超时工单数 / 总工单数) * 30
        // 最终得分 = 基础分 - 超时惩罚 + 时间奖励
        
        double baseScore = completionRate * 0.6;
        double overtimePenalty = ((double) overdueWorkOrders / totalWorkOrders) * 30;
        
        // 时间奖励：平均解决时间越短，奖励越高（最高40分）
        double timeBonus = 0;
        if (averageResolutionTime > 0 && averageResolutionTime <= 24) {
            timeBonus = 40 - (averageResolutionTime / 24) * 40;
        }
        
        this.efficiency = Math.max(0, Math.min(100, baseScore - overtimePenalty + timeBonus));
    }

    /**
     * 获取效率等级
     */
    public String getEfficiencyLevel() {
        if (efficiency >= 90) return "优秀";
        else if (efficiency >= 80) return "良好";
        else if (efficiency >= 70) return "一般";
        else if (efficiency >= 60) return "较差";
        else return "很差";
    }
} 