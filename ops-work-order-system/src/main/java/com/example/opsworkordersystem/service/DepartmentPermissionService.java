package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.entity.Department;
import com.example.opsworkordersystem.entity.DepartmentPermission;
import com.example.opsworkordersystem.entity.PermissionType;
import com.example.opsworkordersystem.repository.DepartmentPermissionRepository;
import com.example.opsworkordersystem.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentPermissionService {

    @Autowired
    private DepartmentPermissionRepository permissionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 授予部门权限
     */
    public DepartmentPermission grantPermission(Integer departmentId, PermissionType permissionType, 
                                              String grantedBy, LocalDateTime expiresAt, String remark) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("部门不存在: " + departmentId));

        // 检查是否已存在该权限
        Optional<DepartmentPermission> existingPermission = 
                permissionRepository.findByDepartmentAndPermissionTypeAndIsActiveTrue(department, permissionType);
        
        if (existingPermission.isPresent()) {
            // 更新现有权限
            DepartmentPermission permission = existingPermission.get();
            permission.setGrantedBy(grantedBy);
            permission.setGrantedAt(LocalDateTime.now());
            permission.setExpiresAt(expiresAt);
            permission.setRemark(remark);
            return permissionRepository.save(permission);
        } else {
            // 创建新权限
            DepartmentPermission newPermission = new DepartmentPermission(department, permissionType, grantedBy);
            newPermission.setExpiresAt(expiresAt);
            newPermission.setRemark(remark);
            return permissionRepository.save(newPermission);
        }
    }

    /**
     * 撤销部门权限
     */
    public void revokePermission(Integer departmentId, PermissionType permissionType) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("部门不存在: " + departmentId));

        Optional<DepartmentPermission> permission = 
                permissionRepository.findByDepartmentAndPermissionTypeAndIsActiveTrue(department, permissionType);
        
        if (permission.isPresent()) {
            DepartmentPermission p = permission.get();
            p.setIsActive(false);
            permissionRepository.save(p);
        }
    }

    /**
     * 批量授予权限
     */
    public void grantPermissions(Integer departmentId, List<PermissionType> permissionTypes, 
                               String grantedBy, LocalDateTime expiresAt, String remark) {
        for (PermissionType permissionType : permissionTypes) {
            grantPermission(departmentId, permissionType, grantedBy, expiresAt, remark);
        }
    }

    /**
     * 批量撤销权限
     */
    public void revokePermissions(Integer departmentId, List<PermissionType> permissionTypes) {
        for (PermissionType permissionType : permissionTypes) {
            revokePermission(departmentId, permissionType);
        }
    }

    /**
     * 获取部门的所有权限
     */
    @Transactional(readOnly = true)
    public List<DepartmentPermission> getDepartmentPermissions(Integer departmentId) {
        return permissionRepository.findByDepartmentIdAndIsActiveTrue(departmentId);
    }

    /**
     * 获取部门的有效权限
     */
    @Transactional(readOnly = true)
    public List<DepartmentPermission> getValidPermissions(Integer departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("部门不存在: " + departmentId));
        
        return permissionRepository.findValidPermissions(department, LocalDateTime.now());
    }

    /**
     * 检查部门是否拥有特定权限
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(Integer departmentId, PermissionType permissionType) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("部门不存在: " + departmentId));
        
        return permissionRepository.hasPermission(department, permissionType, LocalDateTime.now());
    }

    /**
     * 检查部门是否拥有任一权限
     */
    @Transactional(readOnly = true)
    public boolean hasAnyPermission(Integer departmentId, List<PermissionType> permissionTypes) {
        for (PermissionType permissionType : permissionTypes) {
            if (hasPermission(departmentId, permissionType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查部门是否拥有所有权限
     */
    @Transactional(readOnly = true)
    public boolean hasAllPermissions(Integer departmentId, List<PermissionType> permissionTypes) {
        for (PermissionType permissionType : permissionTypes) {
            if (!hasPermission(departmentId, permissionType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取拥有特定权限的所有部门
     */
    @Transactional(readOnly = true)
    public List<DepartmentPermission> getDepartmentsByPermission(PermissionType permissionType) {
        return permissionRepository.findByPermissionTypeAndIsActiveTrue(permissionType);
    }

    /**
     * 获取即将过期的权限
     */
    @Transactional(readOnly = true)
    public List<DepartmentPermission> getExpiringPermissions(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        return permissionRepository.findExpiringPermissions(now, futureDate);
    }

    /**
     * 更新权限过期时间
     */
    public DepartmentPermission updatePermissionExpiry(Integer departmentId, PermissionType permissionType, 
                                                     LocalDateTime newExpiryDate) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("部门不存在: " + departmentId));

        DepartmentPermission permission = permissionRepository
                .findByDepartmentAndPermissionTypeAndIsActiveTrue(department, permissionType)
                .orElseThrow(() -> new RuntimeException("权限不存在"));
        
        permission.setExpiresAt(newExpiryDate);
        return permissionRepository.save(permission);
    }

    /**
     * 续期权限
     */
    public DepartmentPermission renewPermission(Integer departmentId, PermissionType permissionType, int days) {
        LocalDateTime newExpiryDate = LocalDateTime.now().plusDays(days);
        return updatePermissionExpiry(departmentId, permissionType, newExpiryDate);
    }

    /**
     * 复制权限配置
     */
    public void copyPermissions(Integer sourceDepartmentId, Integer targetDepartmentId, String grantedBy) {
        List<DepartmentPermission> sourcePermissions = getValidPermissions(sourceDepartmentId);
        
        for (DepartmentPermission sourcePermission : sourcePermissions) {
            grantPermission(targetDepartmentId, sourcePermission.getPermissionType(), 
                          grantedBy, sourcePermission.getExpiresAt(), 
                          "从部门 " + sourceDepartmentId + " 复制");
        }
    }

    /**
     * 清除部门所有权限
     */
    public void clearAllPermissions(Integer departmentId) {
        List<DepartmentPermission> permissions = getDepartmentPermissions(departmentId);
        for (DepartmentPermission permission : permissions) {
            permission.setIsActive(false);
            permissionRepository.save(permission);
        }
    }
} 