package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.dto.UserDto;
import com.example.opsworkordersystem.entity.Role;
import com.example.opsworkordersystem.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private IUserService userService;

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }

    /**
     * 处理注册请求
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult bindingResult, Model model) {
        // 验证用户输入
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // 检查用户名是否已存在
        if (userService.isUsernameExists(userDto.getUsername())) {
            model.addAttribute("usernameError", "用户名已存在");
            return "register";
        }

        // 检查邮箱是否已存在
        if (userDto.getEmail() != null && userService.isEmailExists(userDto.getEmail())) {
            model.addAttribute("emailError", "邮箱已存在");
            return "register";
        }

        // 默认注册为普通用户
        userDto.setRole(Role.USER);
        
        try {
            userService.createUser(userDto);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", "注册失败：" + e.getMessage());
            return "register";
        }
    }

    /**
     * 请求密码重置页面
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    /**
     * 处理密码重置请求
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        boolean result = userService.requestPasswordReset(email);
        if (result) {
            model.addAttribute("success", "密码重置邮件已发送到您的邮箱");
        } else {
            model.addAttribute("error", "密码重置请求失败，请稍后重试");
        }
        return "forgot-password";
    }

    /**
     * 显示密码重置表单
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    /**
     * 处理密码重置
     */
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String token,
            @RequestParam String password,
            Model model) {
        boolean result = userService.resetPassword(token, password);
        if (result) {
            return "redirect:/login?resetSuccess";
        } else {
            model.addAttribute("error", "密码重置失败，请稍后重试");
            model.addAttribute("token", token);
            return "reset-password";
        }
    }

    // 已移至HomeController
    /*
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    */
} 