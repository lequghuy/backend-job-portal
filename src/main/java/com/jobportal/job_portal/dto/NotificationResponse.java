package com.jobportal.job_portal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}