package com.example.opsworkordersystem.service;

import com.example.opsworkordersystem.entity.Department;
import com.example.opsworkordersystem.entity.DepartmentContact;
import com.example.opsworkordersystem.repository.DepartmentContactRepository;
import com.example.opsworkordersystem.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentContactService {

    @Autowired
    private DepartmentContactRepository contactRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 创建部门联系人
     */
    public DepartmentContact createContact(Integer departmentId, DepartmentContact contact) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("部门不存在: " + departmentId));
        
        contact.setDepartment(department);
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());
        
        // 如果设置为主要联系人，需要确保该部门只有一个主要联系人
        if (contact.getIsPrimary() != null && contact.getIsPrimary()) {
            clearPrimaryContact(department);
        }
        
        // 如果设置为紧急联系人，需要确保该部门只有一个紧急联系人
        if (contact.getIsEmergency() != null && contact.getIsEmergency()) {
            clearEmergencyContact(department);
        }
        
        return contactRepository.save(contact);
    }

    /**
     * 更新联系人信息
     */
    public DepartmentContact updateContact(Integer contactId, DepartmentContact contactUpdate) {
        DepartmentContact existingContact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("联系人不存在: " + contactId));
        
        // 更新字段
        existingContact.setName(contactUpdate.getName());
        existingContact.setPosition(contactUpdate.getPosition());
        existingContact.setPhone(contactUpdate.getPhone());
        existingContact.setMobile(contactUpdate.getMobile());
        existingContact.setEmail(contactUpdate.getEmail());
        existingContact.setWorkingHours(contactUpdate.getWorkingHours());
        existingContact.setRemark(contactUpdate.getRemark());
        existingContact.setUpdatedAt(LocalDateTime.now());
        
        // 处理主要联系人设置
        if (contactUpdate.getIsPrimary() != null && contactUpdate.getIsPrimary()) {
            clearPrimaryContact(existingContact.getDepartment());
            existingContact.setIsPrimary(true);
        } else if (contactUpdate.getIsPrimary() != null) {
            existingContact.setIsPrimary(contactUpdate.getIsPrimary());
        }
        
        // 处理紧急联系人设置
        if (contactUpdate.getIsEmergency() != null && contactUpdate.getIsEmergency()) {
            clearEmergencyContact(existingContact.getDepartment());
            existingContact.setIsEmergency(true);
        } else if (contactUpdate.getIsEmergency() != null) {
            existingContact.setIsEmergency(contactUpdate.getIsEmergency());
        }
        
        return contactRepository.save(existingContact);
    }

    /**
     * 删除联系人（软删除）
     */
    public void deleteContact(Integer contactId) {
        DepartmentContact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("联系人不存在: " + contactId));
        
        contact.setIsActive(false);
        contact.setUpdatedAt(LocalDateTime.now());
        contactRepository.save(contact);
    }

    /**
     * 获取部门的所有联系人
     */
    @Transactional(readOnly = true)
    public List<DepartmentContact> getDepartmentContacts(Integer departmentId) {
        return contactRepository.findByDepartmentIdAndIsActiveTrue(departmentId);
    }

    /**
     * 获取部门的主要联系人
     */
    @Transactional(readOnly = true)
    public Optional<DepartmentContact> getPrimaryContact(Integer departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("部门不存在: " + departmentId));
        
        return contactRepository.findByDepartmentAndIsPrimaryTrueAndIsActiveTrue(department);
    }

    /**
     * 获取部门的紧急联系人
     */
    @Transactional(readOnly = true)
    public Optional<DepartmentContact> getEmergencyContact(Integer departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("部门不存在: " + departmentId));
        
        return contactRepository.findByDepartmentAndIsEmergencyTrueAndIsActiveTrue(department);
    }

    /**
     * 根据电话号码查找联系人
     */
    @Transactional(readOnly = true)
    public List<DepartmentContact> findByPhone(String phone) {
        return contactRepository.findByPhoneAndIsActiveTrue(phone);
    }

    /**
     * 根据姓名模糊查询联系人
     */
    @Transactional(readOnly = true)
    public List<DepartmentContact> findByName(String name) {
        return contactRepository.findByNameContaining(name);
    }

    /**
     * 获取联系人详情
     */
    @Transactional(readOnly = true)
    public DepartmentContact getContactById(Integer contactId) {
        return contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("联系人不存在: " + contactId));
    }

    /**
     * 清除部门的主要联系人标记
     */
    private void clearPrimaryContact(Department department) {
        List<DepartmentContact> contacts = contactRepository.findByDepartmentAndIsActiveTrue(department);
        for (DepartmentContact contact : contacts) {
            if (contact.getIsPrimary() != null && contact.getIsPrimary()) {
                contact.setIsPrimary(false);
                contact.setUpdatedAt(LocalDateTime.now());
                contactRepository.save(contact);
            }
        }
    }

    /**
     * 清除部门的紧急联系人标记
     */
    private void clearEmergencyContact(Department department) {
        List<DepartmentContact> contacts = contactRepository.findByDepartmentAndIsActiveTrue(department);
        for (DepartmentContact contact : contacts) {
            if (contact.getIsEmergency() != null && contact.getIsEmergency()) {
                contact.setIsEmergency(false);
                contact.setUpdatedAt(LocalDateTime.now());
                contactRepository.save(contact);
            }
        }
    }
} 