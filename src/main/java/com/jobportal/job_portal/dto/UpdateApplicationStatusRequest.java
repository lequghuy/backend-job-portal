package com.jobportal.job_portal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {
    @NotBlank(message = "Trạng thái không được để trống (PENDING, REVIEWED, ACCEPTED, REJECTED)")
    private String status;
}