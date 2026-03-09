package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.*;
import com.jobportal.job_portal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse responseData = authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng ký thành công", responseData));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse responseData = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng nhập thành công", responseData));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        AuthResponse responseData = authService.refreshToken(request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Cấp lại Token thành công", responseData));
    }
}