package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.Department;
import com.example.opsworkordersystem.entity.DepartmentType;
import com.example.opsworkordersystem.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
} 