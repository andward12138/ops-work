package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    
    // 根据名称查找部门
    Optional<Department> findByName(String name);
    
    // 查找所有激活的部门
    List<Department> findByIsActive(Boolean isActive);
    
    // 根据父部门查找子部门
    List<Department> findByParentId(Integer parentId);
    
    // 根据类型查找部门
    List<Department> findByType(String type);
} 