package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.JobRequest;
import com.jobportal.job_portal.dto.JobResponse;
import com.jobportal.job_portal.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/employer/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MANAGE_OWN_JOBS')")
public class EmployerJobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getMyJobs(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        // 1. Khởi tạo cấu hình phân trang (Sắp xếp tin mới nhất lên đầu)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 2. Gọi hàm getMyJobs với ĐẦY ĐỦ 2 tham số: Email và Pageable
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách việc làm thành công",
                jobService.getMyJobs(principal.getName(), pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            Principal principal, @Valid @RequestBody JobRequest request) {
        JobResponse response = jobService.createJob(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng việc làm thành công", response));
    }

    // 2. API MỚI: Chỉ dùng để Upload File (Ảnh/PDF) cho Job đã tạo
    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadJobFile(
            Principal principal,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        // Gọi Service để xử lý
        jobService.uploadJobThumbnail(principal.getName(), id, file);

        return ResponseEntity.ok(new ApiResponse<>(true, "Tải ảnh minh họa thành công", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            Principal principal, @PathVariable Long id, @Valid @RequestBody JobRequest request) {
        JobResponse response = jobService.updateJob(principal.getName(), id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật việc làm thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteJob(Principal principal, @PathVariable Long id) {
        jobService.deleteJob(principal.getName(), id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa việc làm thành công", null));
    }
}