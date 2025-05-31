package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.DepartmentContact;
import com.example.opsworkordersystem.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentContactRepository extends JpaRepository<DepartmentContact, Integer> {
    
    // 根据部门查找联系人
    List<DepartmentContact> findByDepartmentAndIsActiveTrue(Department department);
    
    // 根据部门ID查找联系人
    List<DepartmentContact> findByDepartmentIdAndIsActiveTrue(Integer departmentId);
    
    // 查找主要联系人
    Optional<DepartmentContact> findByDepartmentAndIsPrimaryTrueAndIsActiveTrue(Department department);
    
    // 查找紧急联系人
    Optional<DepartmentContact> findByDepartmentAndIsEmergencyTrueAndIsActiveTrue(Department department);
    
    // 根据电话号码查找联系人
    List<DepartmentContact> findByPhoneAndIsActiveTrue(String phone);
    
    // 根据邮箱查找联系人
    List<DepartmentContact> findByEmailAndIsActiveTrue(String email);
    
    // 根据姓名模糊查询
    @Query("SELECT dc FROM DepartmentContact dc WHERE dc.name LIKE %:name% AND dc.isActive = true")
    List<DepartmentContact> findByNameContaining(@Param("name") String name);
    
    // 查找部门的有效联系人数量
    @Query("SELECT COUNT(dc) FROM DepartmentContact dc WHERE dc.department = :department AND dc.isActive = true")
    long countByDepartmentAndIsActiveTrue(@Param("department") Department department);
} 