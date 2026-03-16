package com.jobportal.job_portal.dto;

import lombok.Data;

@Data
public class CompanyResponse {
    private Long id;
    private String companyName;
    private String description;
    private String website;
    private String location;
    private String logo;

    // Lấy thêm thông tin từ UserEntity liên kết với Công ty này
    private String email;
    private Boolean isActive;
}