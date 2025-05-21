package com.example.opsworkordersystem.config;

import com.example.opsworkordersystem.entity.Role;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 创建管理员账号（如果不存在）
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setEmail("admin@example.com");
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("管理员账号创建成功：admin/admin123");
        }

        // 创建测试用户（如果不存在）
        if (!userRepository.existsByUsername("operator")) {
            User operator = new User();
            operator.setUsername("operator");
            operator.setPassword(passwordEncoder.encode("operator123"));
            operator.setRole(Role.OPERATOR);
            operator.setEmail("operator@example.com");
            operator.setCreatedAt(LocalDateTime.now());
            operator.setUpdatedAt(LocalDateTime.now());
            userRepository.save(operator);
            System.out.println("操作员账号创建成功：operator/operator123");
        }

        if (!userRepository.existsByUsername("manager")) {
            User manager = new User();
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setRole(Role.MANAGER);
            manager.setEmail("manager@example.com");
            manager.setCreatedAt(LocalDateTime.now());
            manager.setUpdatedAt(LocalDateTime.now());
            userRepository.save(manager);
            System.out.println("经理账号创建成功：manager/manager123");
        }
    }
} 