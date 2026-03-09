package com.jobportal.job_portal.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String roleName; // Trả về tên Role (ADMIN, CANDIDATE, EMPLOYER)
    private Boolean isActive;
    private String avatarUrl;
}