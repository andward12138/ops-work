package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.dto.*;
import com.example.opsworkordersystem.service.StatisticsService;
import com.example.opsworkordersystem.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;
    
    @Autowired
    private ExcelExportService excelExportService;

    /**
     * 统计报表主页
     */
    @GetMapping
    public String statisticsIndex(Model model) {
        // 获取首页统计摘要
        Map<String, Object> summary = statisticsService.getDashboardSummary();
        model.addAttribute("summary", summary);
        
        // 获取月度对比数据
        Map<String, Object> monthlyComparison = statisticsService.getMonthlyComparison();
        model.addAttribute("monthlyComparison", monthlyComparison);
        
        return "statistics/index";
    }

    /**
     * 每日统计页面
     */
    @GetMapping("/daily")
    public String dailyStatistics(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                  Model model) {
        // 默认显示最近7天的数据
        if (startDate == null) startDate = LocalDate.now().minusDays(6);
        if (endDate == null) endDate = LocalDate.now();
        
        List<DailyStatisticsDTO> dailyStats = statisticsService.getDailyStatistics(startDate, endDate);
        
        model.addAttribute("dailyStats", dailyStats);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "statistics/daily";
    }

    /**
     * 每周统计页面
     */
    @GetMapping("/weekly")
    public String weeklyStatistics(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                   Model model) {
        // 默认显示最近4周的数据
        if (startDate == null) startDate = LocalDate.now().minusWeeks(3);
        if (endDate == null) endDate = LocalDate.now();
        
        List<WeeklyStatisticsDTO> weeklyStats = statisticsService.getWeeklyStatistics(startDate, endDate);
        
        model.addAttribute("weeklyStats", weeklyStats);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "statistics/weekly";
    }

    /**
     * 超时预警页面
     */
    @GetMapping("/overdue")
    public String overdueWarnings(Model model) {
        List<OverdueWarningDTO> overdueWarnings = statisticsService.getOverdueWarnings();
        
        // 计算各预警级别的统计数据
        long mildCount = overdueWarnings.stream()
                .filter(w -> "轻微".equals(w.getWarningLevel()))
                .count();
        long severeCount = overdueWarnings.stream()
                .filter(w -> "严重".equals(w.getWarningLevel()))
                .count();
        long urgentCount = overdueWarnings.stream()
                .filter(w -> "紧急".equals(w.getWarningLevel()))
                .count();
        
        model.addAttribute("overdueWarnings", overdueWarnings);
        model.addAttribute("mildCount", mildCount);
        model.addAttribute("severeCount", severeCount);
        model.addAttribute("urgentCount", urgentCount);
        model.addAttribute("totalCount", overdueWarnings.size());
        
        return "statistics/overdue";
    }

    /**
     * 部门效率分析页面
     */
    @GetMapping("/department-efficiency")
    public String departmentEfficiency(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                       Model model) {
        // 默认显示最近30天的数据
        if (startDate == null) startDate = LocalDate.now().minusDays(29);
        if (endDate == null) endDate = LocalDate.now();
        
        List<DepartmentEfficiencyDTO> efficiencyStats = statisticsService.getDepartmentEfficiency(startDate, endDate);
        
        model.addAttribute("efficiencyStats", efficiencyStats);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "statistics/department-efficiency";
    }

    // ========== API 接口 ==========

    /**
     * 获取每日统计数据API
     */
    @GetMapping("/api/daily")
    @ResponseBody
    public ResponseEntity<List<DailyStatisticsDTO>> getDailyStatisticsAPI(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<DailyStatisticsDTO> dailyStats = statisticsService.getDailyStatistics(startDate, endDate);
        return ResponseEntity.ok(dailyStats);
    }

    /**
     * 获取每周统计数据API
     */
    @GetMapping("/api/weekly")
    @ResponseBody
    public ResponseEntity<List<WeeklyStatisticsDTO>> getWeeklyStatisticsAPI(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<WeeklyStatisticsDTO> weeklyStats = statisticsService.getWeeklyStatistics(startDate, endDate);
        return ResponseEntity.ok(weeklyStats);
    }

    /**
     * 获取超时预警数据API
     */
    @GetMapping("/api/overdue")
    @ResponseBody
    public ResponseEntity<List<OverdueWarningDTO>> getOverdueWarningsAPI() {
        List<OverdueWarningDTO> overdueWarnings = statisticsService.getOverdueWarnings();
        return ResponseEntity.ok(overdueWarnings);
    }

    /**
     * 获取部门效率分析数据API
     */
    @GetMapping("/api/department-efficiency")
    @ResponseBody
    public ResponseEntity<List<DepartmentEfficiencyDTO>> getDepartmentEfficiencyAPI(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<DepartmentEfficiencyDTO> efficiencyStats = statisticsService.getDepartmentEfficiency(startDate, endDate);
        return ResponseEntity.ok(efficiencyStats);
    }

    /**
     * 获取首页统计摘要API
     */
    @GetMapping("/api/dashboard")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardSummaryAPI() {
        Map<String, Object> summary = statisticsService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    /**
     * 获取月度对比数据API
     */
    @GetMapping("/api/monthly-comparison")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMonthlyComparisonAPI() {
        Map<String, Object> comparison = statisticsService.getMonthlyComparison();
        return ResponseEntity.ok(comparison);
    }

    // ========== Excel 导出接口 ==========

    /**
     * 导出每日统计Excel
     */
    @GetMapping("/export/daily")
    public ResponseEntity<byte[]> exportDailyStatisticsExcel(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException {
        
        List<DailyStatisticsDTO> dailyStats = statisticsService.getDailyStatistics(startDate, endDate);
        byte[] excelData = excelExportService.exportDailyStatistics(dailyStats);
        
        String fileName = String.format("每日工单统计_%s至%s.xlsx", 
                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        return createExcelResponse(excelData, fileName);
    }

    /**
     * 导出每周统计Excel
     */
    @GetMapping("/export/weekly")
    public ResponseEntity<byte[]> exportWeeklyStatisticsExcel(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException {
        
        List<WeeklyStatisticsDTO> weeklyStats = statisticsService.getWeeklyStatistics(startDate, endDate);
        byte[] excelData = excelExportService.exportWeeklyStatistics(weeklyStats);
        
        String fileName = String.format("每周工单统计_%s至%s.xlsx", 
                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        return createExcelResponse(excelData, fileName);
    }

    /**
     * 导出超时预警Excel
     */
    @GetMapping("/export/overdue")
    public ResponseEntity<byte[]> exportOverdueWarningsExcel() throws IOException {
        List<OverdueWarningDTO> overdueWarnings = statisticsService.getOverdueWarnings();
        byte[] excelData = excelExportService.exportOverdueWarnings(overdueWarnings);
        
        String fileName = String.format("超时预警列表_%s.xlsx", 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        return createExcelResponse(excelData, fileName);
    }

    /**
     * 导出部门效率分析Excel
     */
    @GetMapping("/export/department-efficiency")
    public ResponseEntity<byte[]> exportDepartmentEfficiencyExcel(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException {
        
        List<DepartmentEfficiencyDTO> efficiencyStats = statisticsService.getDepartmentEfficiency(startDate, endDate);
        byte[] excelData = excelExportService.exportDepartmentEfficiency(efficiencyStats);
        
        String fileName = String.format("部门效率分析_%s至%s.xlsx", 
                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        return createExcelResponse(excelData, fileName);
    }

    /**
     * 创建Excel下载响应
     */
    private ResponseEntity<byte[]> createExcelResponse(byte[] excelData, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelData.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
} 