package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.JobCategoryRequest;
import com.jobportal.job_portal.dto.JobCategoryResponse;
import com.jobportal.job_portal.service.JobCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class JobCategoryController {

    private final JobCategoryService categoryService;

    // API Public
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobCategoryResponse>>> getAllCategories() {
        return ResponseEntity
                .ok(new ApiResponse<>(true, "Lấy danh sách ngành nghề thành công", categoryService.getAllCategories()));
    }

    // API cho Admin
    @PostMapping
    public ResponseEntity<ApiResponse<JobCategoryResponse>> createCategory(
            @Valid @RequestBody JobCategoryRequest request) {
        JobCategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo danh mục thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobCategoryResponse>> updateCategory(
            @PathVariable Long id, @Valid @RequestBody JobCategoryRequest request) {
        JobCategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật danh mục thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa danh mục thành công", null));
    }
}