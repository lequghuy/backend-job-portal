package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.ChangePasswordRequest;
import com.jobportal.job_portal.dto.UserResponse;
import com.jobportal.job_portal.dto.UserUpdateRequest;
import com.jobportal.job_portal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // API: Lấy thông tin tài khoản (Dùng để hiển thị Header ở Frontend)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(Principal principal) {
        UserResponse response = userService.getMyProfile(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin tài khoản thành công", response));
    }

    // API: Cập nhật thông tin cơ bản (Tên, Email)
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            Principal principal,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse response = userService.updateMyProfile(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật thông tin thành công", response));
    }

    // API: Đổi mật khẩu
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            Principal principal, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đổi mật khẩu thành công", null));
    }

    // API: Tải ảnh đại diện
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            Principal principal,
            @RequestParam("file") MultipartFile file) {

        String avatarUrl = userService.updateAvatar(principal.getName(), file);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật ảnh đại diện thành công", avatarUrl));
    }

}