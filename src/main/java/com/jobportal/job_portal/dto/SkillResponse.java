package com.jobportal.job_portal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillResponse {
    private Long id;
    private String name;
}
