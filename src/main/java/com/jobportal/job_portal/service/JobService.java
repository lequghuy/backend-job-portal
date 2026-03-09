package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.JobRequest;
import com.jobportal.job_portal.dto.JobResponse;
import com.jobportal.job_portal.entity.*;
import com.jobportal.job_portal.exception.ApiException;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.JobMapper;
import com.jobportal.job_portal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j

public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobCategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final JobMapper jobMapper;

    // --- LUỒNG PUBLIC ---
    @Transactional(readOnly = true)
    public List<JobResponse> getAllOpenJobs() {
        List<JobEntity> jobs = jobRepository.findByStatusOrderByCreatedAtDesc("OPEN");
        return jobMapper.toResponseList(jobs);
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        JobEntity job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm này"));
        return jobMapper.toResponse(job);
    }

    // --- LUỒNG EMPLOYER ---
    @Transactional(readOnly = true)
    public List<JobResponse> getMyJobs(String email) {
        List<JobEntity> jobs = jobRepository.findByCompany_User_Email(email);
        return jobMapper.toResponseList(jobs);
    }

    @Transactional
    public JobResponse createJob(String email, JobRequest request) {
        CompanyEntity company = companyRepository.findByUser_Email(email)
                .orElseThrow(() -> new ApiException("Bạn cần cập nhật Profile Công ty trước khi đăng việc làm!"));

        JobCategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));

        JobEntity job = new JobEntity();
        mapRequestToEntity(request, job, category);

        job.setCompany(company);
        job.setStatus("OPEN"); // Mặc định khi tạo là đang mở

        JobEntity savedJob = jobRepository.save(job);
        log.info("Đăng việc làm mới thành công: {}", savedJob.getTitle());
        return jobMapper.toResponse(savedJob);
    }

    @Transactional
    public JobResponse updateJob(String email, Long jobId, JobRequest request) {
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm"));

        // Kiểm tra quyền sở hữu: Việc làm này có đúng là của công ty này đăng không?
        if (!job.getCompany().getUser().getEmail().equals(email)) {
            throw new ApiException("Bạn không có quyền sửa tin tuyển dụng này");
        }

        JobCategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));

        mapRequestToEntity(request, job, category);

        JobEntity updatedJob = jobRepository.save(job);
        log.info("Cập nhật việc làm thành công: {}", jobId);
        return jobMapper.toResponse(updatedJob);
    }

    @Transactional
    public void deleteJob(String email, Long jobId) {
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm"));

        if (!job.getCompany().getUser().getEmail().equals(email)) {
            throw new ApiException("Bạn không có quyền xóa tin tuyển dụng này");
        }

        jobRepository.delete(job);
        log.info("Xóa việc làm thành công: {}", jobId);
    }

    // Hàm hỗ trợ map Request vào Entity
    private void mapRequestToEntity(JobRequest request, JobEntity job, JobCategoryEntity category) {
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setLocation(request.getLocation());
        job.setEmploymentType(request.getEmploymentType());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setDeadline(request.getDeadline());
        job.setCategory(category);

        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
            Set<SkillEntity> skills = skillRepository.findByIdIn(request.getSkillIds());
            job.setSkills(skills);
        } else {
            job.setSkills(new HashSet<>());
        }
    }
}