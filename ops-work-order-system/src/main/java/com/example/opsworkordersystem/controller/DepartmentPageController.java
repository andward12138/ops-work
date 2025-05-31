package com.example.opsworkordersystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/departments")
public class DepartmentPageController {

    /**
     * 部门管理主页面
     */
    @GetMapping("")
    public String index() {
        return "departments/index";
    }

    /**
     * 部门联系人管理页面
     */
    @GetMapping("/{departmentId}/contacts")
    public String contacts(@PathVariable Integer departmentId) {
        return "departments/contacts";
    }

    /**
     * 部门权限管理页面
     */
    @GetMapping("/{departmentId}/permissions")
    public String permissions(@PathVariable Integer departmentId) {
        return "departments/permissions";
    }
} 