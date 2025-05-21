package com.example.opsworkordersystem.controller.api;

import com.example.opsworkordersystem.dto.UserDto;
import com.example.opsworkordersystem.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users/api")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 创建用户
     * @param userDto 用户信息
     * @return 创建的用户
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            UserDto createdUser = userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.username == #username")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            UserDto user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (NoSuchElementException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.username == #username")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            UserDto user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (NoSuchElementException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDto 用户信息
     * @return 更新后的用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "用户删除成功");
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 获取用户列表（分页）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 用户列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserDto> users = userService.getAllUsers(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username")));
        return ResponseEntity.ok(users);
    }

    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", !userService.isUsernameExists(username));
        return ResponseEntity.ok(response);
    }

    /**
     * 检查邮箱是否可用
     * @param email 邮箱
     * @return 是否可用
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", !userService.isEmailExists(email));
        return ResponseEntity.ok(response);
    }

    /**
     * 请求密码重置
     * @param email 邮箱
     * @return 请求结果
     */
    @PostMapping("/reset-password-request")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        boolean result = userService.requestPasswordReset(email);
        Map<String, String> response = new HashMap<>();
        if (result) {
            response.put("message", "密码重置邮件已发送");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "发送密码重置邮件失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 重置密码
     * @param token 重置令牌
     * @param newPassword 新密码
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        boolean result = userService.resetPassword(token, newPassword);
        Map<String, String> response = new HashMap<>();
        if (result) {
            response.put("message", "密码重置成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "密码重置失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据角色获取用户列表
     * @param role 角色名称
     * @return 用户列表
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            com.example.opsworkordersystem.entity.Role roleEnum = 
                com.example.opsworkordersystem.entity.Role.valueOf(role.toUpperCase());
            return ResponseEntity.ok(userService.getUsersByRole(roleEnum));
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid role: " + role);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
