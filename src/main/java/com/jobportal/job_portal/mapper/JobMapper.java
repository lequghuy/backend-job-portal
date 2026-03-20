package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.JobResponse;
import com.jobportal.job_portal.entity.JobEntity;
import com.jobportal.job_portal.entity.SkillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JobMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", source = "company.companyName")
    @Mapping(target = "companyLogo", source = "company.logo")
    @Mapping(target = "categoryName", source = "category.name")
    // SỬA Ở ĐÂY: Sử dụng tên tham số là 'entity'
    @Mapping(target = "skills", expression = "java(mapSkills(entity.getSkills()))")
    @Mapping(target = "salaryMin", source = "salaryMin")
    @Mapping(target = "salaryMax", source = "salaryMax")
    // Đảm bảo trường logo của Company được đưa vào thumbnail của DTO
    @Mapping(target = "thumbnail", source = "thumbnail")
    JobResponse toResponse(JobEntity entity);

    List<JobResponse> toResponseList(List<JobEntity> entities);

    // Hàm hỗ trợ bóc tách Tên kỹ năng từ Bảng trung gian
    default Set<String> mapSkills(Set<SkillEntity> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptySet();
        }
        return skills.stream().map(SkillEntity::getName).collect(Collectors.toSet());
    }
}