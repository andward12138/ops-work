package com.example.opsworkordersystem.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // 设置默认缓存配置
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats());
        
        // 为不同的缓存设置不同的过期时间
        cacheManager.setCacheNames(java.util.Arrays.asList(
                "users",           // 用户缓存 - 30分钟
                "departments",     // 部门缓存 - 1小时
                "workOrders",      // 工单缓存 - 5分钟
                "statistics",      // 统计数据缓存 - 10分钟
                "transferRecords", // 转派记录缓存 - 15分钟
                "workflowTemplates"// 工作流模板缓存 - 1小时
        ));
        
        return cacheManager;
    }

    @Bean("usersCaffeine")
    public Caffeine<Object, Object> usersCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats();
    }

    @Bean("departmentsCaffeine")
    public Caffeine<Object, Object> departmentsCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats();
    }

    @Bean("workOrdersCaffeine")
    public Caffeine<Object, Object> workOrdersCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats();
    }

    @Bean("statisticsCaffeine")
    public Caffeine<Object, Object> statisticsCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats();
    }

    @Bean("transferRecordsCaffeine")
    public Caffeine<Object, Object> transferRecordsCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .recordStats();
    }

    @Bean("workflowTemplatesCaffeine")
    public Caffeine<Object, Object> workflowTemplatesCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(50)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats();
    }
} 