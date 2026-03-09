package com.jobportal.job_portal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data; // Phải có cái này

@Data // <-- Annotation này cực kỳ quan trọng, nó tự động tạo ra getJobId() và
      // getResumeId()
public class JobApplicationRequest {

    @NotNull(message = "ID công việc không được để trống")
    private Long jobId; // Phải viết đúng chính tả biến này

    @NotNull(message = "ID CV không được để trống")
    private Long resumeId; // Phải viết đúng chính tả biến này
}