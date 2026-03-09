package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.JobCategoryRequest;
import com.jobportal.job_portal.dto.JobCategoryResponse;
import com.jobportal.job_portal.entity.JobCategoryEntity;
import com.jobportal.job_portal.exception.ApiException;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.JobCategoryMapper;
import com.jobportal.job_portal.repository.JobCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobCategoryService {

    private final JobCategoryRepository categoryRepository;
    private final JobCategoryMapper categoryMapper;

    // 1. Dành cho Public: Lấy tất cả danh mục
    public List<JobCategoryResponse> getAllCategories() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }

    // 2. Dành cho Admin: Thêm danh mục mới
    public JobCategoryResponse createCategory(JobCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ApiException("Danh mục này đã tồn tại");
        }

        JobCategoryEntity category = new JobCategoryEntity();
        category.setName(request.getName());

        JobCategoryEntity savedCategory = categoryRepository.save(category);
        log.info("Tạo mới danh mục: {}", savedCategory.getName());
        return categoryMapper.toResponse(savedCategory);
    }

    // 3. Dành cho Admin: Sửa tên danh mục
    public JobCategoryResponse updateCategory(Long id, JobCategoryRequest request) {
        JobCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByName(request.getName())) {
            throw new ApiException("Tên danh mục này đã được sử dụng");
        }

        category.setName(request.getName());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    // 4. Dành cho Admin: Xóa danh mục
    public void deleteCategory(Long id) {
        JobCategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        // Lưu ý thực tế: Bạn nên kiểm tra xem có Job nào đang dùng category này không
        // trước khi xóa
        categoryRepository.delete(category);
        log.info("Xóa danh mục ID: {}", id);
    }
}