package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.dto.UserDto;
import com.example.opsworkordersystem.entity.Role;
import com.example.opsworkordersystem.entity.User;
import com.example.opsworkordersystem.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/legacy/users")
@PreAuthorize("hasRole('ADMIN')")
public class LegacyUserController {

    @Autowired
    private IUserService userService;

    // 根据用户名获取用户
    @GetMapping("/{username}")
    public UserDto getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    // 根据角色获取用户列表
    @GetMapping("/role/{role}")
    public List<UserDto> getUsersByRole(@PathVariable String role) {
        // 注意：新服务不再支持按角色查询用户，建议使用API用户控制器
        throw new UnsupportedOperationException("此方法不再支持，请使用/api/users接口");
    }

    // 创建用户
    @PostMapping
    public UserDto createUser(@RequestBody UserDto user) {
        return userService.createUser(user);
    }
}
