package com.jobportal.job_portal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobApplicationResponse {
    private Long id;

    // Thông tin việc làm
    private Long jobId;
    private String jobTitle;
    private String companyName;

    // Thông tin Ứng viên & CV
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String resumeUrl;
    private String resumeName;

    private String status;
    private LocalDateTime appliedAt;
}