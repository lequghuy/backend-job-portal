package com.jobportal.job_portal.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CandidateProfileResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String address;
    private String experience;
    private String education;

    // Trả về danh sách TÊN kỹ năng cho Frontend dễ hiển thị (VD: ["Java",
    // "Spring"])
    private Set<String> skills;
}