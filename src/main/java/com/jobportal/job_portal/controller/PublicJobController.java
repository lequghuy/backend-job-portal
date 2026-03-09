package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.JobResponse;
import com.jobportal.job_portal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class PublicJobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> getAllOpenJobs() {
        return ResponseEntity
                .ok(new ApiResponse<>(true, "Lấy danh sách việc làm thành công", jobService.getAllOpenJobs()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        return ResponseEntity
                .ok(new ApiResponse<>(true, "Lấy chi tiết việc làm thành công", jobService.getJobById(id)));
    }
}