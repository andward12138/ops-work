package com.example.opsworkordersystem.repository;

import com.example.opsworkordersystem.entity.Role;
import com.example.opsworkordersystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // 根据用户名查找用户
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    Page<User> findAll(Pageable pageable);
    
    // 根据角色查找用户
    List<User> findByRole(Role role);
    
    // 根据角色和部门查找用户
    List<User> findByRoleAndDepartmentId(Role role, Integer departmentId);
}
