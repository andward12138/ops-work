package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.entity.DeviceToken;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.entity.WorkOrder;
import com.example.opsworkordersystem.repository.DeviceTokenRepository;
import com.example.opsworkordersystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class PushNotificationService {

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * 注册设备令牌
     */
    public void registerDeviceToken(Integer userId, String fcmToken, DeviceToken.DeviceType deviceType, String deviceInfo) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

            // 检查令牌是否已存在
            Optional<DeviceToken> existingToken = deviceTokenRepository.findByFcmToken(fcmToken);
            if (existingToken.isPresent()) {
                DeviceToken token = existingToken.get();
                token.setLastUsedAt(LocalDateTime.now());
                token.setIsActive(true);
                deviceTokenRepository.save(token);
                return;
            }

            // 限制每个用户的设备令牌数量（最多5个）
            List<DeviceToken> userTokens = deviceTokenRepository.findUserTokensOrderByCreatedDesc(userId);
            if (userTokens.size() >= 5) {
                // 删除最旧的令牌
                for (int i = 4; i < userTokens.size(); i++) {
                    deviceTokenRepository.delete(userTokens.get(i));
                }
            }

            // 创建新的设备令牌
            DeviceToken deviceToken = DeviceToken.builder()
                    .user(user)
                    .fcmToken(fcmToken)
                    .deviceType(deviceType)
                    .deviceInfo(deviceInfo)
                    .lastUsedAt(LocalDateTime.now())
                    .build();

            deviceTokenRepository.save(deviceToken);
            log.info("注册设备令牌成功: 用户ID={}, 设备类型={}", userId, deviceType);

        } catch (Exception e) {
            log.error("注册设备令牌失败: 用户ID={}, 错误={}", userId, e.getMessage(), e);
            throw new RuntimeException("注册设备令牌失败", e);
        }
    }

    /**
     * 发送工单通知给指定用户
     */
    public void sendWorkOrderNotification(Integer userId, WorkOrder workOrder, String notificationType) {
        try {
            List<String> tokens = deviceTokenRepository.findActiveTokensByUserId(userId);
            if (tokens.isEmpty()) {
                log.info("用户 {} 没有活跃的设备令牌", userId);
                return;
            }

            String title = getNotificationTitle(notificationType, workOrder);
            String body = getNotificationBody(notificationType, workOrder);
            
            Map<String, String> data = new HashMap<>();
            data.put("workOrderId", workOrder.getId().toString());
            data.put("type", notificationType);
            data.put("priority", workOrder.getPriority().toString());
            data.put("action", "view_work_order");

            // TODO: 实际发送FCM推送通知
            log.info("模拟发送推送通知: 用户ID={}, 标题={}, 内容={}", userId, title, body);
            
            // 更新令牌使用时间
            deviceTokenRepository.updateLastUsedTime(tokens, LocalDateTime.now());
            
            log.info("工单通知发送成功: 用户ID={}, 工单ID={}, 类型={}", userId, workOrder.getId(), notificationType);

        } catch (Exception e) {
            log.error("发送工单通知失败: 用户ID={}, 工单ID={}, 错误={}", userId, workOrder.getId(), e.getMessage(), e);
        }
    }

    /**
     * 发送通知给整个部门
     */
    public void sendDepartmentNotification(Integer departmentId, String title, String body, String notificationType) {
        try {
            List<String> tokens = deviceTokenRepository.findActiveTokensByDepartmentId(departmentId);
            if (tokens.isEmpty()) {
                log.info("部门 {} 没有活跃的设备令牌", departmentId);
                return;
            }

            Map<String, String> data = new HashMap<>();
            data.put("type", notificationType);
            data.put("departmentId", departmentId.toString());
            data.put("action", "view_department_notification");

            // TODO: 实际发送FCM推送通知
            log.info("模拟发送部门通知: 部门ID={}, 标题={}, 内容={}, 设备数量={}", departmentId, title, body, tokens.size());

        } catch (Exception e) {
            log.error("发送部门通知失败: 部门ID={}, 错误={}", departmentId, e.getMessage(), e);
        }
    }

    /**
     * 发送广播通知
     */
    public void sendBroadcastNotification(String title, String body, String notificationType) {
        try {
            List<String> tokens = deviceTokenRepository.findAllActiveAndroidTokens();
            if (tokens.isEmpty()) {
                log.info("没有活跃的Android设备令牌");
                return;
            }

            Map<String, String> data = new HashMap<>();
            data.put("type", notificationType);
            data.put("action", "view_broadcast");

            // TODO: 实际发送FCM推送通知
            log.info("模拟发送广播通知: 标题={}, 内容={}, 设备数量={}", title, body, tokens.size());

        } catch (Exception e) {
            log.error("发送广播通知失败: 错误={}", e.getMessage(), e);
        }
    }

    /**
     * 清理过期的设备令牌
     */
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(30); // 30天未使用
            int count = deviceTokenRepository.deactivateExpiredTokens(cutoffTime);
            log.info("清理过期设备令牌: 数量={}", count);
        } catch (Exception e) {
            log.error("清理过期设备令牌失败: 错误={}", e.getMessage(), e);
        }
    }

    /**
     * 获取通知标题
     */
    private String getNotificationTitle(String notificationType, WorkOrder workOrder) {
        switch (notificationType) {
            case "assignment":
                return "新工单分配";
            case "status_change":
                return "工单状态更新";
            case "urgent":
                return "紧急工单提醒";
            case "approval_request":
                return "审批请求";
            case "completion":
                return "工单完成";
            case "transfer":
                return "工单转派";
            default:
                return "运维工单通知";
        }
    }

    /**
     * 获取通知内容
     */
    private String getNotificationBody(String notificationType, WorkOrder workOrder) {
        switch (notificationType) {
            case "assignment":
                return String.format("您有新的工单需要处理：%s", workOrder.getTitle());
            case "status_change":
                return String.format("工单 \"%s\" 状态已更新为：%s", workOrder.getTitle(), workOrder.getStatus().name());
            case "urgent":
                return String.format("紧急工单：%s，请立即处理！", workOrder.getTitle());
            case "approval_request":
                return String.format("工单 \"%s\" 需要您的审批", workOrder.getTitle());
            case "completion":
                return String.format("工单 \"%s\" 已完成", workOrder.getTitle());
            case "transfer":
                return String.format("工单 \"%s\" 已转派给您", workOrder.getTitle());
            default:
                return String.format("工单 \"%s\" 有新的更新", workOrder.getTitle());
        }
    }
}

