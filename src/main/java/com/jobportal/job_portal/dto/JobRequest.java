package com.jobportal.job_portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class JobRequest {

    @NotBlank(message = "Tiêu đề công việc không được để trống")
    private String title;

    private String description;

    private Double salaryMin;
    private Double salaryMax;

    @NotBlank(message = "Địa điểm không được để trống")
    private String location;

    private String thumbnail;

    private String employmentType; // FULL_TIME, PART_TIME...
    private String experienceLevel; // FRESHER, JUNIOR, SENIOR...

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline; // Hạn chót nộp CV

    @NotNull(message = "Danh mục ngành nghề không được để trống")
    private Long categoryId;

    // Danh sách ID kỹ năng yêu cầu (VD: [1, 2, 5])
    private Set<Long> skillIds;
}