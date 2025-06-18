package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.DeviceToken;
import com.example.opsworkordersystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Integer> {
    
    /**
     * 根据用户查找所有活跃的设备令牌
     */
    List<DeviceToken> findByUserAndIsActiveTrue(User user);
    
    /**
     * 根据FCM令牌查找设备
     */
    Optional<DeviceToken> findByFcmToken(String fcmToken);
    
    /**
     * 根据用户ID查找所有活跃的设备令牌
     */
    @Query("SELECT dt.fcmToken FROM DeviceToken dt WHERE dt.user.id = :userId AND dt.isActive = true")
    List<String> findActiveTokensByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID和设备类型查找设备令牌
     */
    List<DeviceToken> findByUserIdAndDeviceTypeAndIsActiveTrue(Integer userId, DeviceToken.DeviceType deviceType);
    
    /**
     * 批量更新设备令牌的最后使用时间
     */
    @Modifying
    @Query("UPDATE DeviceToken dt SET dt.lastUsedAt = :lastUsedAt WHERE dt.fcmToken IN :tokens")
    void updateLastUsedTime(@Param("tokens") List<String> tokens, @Param("lastUsedAt") LocalDateTime lastUsedAt);
    
    /**
     * 停用过期的设备令牌（超过指定天数未使用）
     */
    @Modifying
    @Query("UPDATE DeviceToken dt SET dt.isActive = false WHERE dt.lastUsedAt < :cutoffTime AND dt.isActive = true")
    int deactivateExpiredTokens(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * 删除指定用户的旧设备令牌（保留最新的N个）
     */
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.user.id = :userId AND dt.isActive = true ORDER BY dt.createdAt DESC")
    List<DeviceToken> findUserTokensOrderByCreatedDesc(@Param("userId") Integer userId);
    
    /**
     * 获取所有活跃的Android设备令牌
     */
    @Query("SELECT dt.fcmToken FROM DeviceToken dt WHERE dt.deviceType = 'ANDROID' AND dt.isActive = true")
    List<String> findAllActiveAndroidTokens();
    
    /**
     * 获取指定部门所有用户的活跃设备令牌
     */
    @Query("SELECT dt.fcmToken FROM DeviceToken dt JOIN dt.user u WHERE u.department.id = :departmentId AND dt.isActive = true")
    List<String> findActiveTokensByDepartmentId(@Param("departmentId") Integer departmentId);
} 