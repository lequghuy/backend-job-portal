package com.jobportal.job_portal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequest {

    @NotBlank(message = "Tên công ty không được để trống")
    private String companyName;

    private String description;

    private String website;

    private String logo;

    @NotBlank(message = "Địa chỉ công ty không được để trống")
    private String location;
}