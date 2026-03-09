package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.CandidateProfileRequest;
import com.jobportal.job_portal.dto.CandidateProfileResponse;
import com.jobportal.job_portal.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/candidate/profile")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService profileService;

    // API: Lấy hồ sơ của ứng viên đang đăng nhập
    @GetMapping
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> getMyProfile(Principal principal) {
        // principal.getName() sẽ tự động lấy ra chuỗi 'subject' trong JWT (chính là
        // Email của User)
        String email = principal.getName();

        CandidateProfileResponse responseData = profileService.getMyProfile(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin hồ sơ thành công", responseData));
    }

    // API: Cập nhật hồ sơ của ứng viên đang đăng nhập
    @PutMapping
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> updateMyProfile(
            Principal principal,
            @Valid @RequestBody CandidateProfileRequest request) {

        String email = principal.getName();
        CandidateProfileResponse responseData = profileService.updateMyProfile(email, request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật hồ sơ thành công", responseData));
    }
}