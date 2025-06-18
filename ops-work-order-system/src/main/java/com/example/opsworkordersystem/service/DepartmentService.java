package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.entity.Department;
import com.example.opsworkordersystem.entity.DepartmentType;
import com.example.opsworkordersystem.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 获取所有部门
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "departments", key = "'all_active'")
    public List<Department> getAllDepartments() {
        return departmentRepository.findByIsActive(true);
    }

    /**
     * 根据ID获取部门
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "departments", key = "'id_' + #id")
    public Department getDepartmentById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在: " + id));
    }

    /**
     * 创建新部门
     */
    public Department createDepartment(Department department) {
        // 设置创建时间和更新时间
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        department.setIsActive(true);

        // 如果有父部门，验证父部门存在
        if (department.getParent() != null && department.getParent().getId() != null) {
            Department parent = getDepartmentById(department.getParent().getId());
            department.setParent(parent);
            
            // 设置层级
            if (parent.getLevel() != null) {
                department.setLevel(parent.getLevel() + 1);
            } else {
                department.setLevel(2); // 默认为二级
            }
        } else {
            department.setParent(null);
            department.setLevel(1); // 顶级部门
        }

        return departmentRepository.save(department);
    }

    /**
     * 更新部门信息
     */
    public Department updateDepartment(Integer id, Department departmentUpdate) {
        Department existingDepartment = getDepartmentById(id);

        // 更新字段
        existingDepartment.setName(departmentUpdate.getName());
        existingDepartment.setCode(departmentUpdate.getCode());
        existingDepartment.setType(departmentUpdate.getType());
        existingDepartment.setDescription(departmentUpdate.getDescription());
        existingDepartment.setContactPerson(departmentUpdate.getContactPerson());
        existingDepartment.setContactPhone(departmentUpdate.getContactPhone());
        existingDepartment.setUpdatedAt(LocalDateTime.now());

        // 处理父部门变更
        if (departmentUpdate.getParent() != null && departmentUpdate.getParent().getId() != null) {
            Department newParent = getDepartmentById(departmentUpdate.getParent().getId());
            
            // 防止循环引用
            if (isDescendantOf(newParent, existingDepartment)) {
                throw new RuntimeException("不能将部门设置为其子部门的下级");
            }
            
            existingDepartment.setParent(newParent);
            if (newParent.getLevel() != null) {
                existingDepartment.setLevel(newParent.getLevel() + 1);
            }
        } else {
            existingDepartment.setParent(null);
            existingDepartment.setLevel(1);
        }

        return departmentRepository.save(existingDepartment);
    }

    /**
     * 删除部门（软删除）
     */
    public void deleteDepartment(Integer id) {
        Department department = getDepartmentById(id);
        
        // 检查是否有子部门
        List<Department> children = departmentRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("不能删除有子部门的部门，请先删除或转移子部门");
        }

        department.setIsActive(false);
        department.setUpdatedAt(LocalDateTime.now());
        departmentRepository.save(department);
    }

    /**
     * 根据类型获取部门
     */
    @Transactional(readOnly = true)
    public List<Department> getDepartmentsByType(DepartmentType type) {
        return departmentRepository.findByType(type.name());
    }

    /**
     * 获取子部门
     */
    @Transactional(readOnly = true)
    public List<Department> getChildDepartments(Integer parentId) {
        return departmentRepository.findByParentId(parentId);
    }

    /**
     * 根据名称搜索部门
     */
    @Transactional(readOnly = true)
    public List<Department> searchDepartmentsByName(String name) {
        return departmentRepository.findAll().stream()
                .filter(dept -> dept.getIsActive() && 
                              dept.getName() != null && 
                              dept.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    /**
     * 根据名称获取部门
     */
    @Transactional(readOnly = true)
    public Optional<Department> getDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }

    /**
     * 获取顶级部门（没有父部门的部门）
     */
    @Transactional(readOnly = true)
    public List<Department> getRootDepartments() {
        return departmentRepository.findAll().stream()
                .filter(dept -> dept.getIsActive() && dept.getParent() == null)
                .toList();
    }

    /**
     * 获取部门树结构
     */
    @Transactional(readOnly = true)
    public List<Department> getDepartmentTree() {
        List<Department> rootDepartments = getRootDepartments();
        // 这里可以添加递归加载子部门的逻辑
        return rootDepartments;
    }

    /**
     * 检查部门A是否是部门B的后代
     */
    private boolean isDescendantOf(Department a, Department b) {
        if (a == null || b == null) {
            return false;
        }
        
        Department current = a.getParent();
        while (current != null) {
            if (current.getId().equals(b.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    /**
     * 移动部门到新的父部门
     */
    public Department moveDepartment(Integer departmentId, Integer newParentId) {
        Department department = getDepartmentById(departmentId);
        
        if (newParentId != null) {
            Department newParent = getDepartmentById(newParentId);
            
            // 防止循环引用
            if (isDescendantOf(newParent, department)) {
                throw new RuntimeException("不能将部门移动到其子部门下");
            }
            
            department.setParent(newParent);
            department.setLevel(newParent.getLevel() + 1);
        } else {
            department.setParent(null);
            department.setLevel(1);
        }
        
        department.setUpdatedAt(LocalDateTime.now());
        return departmentRepository.save(department);
    }

    /**
     * 启用部门
     */
    public Department enableDepartment(Integer id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在: " + id));
        
        department.setIsActive(true);
        department.setUpdatedAt(LocalDateTime.now());
        return departmentRepository.save(department);
    }

    /**
     * 禁用部门
     */
    public Department disableDepartment(Integer id) {
        Department department = getDepartmentById(id);
        department.setIsActive(false);
        department.setUpdatedAt(LocalDateTime.now());
        return departmentRepository.save(department);
    }
} 