package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.UserResponse;
import com.jobportal.job_portal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
// THAY ĐỔI: Sử dụng quyền MANAGE_USERS
@PreAuthorize("hasAuthority('MANAGE_USERS')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách người dùng thành công",
                userService.getAllUsers(page, size)));
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserStatus(@PathVariable Long id) {
        UserResponse response = userService.toggleUserStatus(id);
        String action = response.getIsActive() ? "Mở khóa" : "Khóa";
        return ResponseEntity.ok(new ApiResponse<>(true, action + " tài khoản thành công", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @jakarta.validation.Valid @RequestBody com.jobportal.job_portal.dto.CreateUserRequest request) {
        UserResponse response = userService.createUserByAdmin(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo tài khoản thành công", response));
    }
}