package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.dto.TransferRecordDTO;
import com.example.opsworkordersystem.dto.UserDto;
import com.example.opsworkordersystem.entity.TransferType;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.entity.Department;
import com.example.opsworkordersystem.service.TransferRecordService;
import com.example.opsworkordersystem.service.IUserService;
import com.example.opsworkordersystem.service.DepartmentService;
import com.example.opsworkordersystem.repository.TransferRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/transfer")
public class TransferRecordController {

    @Autowired
    private TransferRecordService transferRecordService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private TransferRecordRepository transferRecordRepository;

    /**
     * 转派管理主页 - 最终优化版本
     */
    @GetMapping
    public String transferIndex(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userService.loadUserByUsername(username);
            
            // 使用更智能的异步加载策略，只加载必要的数据
            CompletableFuture<List<TransferRecordDTO>> pendingTransfersFuture = 
                CompletableFuture.supplyAsync(() -> {
                    List<TransferRecordDTO> pending = transferRecordService.getPendingTransfersForUser(currentUser.getId());
                    // 只取前10条，减少内存占用
                    return pending.stream().limit(10).collect(Collectors.toList());
                });
            
            CompletableFuture<List<TransferRecordDTO>> userTransfersFuture = 
                CompletableFuture.supplyAsync(() -> {
                    List<TransferRecordDTO> userTransfers = transferRecordService.getUserRequestedTransfers(currentUser.getId());
                    // 只取前10条最新的
                    return userTransfers.stream().limit(10).collect(Collectors.toList());
                });
            
            CompletableFuture<TransferRecordService.TransferStatisticsDTO> statisticsFuture = 
                CompletableFuture.supplyAsync(() -> transferRecordService.getTransferStatistics());
            
            // 等待所有异步任务完成，设置超时避免长时间等待
            try {
                List<TransferRecordDTO> pendingTransfers = pendingTransfersFuture.get(3, java.util.concurrent.TimeUnit.SECONDS);
                List<TransferRecordDTO> userTransfers = userTransfersFuture.get(3, java.util.concurrent.TimeUnit.SECONDS);
                TransferRecordService.TransferStatisticsDTO statistics = statisticsFuture.get(2, java.util.concurrent.TimeUnit.SECONDS);
                
                model.addAttribute("pendingTransfers", pendingTransfers);
                model.addAttribute("userTransfers", userTransfers);
                model.addAttribute("statistics", statistics);
                model.addAttribute("currentUser", currentUser);
                
            } catch (java.util.concurrent.TimeoutException e) {
                // 超时处理，提供默认数据
                model.addAttribute("pendingTransfers", List.of());
                model.addAttribute("userTransfers", List.of());
                model.addAttribute("statistics", new TransferRecordService.TransferStatisticsDTO());
                model.addAttribute("warning", "数据加载超时，请刷新页面重试");
            }
            
        } catch (Exception e) {
            // 错误处理，提供默认值
            model.addAttribute("pendingTransfers", List.of());
            model.addAttribute("userTransfers", List.of());
            model.addAttribute("statistics", new TransferRecordService.TransferStatisticsDTO());
            model.addAttribute("error", "加载数据时出现错误，请稍后重试");
        }
        
