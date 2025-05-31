package com.example.opsworkordersystem.config;

import com.example.opsworkordersystem.entity.Department;
import com.example.opsworkordersystem.entity.DepartmentType;
import com.example.opsworkordersystem.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Order(1)  // 确保在其他初始化器之前运行
public class DepartmentDataInitializer implements CommandLineRunner {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有部门数据
        if (departmentRepository.count() == 0) {
            System.out.println("初始化部门数据...");
            
            // 创建总部
            Department headquarters = createDepartmentIfNotExists(
                "总部", "公司总部", DepartmentType.PROVINCIAL, null
            );
            
            // 创建技术部
            Department techDept = createDepartmentIfNotExists(
                "技术部", "技术开发部门", DepartmentType.SUPPORT, headquarters
            );
            
            // 创建运维部
            Department opsDept = createDepartmentIfNotExists(
                "运维部", "运维管理部门", DepartmentType.OPERATIONAL, headquarters
            );
            
            // 创建网络组
            createDepartmentIfNotExists(
                "网络组", "网络运维小组", DepartmentType.OPERATIONAL, opsDept
            );
            
            // 创建系统组
            createDepartmentIfNotExists(
                "系统组", "系统运维小组", DepartmentType.OPERATIONAL, opsDept
            );
            
            System.out.println("部门数据初始化完成");
        }
    }
    
    private Department createDepartmentIfNotExists(String name, String description, 
                                                  DepartmentType type, Department parent) {
        Optional<Department> existing = departmentRepository.findByName(name);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        Department department = new Department();
        department.setName(name);
        department.setDescription(description);
        department.setType(type);
        department.setParent(parent);
        department.setIsActive(true);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        
        return departmentRepository.save(department);
    }
} 