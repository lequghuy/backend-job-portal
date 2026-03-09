package com.jobportal.job_portal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResumeResponse {
    private Long id;
    private String resumeName;
    private String fileUrl;
    private Boolean isDefault;
    private LocalDateTime createdAt;
}