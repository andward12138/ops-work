package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.WorkflowTemplate;
import com.example.opsworkordersystem.repository.WorkflowTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workflow")
public class WorkflowPageController {

    @Autowired
    private WorkflowTemplateRepository workflowTemplateRepository;

    /**
     * 工作流管理主页
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String workflowIndex() {
        return "workflow/index";
    }

    /**
     * 工作流模板管理页面
     */
    @GetMapping("/templates")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String templates(Model model) {
        model.addAttribute("newTemplate", new WorkflowTemplate());
        return "workflow/templates";
    }

    /**
     * 工作流模板详情页面
     */
    @GetMapping("/templates/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public String templateDetails(@PathVariable Integer id, Model model) {
        workflowTemplateRepository.findByIdWithSteps(id)
            .ifPresent(template -> model.addAttribute("template", template));
        return "workflow/template-details";
    }

    /**
     * 我的审批任务页面
     */
    @GetMapping("/my-approvals")
    public String myApprovals() {
        return "workflow/my-approvals";
    }

    /**
     * 审批历史页面
     */
    @GetMapping("/approval-history")
    public String approvalHistory() {
        return "workflow/approval-history";
    }
} 