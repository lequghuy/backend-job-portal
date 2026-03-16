package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.repository.CompanyRepository;
import com.jobportal.job_portal.repository.JobRepository;
import com.jobportal.job_portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
// Dùng chung quyền MANAGE_USERS hoặc tạo thêm quyền VIEW_DASHBOARD đều được
@PreAuthorize("hasAuthority('MANAGE_USERS')")
public class AdminDashboardController {
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCompanies", companyRepository.count());
        stats.put("totalJobs", jobRepository.count());
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thống kê thành công", stats));
    }
}