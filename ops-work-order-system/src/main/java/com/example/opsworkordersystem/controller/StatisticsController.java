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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException, UnsupportedEncodingException {
        
        List<DailyStatisticsDTO> dailyStats = statisticsService.getDailyStatistics(startDate, endDate);
        
        // 验证数据不为空
        if (dailyStats == null || dailyStats.isEmpty()) {
            throw new IOException("没有找到指定日期范围内的统计数据");
        }
        
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
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException, UnsupportedEncodingException {
        
        List<WeeklyStatisticsDTO> weeklyStats = statisticsService.getWeeklyStatistics(startDate, endDate);
        
        // 验证数据不为空
        if (weeklyStats == null || weeklyStats.isEmpty()) {
            throw new IOException("没有找到指定日期范围内的周统计数据");
        }
        
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
    public ResponseEntity<byte[]> exportOverdueWarningsExcel() throws IOException, UnsupportedEncodingException {
        List<OverdueWarningDTO> overdueWarnings = statisticsService.getOverdueWarnings();
        
        // 验证数据不为空
        if (overdueWarnings == null || overdueWarnings.isEmpty()) {
            throw new IOException("当前没有超时预警数据");
        }
        
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
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException, UnsupportedEncodingException {
        
        List<DepartmentEfficiencyDTO> efficiencyStats = statisticsService.getDepartmentEfficiency(startDate, endDate);
        
        // 验证数据不为空
        if (efficiencyStats == null || efficiencyStats.isEmpty()) {
            throw new IOException("没有找到指定日期范围内的部门效率数据");
        }
        
        byte[] excelData = excelExportService.exportDepartmentEfficiency(efficiencyStats);
        
        String fileName = String.format("部门效率分析_%s至%s.xlsx", 
                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        return createExcelResponse(excelData, fileName);
    }

    /**
     * 测试Excel导出功能
     */
    @GetMapping("/export/test")
    public ResponseEntity<byte[]> exportTestExcel() throws IOException, UnsupportedEncodingException {
        try {
            // 创建一个简单的测试Excel
            byte[] testExcelData = createTestExcel();
            String fileName = "测试文件_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            
            return createExcelResponse(testExcelData, fileName);
        } catch (Exception e) {
            throw new IOException("Excel导出测试失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建测试Excel文件
     */
    private byte[] createTestExcel() throws IOException {
        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("测试数据");
            
            // 创建表头
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("列1");
            headerRow.createCell(1).setCellValue("列2");
            headerRow.createCell(2).setCellValue("列3");
            
            // 创建测试数据
            for (int i = 1; i <= 5; i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i);
                row.createCell(0).setCellValue("数据" + i);
                row.createCell(1).setCellValue(i * 10);
                row.createCell(2).setCellValue("测试内容" + i);
            }
            
            // 自动调整列宽
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 转换为字节数组
            try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
                workbook.write(out);
                out.flush();
                return out.toByteArray();
            }
        }
    }

    /**
     * 创建Excel下载响应
     */
    private ResponseEntity<byte[]> createExcelResponse(byte[] excelData, String fileName) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        
        // 设置正确的Excel文件MIME类型
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        
        // 设置文件名，兼容不同浏览器
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        
        // 设置多种Content-Disposition格式以兼容不同浏览器
        headers.set("Content-Disposition", "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName);
        
        // 设置内容长度
        headers.setContentLength(excelData.length);
        
        // 添加CORS和缓存控制头
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Expose-Headers", "Content-Disposition");
        headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.set("Pragma", "no-cache");
        headers.set("Expires", "0");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
} 