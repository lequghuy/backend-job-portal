package com.jobportal.job_portal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SavedJobRequest {
    @NotNull(message = "ID công việc không được để trống")
    private Long jobId;
}