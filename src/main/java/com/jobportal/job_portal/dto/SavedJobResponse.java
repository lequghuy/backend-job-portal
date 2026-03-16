package com.jobportal.job_portal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SavedJobResponse {
    private Long id; // ID của bản ghi lưu

    // Thông tin tóm tắt về Job để hiển thị trên thẻ (card)
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private Double salaryMin;
    private Double salaryMax;
    private String status;
    private String thumbnail;

    private LocalDateTime savedAt;
}