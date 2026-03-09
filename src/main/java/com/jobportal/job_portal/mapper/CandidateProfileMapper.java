package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.CandidateProfileResponse;
import com.jobportal.job_portal.entity.CandidateProfileEntity;
import com.jobportal.job_portal.entity.SkillEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CandidateProfileMapper {

    @Mapping(target = "skills", expression = "java(mapSkills(entity.getSkills()))")
    CandidateProfileResponse toResponse(CandidateProfileEntity entity);

    // Hàm hỗ trợ MapStruct bóc tách tên Kỹ năng từ Entity
    default Set<String> mapSkills(Set<SkillEntity> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptySet();
        }
        return skills.stream()
                .map(SkillEntity::getName)
                .collect(Collectors.toSet());
    }
}