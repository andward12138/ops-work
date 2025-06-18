package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.Department;
import com.example.opsworkordersystem.entity.DepartmentType;
import com.example.opsworkordersystem.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 获取所有部门
     */
    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据ID获取部门
     */
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartment(@PathVariable Integer id) {
        try {
            Department department = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(department);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建新部门
     */
    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        try {
            Department createdDepartment = departmentService.createDepartment(department);
            return ResponseEntity.ok(createdDepartment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新部门信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Integer id, @RequestBody Department department) {
        try {
            Department updatedDepartment = departmentService.updateDepartment(id, department);
            return ResponseEntity.ok(updatedDepartment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Integer id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据类型获取部门
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Department>> getDepartmentsByType(@PathVariable DepartmentType type) {
        try {
            List<Department> departments = departmentService.getDepartmentsByType(type);
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取子部门
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<List<Department>> getChildDepartments(@PathVariable Integer id) {
        try {
            List<Department> children = departmentService.getChildDepartments(id);
            return ResponseEntity.ok(children);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据名称搜索部门
     */
    @GetMapping("/search")
    public ResponseEntity<List<Department>> searchDepartments(@RequestParam String name) {
        try {
            List<Department> departments = departmentService.searchDepartmentsByName(name);
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 调试：获取所有部门（包括非活跃的）
     */
    @GetMapping("/debug/all")
    public ResponseEntity<List<Department>> getAllDepartmentsDebug() {
        try {
            List<Department> departments = departmentService.getAllDepartmentsIncludingInactive();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 调试：获取部门统计信息
     */
    @GetMapping("/debug/stats")
    public ResponseEntity<Map<String, Long>> getDepartmentStats() {
        try {
            Map<String, Long> stats = departmentService.getDepartmentStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 修复：清理部门缓存
     */
    @PostMapping("/fix/clear-cache")
    public ResponseEntity<Map<String, String>> clearDepartmentCache() {
        try {
            departmentService.clearDepartmentCache();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "部门缓存已清理");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "缓存清理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 修复：激活所有部门
     */
    @PostMapping("/fix/activate-all")
    public ResponseEntity<Map<String, Object>> activateAllDepartments() {
        try {
            Map<String, Long> beforeStats = departmentService.getDepartmentStatistics();
            departmentService.activateAllDepartments();
            Map<String, Long> afterStats = departmentService.getDepartmentStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "所有部门已激活");
            response.put("beforeStats", beforeStats);
            response.put("afterStats", afterStats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "激活失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 