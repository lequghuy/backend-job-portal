package com.jobportal.job_portal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class CandidateProfileRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String address;
    private String experience;
    private String education;

    // Frontend sẽ gửi lên một mảng các ID của kỹ năng (VD: [1, 3, 5])
    private Set<Long> skillIds;
}