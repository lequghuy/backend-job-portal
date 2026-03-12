package com.jobportal.job_portal.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;

    // Thêm thông tin người dùng để Frontend dễ hiển thị
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String role;

}
