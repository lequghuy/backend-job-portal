package com.jobportal.job_portal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResumeRequest {
    @NotBlank(message = "Tên CV không được để trống")
    private String resumeName;

    @NotBlank(message = "Đường dẫn file CV không được để trống")
    private String fileUrl;
}