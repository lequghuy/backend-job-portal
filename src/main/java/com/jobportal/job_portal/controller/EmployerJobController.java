package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.JobRequest;
import com.jobportal.job_portal.dto.JobResponse;
import com.jobportal.job_portal.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/employer/jobs")
@RequiredArgsConstructor
public class EmployerJobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> getMyJobs(Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách việc làm của tôi thành công",
                jobService.getMyJobs(principal.getName())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            Principal principal, @Valid @RequestBody JobRequest request) {
        JobResponse response = jobService.createJob(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng việc làm thành công", response));
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