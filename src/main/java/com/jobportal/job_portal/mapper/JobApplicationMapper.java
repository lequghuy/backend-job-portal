package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.JobApplicationResponse;
import com.jobportal.job_portal.entity.JobApplicationEntity;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobApplicationMapper {

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "jobTitle", source = "job.title")
    @Mapping(target = "companyName", source = "job.company.companyName")
    @Mapping(target = "candidateId", source = "candidate.id")
    @Mapping(target = "candidateName", source = "candidate.fullName")
    @Mapping(target = "candidateEmail", source = "candidate.email")
    @Mapping(target = "resumeUrl", source = "resume.fileUrl")
    @Mapping(target = "resumeName", source = "resume.resumeName")
    @Mapping(target = "employerEmail", source = "job.company.user.email")
    JobApplicationResponse toResponse(JobApplicationEntity entity);

    List<JobApplicationResponse> toResponseList(List<JobApplicationEntity> entities);

    // ==============================================================
    // BẢO MẬT: CHỈ TRẢ VỀ EMAIL NHÀ TUYỂN DỤNG NẾU ĐƯỢC ACCEPTED
    // ==============================================================
    @AfterMapping
    default void filterEmployerEmail(JobApplicationEntity entity, @MappingTarget JobApplicationResponse response) {
        if (!"ACCEPTED".equalsIgnoreCase(entity.getStatus())) {
            // Nếu trạng thái chưa phải ĐƯỢC NHẬN, set null để bảo mật thông tin
            response.setEmployerEmail(null);
        }
    }
}