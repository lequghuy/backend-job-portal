package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.SavedJobResponse;
import com.jobportal.job_portal.entity.SavedJobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SavedJobMapper {

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "jobTitle", source = "job.title")
    @Mapping(target = "companyName", source = "job.company.companyName")
    @Mapping(target = "location", source = "job.location")
    @Mapping(target = "salaryMin", source = "job.salaryMin")
    @Mapping(target = "salaryMax", source = "job.salaryMax")
    @Mapping(target = "status", source = "job.status")
    SavedJobResponse toResponse(SavedJobEntity entity);

    List<SavedJobResponse> toResponseList(List<SavedJobEntity> entities);
}