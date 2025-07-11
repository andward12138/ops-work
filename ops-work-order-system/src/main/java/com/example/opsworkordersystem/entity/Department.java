package com.example.opsworkordersystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", unique = true)
    private String code; // 部门编码

    @Column(name = "level")
    private Integer level; // 部门层级 (1-省级, 2-市级, 3-县级, 4-具体部门)

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private DepartmentType type; // 部门类型

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore  // 忽略父部门以避免循环引用
    private Department parent; // 上级部门

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // 忽略子部门列表以避免循环引用
    private List<Department> children; // 下级部门

    // 联系人关联 - 使用@Transient避免序列化问题
    @Transient
    private List<DepartmentContact> contacts; // 部门联系人

    // 权限关联 - 使用@Transient避免序列化问题
    @Transient
    private List<DepartmentPermission> permissions; // 部门权限

    @Column(name = "description")
    private String description;

    @Column(name = "contact_person")
    private String contactPerson; // 联系人

    @Column(name = "contact_phone")
    private String contactPhone; // 联系电话

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 构造方法
    public Department() {}

    public Department(String name, String code, Integer level, DepartmentType type) {
        this.name = name;
        this.code = code;
        this.level = level;
        this.type = type;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public DepartmentType getType() {
        return type;
    }

    public void setType(DepartmentType type) {
        this.type = type;
    }

    public Department getParent() {
        return parent;
    }

    public void setParent(Department parent) {
        this.parent = parent;
    }

    public List<Department> getChildren() {
        return children;
    }

    public void setChildren(List<Department> children) {
        this.children = children;
    }

    public List<DepartmentContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<DepartmentContact> contacts) {
        this.contacts = contacts;
    }

    public List<DepartmentPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<DepartmentPermission> permissions) {
        this.permissions = permissions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 业务方法
    public String getFullPath() {
        if (parent != null) {
            return parent.getFullPath() + " > " + name;
        }
        return name;
    }

    public boolean isParentOf(Department other) {
        if (other == null || other.getParent() == null) {
            return false;
        }
        return this.id.equals(other.getParent().getId());
    }

    public boolean isChildOf(Department other) {
        if (other == null || this.parent == null) {
            return false;
        }
        return other.getId().equals(this.parent.getId());
    }

    // 新增：权限相关方法
    public boolean hasPermission(PermissionType permissionType) {
        if (permissions == null) {
            return false;
        }
        return permissions.stream()
                .anyMatch(p -> p.getPermissionType() == permissionType && p.isValid());
    }

    // 新增：获取主要联系人
    public DepartmentContact getPrimaryContact() {
        if (contacts == null) {
            return null;
        }
        return contacts.stream()
                .filter(contact -> contact.getIsPrimary() != null && contact.getIsPrimary())
                .findFirst()
                .orElse(null);
    }

    // 新增：获取紧急联系人
    public DepartmentContact getEmergencyContact() {
        if (contacts == null) {
            return null;
        }
        return contacts.stream()
                .filter(contact -> contact.getIsEmergency() != null && contact.getIsEmergency())
                .findFirst()
                .orElse(null);
    }
} 