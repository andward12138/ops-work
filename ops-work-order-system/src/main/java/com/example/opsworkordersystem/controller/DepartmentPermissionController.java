package com.example.opsworkordersystem.controller;

import com.example.opsworkordersystem.entity.DepartmentPermission;
import com.example.opsworkordersystem.entity.PermissionType;
import com.example.opsworkordersystem.service.DepartmentPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/department-permissions")
public class DepartmentPermissionController {

    @Autowired
    private DepartmentPermissionService permissionService;

    /**
     * 授予部门权限
     */
    @PostMapping("/grant")
    public ResponseEntity<DepartmentPermission> grantPermission(@RequestBody GrantPermissionRequest request) {
        try {
            DepartmentPermission permission = permissionService.grantPermission(
                    request.getDepartmentId(),
                    request.getPermissionType(),
                    request.getGrantedBy(),
                    request.getExpiresAt(),
                    request.getRemark()
            );
            return ResponseEntity.ok(permission);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 撤销部门权限
     */
    @PostMapping("/revoke")
    public ResponseEntity<Void> revokePermission(@RequestBody RevokePermissionRequest request) {
        try {
            permissionService.revokePermission(request.getDepartmentId(), request.getPermissionType());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量授予权限
     */
    @PostMapping("/grant-batch")
    public ResponseEntity<Void> grantPermissions(@RequestBody GrantPermissionsRequest request) {
        try {
            permissionService.grantPermissions(
                    request.getDepartmentId(),
                    request.getPermissionTypes(),
                    request.getGrantedBy(),
                    request.getExpiresAt(),
                    request.getRemark()
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量撤销权限
     */
    @PostMapping("/revoke-batch")
    public ResponseEntity<Void> revokePermissions(@RequestBody RevokePermissionsRequest request) {
        try {
            permissionService.revokePermissions(request.getDepartmentId(), request.getPermissionTypes());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取部门的所有权限
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<DepartmentPermission>> getDepartmentPermissions(@PathVariable Integer departmentId) {
        try {
            List<DepartmentPermission> permissions = permissionService.getDepartmentPermissions(departmentId);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取部门的有效权限
     */
    @GetMapping("/department/{departmentId}/valid")
    public ResponseEntity<List<DepartmentPermission>> getValidPermissions(@PathVariable Integer departmentId) {
        try {
            List<DepartmentPermission> permissions = permissionService.getValidPermissions(departmentId);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 检查部门是否拥有特定权限
     */
    @GetMapping("/department/{departmentId}/check/{permissionType}")
    public ResponseEntity<Map<String, Boolean>> hasPermission(
            @PathVariable Integer departmentId,
            @PathVariable PermissionType permissionType) {
        try {
            boolean hasPermission = permissionService.hasPermission(departmentId, permissionType);
            return ResponseEntity.ok(Map.of("hasPermission", hasPermission));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取拥有特定权限的所有部门
     */
    @GetMapping("/permission/{permissionType}/departments")
    public ResponseEntity<List<DepartmentPermission>> getDepartmentsByPermission(@PathVariable PermissionType permissionType) {
        try {
            List<DepartmentPermission> permissions = permissionService.getDepartmentsByPermission(permissionType);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取即将过期的权限
     */
    @GetMapping("/expiring")
    public ResponseEntity<List<DepartmentPermission>> getExpiringPermissions(@RequestParam(defaultValue = "7") int days) {
        try {
            List<DepartmentPermission> permissions = permissionService.getExpiringPermissions(days);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 续期权限
     */
    @PostMapping("/renew")
    public ResponseEntity<DepartmentPermission> renewPermission(@RequestBody RenewPermissionRequest request) {
        try {
            DepartmentPermission permission = permissionService.renewPermission(
                    request.getDepartmentId(),
                    request.getPermissionType(),
                    request.getDays()
            );
            return ResponseEntity.ok(permission);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 复制权限配置
     */
    @PostMapping("/copy")
    public ResponseEntity<Void> copyPermissions(@RequestBody CopyPermissionsRequest request) {
        try {
            permissionService.copyPermissions(
                    request.getSourceDepartmentId(),
                    request.getTargetDepartmentId(),
                    request.getGrantedBy()
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 清除部门所有权限
     */
    @DeleteMapping("/department/{departmentId}/clear")
    public ResponseEntity<Void> clearAllPermissions(@PathVariable Integer departmentId) {
        try {
            permissionService.clearAllPermissions(departmentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 请求类定义
    public static class GrantPermissionRequest {
        private Integer departmentId;
        private PermissionType permissionType;
        private String grantedBy;
        private LocalDateTime expiresAt;
        private String remark;

        // getters and setters
        public Integer getDepartmentId() { return departmentId; }
        public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
        public PermissionType getPermissionType() { return permissionType; }
        public void setPermissionType(PermissionType permissionType) { this.permissionType = permissionType; }
        public String getGrantedBy() { return grantedBy; }
        public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }

    public static class RevokePermissionRequest {
        private Integer departmentId;
        private PermissionType permissionType;

        public Integer getDepartmentId() { return departmentId; }
        public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
        public PermissionType getPermissionType() { return permissionType; }
        public void setPermissionType(PermissionType permissionType) { this.permissionType = permissionType; }
    }

    public static class GrantPermissionsRequest {
        private Integer departmentId;
        private List<PermissionType> permissionTypes;
        private String grantedBy;
        private LocalDateTime expiresAt;
        private String remark;

        public Integer getDepartmentId() { return departmentId; }
        public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
        public List<PermissionType> getPermissionTypes() { return permissionTypes; }
        public void setPermissionTypes(List<PermissionType> permissionTypes) { this.permissionTypes = permissionTypes; }
        public String getGrantedBy() { return grantedBy; }
        public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }

    public static class RevokePermissionsRequest {
        private Integer departmentId;
        private List<PermissionType> permissionTypes;

        public Integer getDepartmentId() { return departmentId; }
        public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
        public List<PermissionType> getPermissionTypes() { return permissionTypes; }
        public void setPermissionTypes(List<PermissionType> permissionTypes) { this.permissionTypes = permissionTypes; }
    }

    public static class RenewPermissionRequest {
        private Integer departmentId;
        private PermissionType permissionType;
        private int days;

        public Integer getDepartmentId() { return departmentId; }
        public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
        public PermissionType getPermissionType() { return permissionType; }
        public void setPermissionType(PermissionType permissionType) { this.permissionType = permissionType; }
        public int getDays() { return days; }
        public void setDays(int days) { this.days = days; }
    }

    public static class CopyPermissionsRequest {
        private Integer sourceDepartmentId;
        private Integer targetDepartmentId;
        private String grantedBy;

        public Integer getSourceDepartmentId() { return sourceDepartmentId; }
        public void setSourceDepartmentId(Integer sourceDepartmentId) { this.sourceDepartmentId = sourceDepartmentId; }
        public Integer getTargetDepartmentId() { return targetDepartmentId; }
        public void setTargetDepartmentId(Integer targetDepartmentId) { this.targetDepartmentId = targetDepartmentId; }
        public String getGrantedBy() { return grantedBy; }
        public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }
    }
} 