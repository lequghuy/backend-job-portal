package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.JobApplicationResponse;
import com.jobportal.job_portal.dto.UpdateApplicationStatusRequest;
import com.jobportal.job_portal.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/employer")
@RequiredArgsConstructor
public class EmployerApplicationController {

    private final JobApplicationService applicationService;

    // Lấy danh sách CV nộp vào 1 job cụ thể
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getApplicationsForJob(
            Principal principal, @PathVariable Long jobId) {
        List<JobApplicationResponse> response = applicationService.getApplicationsForMyJob(principal.getName(), jobId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách ứng viên thành công", response));
    }

    // Cập nhật trạng thái duyệt/loại CV
    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> updateApplicationStatus(
            Principal principal,
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        JobApplicationResponse response = applicationService.updateApplicationStatus(principal.getName(), applicationId,
                request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật trạng thái thành công", response));
    }

    // API: Lấy tất cả CV nộp vào công ty của tôi
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getAllMyCandidates(Principal principal) {

        // principal.getName() chính là cái chìa khóa vạn năng (Email người đăng nhập)
        String email = principal.getName();

        List<JobApplicationResponse> response = applicationService.getAllCandidatesForMyCompany(email);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách ứng viên thành công", response));
    }
}