package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.DeviceToken;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.service.PushNotificationService;
import com.example.opsworkordersystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/push")
public class PushNotificationController {

    @Autowired
    private PushNotificationService pushNotificationService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * 注册设备FCM令牌
     */
    @PostMapping("/register-token")
    public ResponseEntity<Map<String, String>> registerToken(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        
        try {
            String fcmToken = request.get("fcmToken");
            String deviceTypeStr = request.getOrDefault("deviceType", "ANDROID");
            String deviceInfo = request.getOrDefault("deviceInfo", "Unknown Device");
            
            // 获取当前用户
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            DeviceToken.DeviceType deviceType;
            try {
                deviceType = DeviceToken.DeviceType.valueOf(deviceTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                deviceType = DeviceToken.DeviceType.ANDROID;
            }

            pushNotificationService.registerDeviceToken(user.getId(), fcmToken, deviceType, deviceInfo);
            
            response.put("status", "success");
            response.put("message", "设备令牌注册成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("注册设备令牌失败", e);
            response.put("status", "error");
            response.put("message", "注册失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 发送测试推送通知
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestNotification(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        
        try {
            String title = request.getOrDefault("title", "测试通知");
            String body = request.getOrDefault("body", "这是一条测试推送通知");
            String type = request.getOrDefault("type", "test");
            
            // 获取当前用户
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 模拟发送测试通知
            log.info("发送测试通知给用户 {}: 标题={}, 内容={}", username, title, body);
            
            response.put("status", "success");
            response.put("message", "测试通知发送成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("发送测试通知失败", e);
            response.put("status", "error");
            response.put("message", "发送失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 发送部门通知（管理员权限）
     */
    @PostMapping("/department/{departmentId}")
    public ResponseEntity<Map<String, String>> sendDepartmentNotification(
            @PathVariable Integer departmentId,
            @RequestBody Map<String, String> request) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // 检查权限（简化版本，实际应用中应该使用@PreAuthorize注解）
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // TODO: 添加权限检查
            
            String title = request.get("title");
            String body = request.get("body");
            String type = request.getOrDefault("type", "department");
            
            if (title == null || body == null) {
                response.put("status", "error");
                response.put("message", "标题和内容不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            pushNotificationService.sendDepartmentNotification(departmentId, title, body, type);
            
            response.put("status", "success");
            response.put("message", "部门通知发送成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("发送部门通知失败", e);
            response.put("status", "error");
            response.put("message", "发送失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 发送广播通知（超级管理员权限）
     */
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> sendBroadcastNotification(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // 检查权限（简化版本）
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // TODO: 添加超级管理员权限检查
            
            String title = request.get("title");
            String body = request.get("body");
            String type = request.getOrDefault("type", "broadcast");
            
            if (title == null || body == null) {
                response.put("status", "error");
                response.put("message", "标题和内容不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            pushNotificationService.sendBroadcastNotification(title, body, type);
            
            response.put("status", "success");
            response.put("message", "广播通知发送成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("发送广播通知失败", e);
            response.put("status", "error");
            response.put("message", "发送失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 清理过期的设备令牌（管理员接口）
     */
    @PostMapping("/cleanup-tokens")
    public ResponseEntity<Map<String, String>> cleanupExpiredTokens() {
        Map<String, String> response = new HashMap<>();
        
        try {
            // 检查权限
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // TODO: 添加管理员权限检查
            
            pushNotificationService.cleanupExpiredTokens();
            
            response.put("status", "success");
            response.put("message", "过期令牌清理完成");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("清理过期令牌失败", e);
            response.put("status", "error");
            response.put("message", "清理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取推送配置信息
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getPushConfig() {
        Map<String, Object> config = new HashMap<>();
        
        try {
            // 获取当前用户
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            config.put("pushEnabled", true);
            config.put("supportedDeviceTypes", new String[]{"ANDROID", "IOS", "WEB"});
            config.put("maxTokensPerUser", 5);
            config.put("tokenExpiryDays", 30);
            config.put("currentUser", username);
            
            return ResponseEntity.ok(config);
            
        } catch (Exception e) {
            log.error("获取推送配置失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
} 