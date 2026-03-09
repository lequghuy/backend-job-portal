package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.CompanyResponse;
import com.jobportal.job_portal.entity.CompanyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyResponse toResponse(CompanyEntity entity);
}