/* 
 * 注意：当前版本为简化版本，仅记录设备令牌和模拟推送功能
 * 要启用真实的FCM推送功能，请：
 * 1. 配置Firebase项目和服务账号密钥
 * 2. 取消注释下面的Firebase相关代码
 * 3. 替换上面的模拟发送代码
 */

/*
// Firebase FCM 完整实现（需要配置Firebase后启用）
private void sendToTokens(List<String> tokens, String title, String body, Map<String, String> data) {
    if (tokens.isEmpty()) {
        return;
    }

    try {
        // 分批发送（FCM限制一次最多1000个令牌）
        int batchSize = 1000;
        for (int i = 0; i < tokens.size(); i += batchSize) {
            List<String> batch = tokens.subList(i, Math.min(i + batchSize, tokens.size()));
            sendBatch(batch, title, body, data);
        }
    } catch (Exception e) {
        log.error("发送推送消息失败: 错误={}", e.getMessage(), e);
    }
}

private void sendBatch(List<String> tokens, String title, String body, Map<String, String> data) {
    try {
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data)
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setIcon("ic_notification")
                                .setColor("#2196F3")
                                .setPriority(AndroidNotification.Priority.HIGH)
                                .build())
                        .build())
                .addAllTokens(tokens)
                .build();

        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
        
        // 处理失败的令牌
        if (response.getFailureCount() > 0) {
            List<SendResponse> responses = response.getResponses();
            List<String> failedTokens = new ArrayList<>();
            
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    String errorCode = responses.get(i).getException().getErrorCode();
                    if ("INVALID_REGISTRATION_TOKEN".equals(errorCode) || 
                        "UNREGISTERED".equals(errorCode)) {
                        failedTokens.add(tokens.get(i));
                    }
                }
            }
            
            // 停用无效的令牌
            if (!failedTokens.isEmpty()) {
                deactivateTokens(failedTokens);
            }
        }
        
        log.info("批量推送完成: 成功={}, 失败={}", response.getSuccessCount(), response.getFailureCount());

    } catch (Exception e) {
        log.error("批量发送推送消息失败: 错误={}", e.getMessage(), e);
    }
}
*/ 