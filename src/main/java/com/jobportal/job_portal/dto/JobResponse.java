package com.jobportal.job_portal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private Double salaryMin;
    private Double salaryMax;
    private String location;
    private String employmentType;
    private String experienceLevel;
    private LocalDate deadline;
    private String status;
    private LocalDateTime createdAt;
    private String thumbnail;

    // Lấy thêm thông tin từ các bảng liên quan để Frontend dễ hiển thị
    private Long companyId;
    private String companyName;
    private String categoryName;
    private Long categoryId;
    private Set<String> skills; // Danh sách TÊN kỹ năng
}