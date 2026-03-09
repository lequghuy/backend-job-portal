package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.ResumeResponse;
import com.jobportal.job_portal.entity.ResumeEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    ResumeResponse toResponse(ResumeEntity entity);

    List<ResumeResponse> toResponseList(List<ResumeEntity> entities);
}