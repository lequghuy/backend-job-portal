package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.entity.PermissionEntity;
import com.jobportal.job_portal.service.AdminRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
// Dùng tạm quyền MANAGE_USERS cho tính năng này
@PreAuthorize("hasAuthority('MANAGE_USERS')")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllRoles() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Thành công", adminRoleService.getAllRolesWithPermissions()));
    }

    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<PermissionEntity>>> getAllPermissions() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Thành công", adminRoleService.getAllPermissions()));
    }

    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<Void>> updateRolePermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) { // Nhận mảng ID từ Frontend gửi lên

        adminRoleService.updateRolePermissions(roleId, permissionIds);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật phân quyền thành công", null));
    }

    // Thêm API này vào trong AdminRoleController
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createRole(@RequestBody java.util.Map<String, String> request) {
        String roleName = request.get("name");
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new com.jobportal.job_portal.exception.ApiException("Tên nhóm quyền không được để trống");
        }
        adminRoleService.createRole(roleName);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo nhóm quyền thành công", null));
    }

    @PutMapping("/{roleId}/name")
    public ResponseEntity<ApiResponse<Void>> updateRoleName(
            @PathVariable Long roleId,
            @RequestBody java.util.Map<String, String> request) {

        String newName = request.get("name");
        if (newName == null || newName.trim().isEmpty()) {
            throw new com.jobportal.job_portal.exception.ApiException("Tên nhóm quyền không được để trống");
        }

        adminRoleService.updateRoleName(roleId, newName);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đổi tên nhóm quyền thành công", null));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long roleId) {
        adminRoleService.deleteRole(roleId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa nhóm quyền thành công", null));
    }
}