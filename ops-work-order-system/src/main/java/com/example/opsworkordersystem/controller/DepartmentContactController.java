package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.DepartmentContact;
import com.example.opsworkordersystem.service.DepartmentContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/department-contacts")
public class DepartmentContactController {

    @Autowired
    private DepartmentContactService contactService;

    /**
     * 创建部门联系人
     */
    @PostMapping("/department/{departmentId}")
    public ResponseEntity<DepartmentContact> createContact(
            @PathVariable Integer departmentId,
            @RequestBody DepartmentContact contact) {
        try {
            DepartmentContact createdContact = contactService.createContact(departmentId, contact);
            return ResponseEntity.ok(createdContact);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取部门的所有联系人
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<DepartmentContact>> getDepartmentContacts(@PathVariable Integer departmentId) {
        try {
            List<DepartmentContact> contacts = contactService.getDepartmentContacts(departmentId);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取部门的主要联系人
     */
    @GetMapping("/department/{departmentId}/primary")
    public ResponseEntity<DepartmentContact> getPrimaryContact(@PathVariable Integer departmentId) {
        try {
            Optional<DepartmentContact> contact = contactService.getPrimaryContact(departmentId);
            return contact.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取部门的紧急联系人
     */
    @GetMapping("/department/{departmentId}/emergency")
    public ResponseEntity<DepartmentContact> getEmergencyContact(@PathVariable Integer departmentId) {
        try {
            Optional<DepartmentContact> contact = contactService.getEmergencyContact(departmentId);
            return contact.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取联系人详情
     */
    @GetMapping("/{contactId}")
    public ResponseEntity<DepartmentContact> getContact(@PathVariable Integer contactId) {
        try {
            DepartmentContact contact = contactService.getContactById(contactId);
            return ResponseEntity.ok(contact);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新联系人信息
     */
    @PutMapping("/{contactId}")
    public ResponseEntity<DepartmentContact> updateContact(
            @PathVariable Integer contactId,
            @RequestBody DepartmentContact contactUpdate) {
        try {
            DepartmentContact updatedContact = contactService.updateContact(contactId, contactUpdate);
            return ResponseEntity.ok(updatedContact);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除联系人
     */
    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteContact(@PathVariable Integer contactId) {
        try {
            contactService.deleteContact(contactId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据电话号码查找联系人
     */
    @GetMapping("/search/phone")
    public ResponseEntity<List<DepartmentContact>> findByPhone(@RequestParam String phone) {
        try {
            List<DepartmentContact> contacts = contactService.findByPhone(phone);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据姓名模糊查询联系人
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<DepartmentContact>> findByName(@RequestParam String name) {
        try {
            List<DepartmentContact> contacts = contactService.findByName(name);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 