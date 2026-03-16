package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.JobApplicationRequest;
import com.jobportal.job_portal.dto.JobApplicationResponse;
import com.jobportal.job_portal.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/candidate/applications")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('APPLY_JOBS')")
public class CandidateApplicationController {

    private final JobApplicationService applicationService;

    // Nộp CV
    @PostMapping
    public ResponseEntity<ApiResponse<JobApplicationResponse>> applyForJob(
            Principal principal, @Valid @RequestBody JobApplicationRequest request) {
        JobApplicationResponse response = applicationService.applyForJob(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Ứng tuyển thành công", response));
    }

    // Xem lịch sử đã nộp
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getMyApplications(Principal principal) {
        List<JobApplicationResponse> response = applicationService.getMyApplications(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy lịch sử ứng tuyển thành công", response));
    }
}