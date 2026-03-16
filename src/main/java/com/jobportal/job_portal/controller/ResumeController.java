package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.ResumeRequest;
import com.jobportal.job_portal.dto.ResumeResponse;
import com.jobportal.job_portal.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidate/resumes")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MANAGE_RESUMES')")
public class ResumeController {

    private final ResumeService resumeService;

    // API: Thêm CV mới
    @PostMapping
    public ResponseEntity<ApiResponse<ResumeResponse>> addResume(
            Principal principal, @Valid @RequestBody ResumeRequest request) {
        ResumeResponse response = resumeService.addResume(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Thêm CV thành công", response));
    }

    // API: Xem danh sách CV
    @GetMapping
    public ResponseEntity<ApiResponse<List<ResumeResponse>>> getMyResumes(Principal principal) {
        List<ResumeResponse> response = resumeService.getMyResumes(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách CV thành công", response));
    }

    // API: Đổi CV mặc định
    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<String>> setDefaultResume(
            Principal principal, @PathVariable Long id) {
        resumeService.setDefaultResume(principal.getName(), id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đã đặt CV làm mặc định", null));
    }

    // API: Xóa CV
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteResume(
            Principal principal, @PathVariable Long id) {
        resumeService.deleteResume(principal.getName(), id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đã xóa CV thành công", null));
    }

    // HÀM MỚI: API Upload CV
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResumeResponse>> uploadResume(
            Principal principal,
            @RequestParam("file") MultipartFile file,
            @RequestParam("resumeName") String resumeName) {

        ResumeResponse response = resumeService.uploadAndAddResume(principal.getName(), file, resumeName);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tải CV lên thành công", response));
    }
}