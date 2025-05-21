package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.dto.UserDto;
import com.example.opsworkordersystem.entity.Role;
import com.example.opsworkordersystem.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private IUserService userService;

    /**
     * 显示用户管理页面
     */
    @GetMapping("/users")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<UserDto> users = userService.getAllUsers(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username")));
        
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("roles", Role.values());
        model.addAttribute("newUser", new UserDto());
        
        return "admin/users";
    }

    /**
     * 创建新用户（管理员功能）
     */
    @PostMapping("/users")
    public String createUser(@ModelAttribute("newUser") UserDto userDto,
                            RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(userDto);
            redirectAttributes.addFlashAttribute("success", "用户创建成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "创建用户失败：" + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * 删除用户
     */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "用户删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除用户失败：" + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/users/{id}")
    public String getUserDetails(@PathVariable Integer id, Model model) {
        try {
            UserDto user = userService.getUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            return "admin/user-details";
        } catch (Exception e) {
            return "redirect:/admin/users";
        }
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Integer id,
                            @ModelAttribute("user") UserDto userDto,
                            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, userDto);
            redirectAttributes.addFlashAttribute("success", "用户信息更新成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "更新用户失败：" + e.getMessage());
        }
        return "redirect:/admin/users/" + id;
    }
} 