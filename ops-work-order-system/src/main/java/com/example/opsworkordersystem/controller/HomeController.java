package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.Status;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private WorkOrderService workOrderService;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 获取各状态工单数量
        long pendingCount = workOrderService.getWorkOrdersByStatus(Status.PENDING).size();
        long inProgressCount = workOrderService.getWorkOrdersByStatus(Status.IN_PROGRESS).size();
        long completedCount = workOrderService.getWorkOrdersByStatus(Status.COMPLETED).size();
        long rejectedCount = workOrderService.getWorkOrdersByStatus(Status.REJECTED).size();
        
        // 获取当前用户信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // 获取当前用户的待处理工单
        List<WorkOrder> myPendingWorkOrders = workOrderService.getWorkOrdersAssignedToUserByStatus(username, Status.PENDING);
        
        // 将数据添加到模型中
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        model.addAttribute("myPendingWorkOrders", myPendingWorkOrders);
        
        return "dashboard";
    }
} 