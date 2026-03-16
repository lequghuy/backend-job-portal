package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.UserResponse;
import com.jobportal.job_portal.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // API: Xem danh sách toàn bộ người dùng
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity
                .ok(new ApiResponse<>(true, "Lấy danh sách người dùng phân trang thành công",
                        userService.getAllUsers(page, size)));
    }

    // API: Khóa hoặc Mở khóa người dùng
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserStatus(@PathVariable Long id) {
        UserResponse response = userService.toggleUserStatus(id);
        String action = response.getIsActive() ? "Mở khóa" : "Khóa";
        return ResponseEntity.ok(new ApiResponse<>(true, action + " tài khoản thành công", response));
    }
}