package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.CompanyResponse;
import com.jobportal.job_portal.entity.CompanyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    // Lấy email và trạng thái từ bảng users (do CompanyEntity có trường user)
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "isActive", source = "user.isActive")
    CompanyResponse toResponse(CompanyEntity entity);
}