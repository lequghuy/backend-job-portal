package com.jobportal.job_portal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillRequest {
    @NotBlank(message = "Tên kỹ năng không được để trống")
    private String name;
}