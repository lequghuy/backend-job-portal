package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.SkillResponse;
import com.jobportal.job_portal.entity.SkillEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillResponse toResponse(SkillEntity entity);

    List<SkillResponse> toResponseList(List<SkillEntity> entities);
}