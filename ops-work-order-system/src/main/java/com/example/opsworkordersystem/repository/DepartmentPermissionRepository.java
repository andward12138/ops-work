package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.DepartmentPermission;
import com.example.opsworkordersystem.entity.Department;
import com.example.opsworkordersystem.entity.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentPermissionRepository extends JpaRepository<DepartmentPermission, Integer> {
    
    // 根据部门查找权限
    List<DepartmentPermission> findByDepartmentAndIsActiveTrue(Department department);
    
    // 根据部门ID查找权限
    List<DepartmentPermission> findByDepartmentIdAndIsActiveTrue(Integer departmentId);
    
    // 查找特定部门的特定权限
    Optional<DepartmentPermission> findByDepartmentAndPermissionTypeAndIsActiveTrue(
            Department department, PermissionType permissionType);
    
    // 根据权限类型查找所有拥有该权限的部门
    List<DepartmentPermission> findByPermissionTypeAndIsActiveTrue(PermissionType permissionType);
    
    // 查找有效权限（未过期且激活）
    @Query("SELECT dp FROM DepartmentPermission dp WHERE dp.department = :department " +
           "AND dp.isActive = true AND (dp.expiresAt IS NULL OR dp.expiresAt > :now)")
    List<DepartmentPermission> findValidPermissions(@Param("department") Department department, 
                                                   @Param("now") LocalDateTime now);
    
    // 查找即将过期的权限
    @Query("SELECT dp FROM DepartmentPermission dp WHERE dp.expiresAt BETWEEN :start AND :end " +
           "AND dp.isActive = true")
    List<DepartmentPermission> findExpiringPermissions(@Param("start") LocalDateTime start, 
                                                      @Param("end") LocalDateTime end);
    
    // 检查部门是否拥有特定权限
    @Query("SELECT COUNT(dp) > 0 FROM DepartmentPermission dp WHERE dp.department = :department " +
           "AND dp.permissionType = :permissionType AND dp.isActive = true " +
           "AND (dp.expiresAt IS NULL OR dp.expiresAt > :now)")
    boolean hasPermission(@Param("department") Department department, 
                         @Param("permissionType") PermissionType permissionType,
                         @Param("now") LocalDateTime now);
    
    // 根据授权人查找权限
    List<DepartmentPermission> findByGrantedByAndIsActiveTrue(String grantedBy);
    
    // 查找部门权限数量
    @Query("SELECT COUNT(dp) FROM DepartmentPermission dp WHERE dp.department = :department AND dp.isActive = true")
    long countByDepartmentAndIsActiveTrue(@Param("department") Department department);
} 