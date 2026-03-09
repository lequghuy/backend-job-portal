package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.JobCategoryResponse;
import com.jobportal.job_portal.entity.JobCategoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobCategoryMapper {
    JobCategoryResponse toResponse(JobCategoryEntity entity);

    List<JobCategoryResponse> toResponseList(List<JobCategoryEntity> entities);
}