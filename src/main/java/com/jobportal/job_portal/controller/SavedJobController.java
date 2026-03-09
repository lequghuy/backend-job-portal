package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.SavedJobRequest;
import com.jobportal.job_portal.dto.SavedJobResponse;
import com.jobportal.job_portal.service.SavedJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/candidate/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    // API: Lưu việc làm
    @PostMapping
    public ResponseEntity<ApiResponse<SavedJobResponse>> saveJob(
            Principal principal, @Valid @RequestBody SavedJobRequest request) {
        SavedJobResponse response = savedJobService.saveJob(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lưu việc làm thành công", response));
    }

    // API: Xem danh sách việc làm đã lưu
    @GetMapping
    public ResponseEntity<ApiResponse<List<SavedJobResponse>>> getMySavedJobs(Principal principal) {
        List<SavedJobResponse> response = savedJobService.getMySavedJobs(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách việc làm đã lưu thành công", response));
    }

    // API: Bỏ lưu việc làm
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<String>> unsaveJob(
            Principal principal, @PathVariable Long jobId) {
        savedJobService.unsaveJob(principal.getName(), jobId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bỏ lưu việc làm thành công", null));
    }
}