package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.JobResponse;
import com.jobportal.job_portal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class PublicJobController {

    private final JobService jobService;

    // API 1: Lấy tất cả (Dùng cho trang chủ)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobResponse>>> getAllOpenJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity
                .ok(new ApiResponse<>(true, "Lấy danh sách thành công", jobService.getAllOpenJobs(pageable)));
    }

    // API 2: Lấy chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy chi tiết thành công", jobService.getJobById(id)));
    }

    // API 3: Filter & Sort (Dùng cho trang tìm kiếm)
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<JobResponse>>> filterJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double salary,
            @RequestParam(defaultValue = "newest") String sort, // THÊM BIẾN NÀY ĐỂ NHẬN LỆNH SẮP XẾP
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Xử lý logic sắp xếp
        Sort sortBy;
        if ("salary".equalsIgnoreCase(sort)) {
            sortBy = Sort.by("salaryMax").descending(); // Mức lương cao nhất đưa lên đầu
        } else {
            sortBy = Sort.by("createdAt").descending(); // Mặc định là Mới nhất
        }

        Pageable pageable = PageRequest.of(page, size, sortBy);
        Page<JobResponse> jobs = jobService.getFilteredJobs(keyword, type, level, location, salary, pageable);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách việc làm thành công", jobs));
    }

    @GetMapping("/filter-options")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFilterOptions() {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Lấy danh sách bộ lọc thành công",
                jobService.getFilterOptions()));
    }
}