        return "transfer/index";
    }

    /**
     * 转派历史页面 - 优化版本
     */
    @GetMapping("/history")
    public String transferHistory(Model model, @RequestParam(required = false) Integer workOrderId) {
        try {
            List<TransferRecordDTO> transferHistory;
            
            if (workOrderId != null) {
                transferHistory = transferRecordService.getWorkOrderTransferHistory(workOrderId);
                model.addAttribute("workOrderId", workOrderId);
            } else {
                transferHistory = transferRecordService.getAllTransferRecords();
            }
            
            model.addAttribute("transferHistory", transferHistory);
        } catch (Exception e) {
            model.addAttribute("transferHistory", List.of());
            model.addAttribute("error", "加载转派历史时出现错误，请稍后重试");
        }
        
        return "transfer/history";
    }

    /**
     * 创建转派页面 - 懒加载优化版本
     */
    @GetMapping("/create")
    public String createTransferPage(@RequestParam Integer workOrderId, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userService.loadUserByUsername(username);
            
            // 检查转派权限
            if (!transferRecordService.canTransferWorkOrder(workOrderId, currentUser.getId())) {
                model.addAttribute("error", "您没有权限转派此工单");
                return "redirect:/work-orders/details/" + workOrderId;
            }
            
            // 延迟加载用户和部门数据，只在需要时加载
            model.addAttribute("workOrderId", workOrderId);
            // 不预加载用户和部门列表，改为前端AJAX懒加载
            
        } catch (Exception e) {
            model.addAttribute("error", "加载创建转派页面时出现错误: " + e.getMessage());
            return "redirect:/work-orders/details/" + workOrderId;
        }
        
        return "transfer/create";
    }

    /**
     * 创建转派记录 - API优化版本
     */
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createTransfer(@RequestBody CreateTransferRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 输入验证
            if (request.getWorkOrderId() == null) {
                response.put("success", false);
                response.put("message", "工单ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getTransferType() == null) {
                response.put("success", false);
                response.put("message", "转派类型不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userService.loadUserByUsername(username);
            
            // 检查权限
            if (!transferRecordService.canTransferWorkOrder(request.getWorkOrderId(), currentUser.getId())) {
                response.put("success", false);
                response.put("message", "您没有权限转派此工单");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 创建转派记录
            transferRecordService.createTransferRecord(
                request.getWorkOrderId(),
                currentUser.getId(),
                request.getTransferType(),
                request.getTransferReason(),
                request.getToUserId(),
                request.getToDepartmentId(),
                request.getIsAssistance()
            );
            
            response.put("success", true);
            response.put("message", "转派请求已创建");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建转派失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 接受转派 - API优化版本
     */
    @PostMapping("/api/{transferRecordId}/accept")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> acceptTransfer(@PathVariable Integer transferRecordId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userService.loadUserByUsername(username);
            
            transferRecordService.acceptTransfer(transferRecordId, currentUser.getId());
            
            response.put("success", true);
            response.put("message", "转派已接受");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "接受转派失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 拒绝转派 - API优化版本
     */
    @PostMapping("/api/{transferRecordId}/reject")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> rejectTransfer(@PathVariable Integer transferRecordId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            transferRecordService.rejectTransfer(transferRecordId);
            
            response.put("success", true);
            response.put("message", "转派已拒绝");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "拒绝转派失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 完成转派 - API优化版本
     */
    @PostMapping("/api/{transferRecordId}/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeTransfer(@PathVariable Integer transferRecordId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            transferRecordService.completeTransfer(transferRecordId);
            
            response.put("success", true);
            response.put("message", "转派已完成");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "完成转派失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 取消转派 - API优化版本
     */
    @PostMapping("/api/{transferRecordId}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelTransfer(@PathVariable Integer transferRecordId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            transferRecordService.cancelTransfer(transferRecordId);
            
            response.put("success", true);
            response.put("message", "转派已取消");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "取消转派失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取工单转派历史 - API优化版本
     */
    @GetMapping("/api/history/{workOrderId}")
    @ResponseBody
    public ResponseEntity<List<TransferRecordDTO>> getWorkOrderTransferHistory(@PathVariable Integer workOrderId) {
        try {
            List<TransferRecordDTO> history = transferRecordService.getWorkOrderTransferHistory(workOrderId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取用户列表 - API，支持搜索和分页
     */
    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(defaultValue = "0") int page, 
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @RequestParam(required = false) String search) {
        try {
            // 限制每页最大数量，防止过度查询
            size = Math.min(size, 50);
            
            List<UserDto> users = userService.getAllUsers(PageRequest.of(page, size)).getContent();
            
            // 如果有搜索条件，进行过滤
            if (search != null && !search.trim().isEmpty()) {
                final String searchLower = search.toLowerCase().trim();
                users = users.stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
            }
            
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取部门列表 - API，支持搜索
     */
    @GetMapping("/api/departments")
    @ResponseBody
    public ResponseEntity<List<Department>> getDepartments(@RequestParam(required = false) String search) {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            
            // 如果有搜索条件，进行过滤
            if (search != null && !search.trim().isEmpty()) {
                final String searchLower = search.toLowerCase().trim();
                departments = departments.stream()
                    .filter(dept -> dept.getName().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
            }
            
            // 限制返回数量
            if (departments.size() > 30) {
                departments = departments.subList(0, 30);
            }
            
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 快速统计API - 用于实时更新统计数据
     */
    @GetMapping("/api/quick-stats")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getQuickStats() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userService.loadUserByUsername(username);
            
            Map<String, Long> stats = new HashMap<>();
            
            // 使用高性能的单独统计查询
            CompletableFuture<Long> pendingCountFuture = CompletableFuture.supplyAsync(
                () -> transferRecordRepository.countPendingByUserId(currentUser.getId()));
            
            CompletableFuture<Long> userRequestedCountFuture = CompletableFuture.supplyAsync(
                () -> transferRecordRepository.countByRequestedById(currentUser.getId()));
            
            // 等待查询完成
            stats.put("pendingCount", pendingCountFuture.get(1, java.util.concurrent.TimeUnit.SECONDS));
            stats.put("userRequestedCount", userRequestedCountFuture.get(1, java.util.concurrent.TimeUnit.SECONDS));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Long> emptyStats = new HashMap<>();
            emptyStats.put("pendingCount", 0L);
            emptyStats.put("userRequestedCount", 0L);
            return ResponseEntity.ok(emptyStats);
        }
    }

    // 内部请求类
    public static class CreateTransferRequest {
        private Integer workOrderId;
        private TransferType transferType;
        private String transferReason;
        private Integer toUserId;
        private Integer toDepartmentId;
        private Boolean isAssistance;

        // Getters and Setters
        public Integer getWorkOrderId() { return workOrderId; }
        public void setWorkOrderId(Integer workOrderId) { this.workOrderId = workOrderId; }

        public TransferType getTransferType() { return transferType; }
        public void setTransferType(TransferType transferType) { this.transferType = transferType; }

        public String getTransferReason() { return transferReason; }
        public void setTransferReason(String transferReason) { this.transferReason = transferReason; }

        public Integer getToUserId() { return toUserId; }
        public void setToUserId(Integer toUserId) { this.toUserId = toUserId; }

        public Integer getToDepartmentId() { return toDepartmentId; }
        public void setToDepartmentId(Integer toDepartmentId) { this.toDepartmentId = toDepartmentId; }

        public Boolean getIsAssistance() { return isAssistance; }
        public void setIsAssistance(Boolean isAssistance) { this.isAssistance = isAssistance; }
    }
} 