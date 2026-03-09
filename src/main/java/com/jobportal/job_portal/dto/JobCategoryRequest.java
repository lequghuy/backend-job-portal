package com.jobportal.job_portal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobCategoryRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
}