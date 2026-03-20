package com.jobportal.job_portal.service;

import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.job_portal.dto.JobRequest;
import com.jobportal.job_portal.dto.JobResponse;
import com.jobportal.job_portal.entity.CompanyEntity;
import com.jobportal.job_portal.entity.EmployerSubscriptionEntity;
import com.jobportal.job_portal.entity.JobCategoryEntity;
import com.jobportal.job_portal.entity.JobEntity;
import com.jobportal.job_portal.entity.SkillEntity;
import com.jobportal.job_portal.exception.ApiException;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.JobMapper;
import com.jobportal.job_portal.repository.CompanyRepository;
import com.jobportal.job_portal.repository.EmployerSubscriptionRepository;
import com.jobportal.job_portal.repository.JobCategoryRepository;
import com.jobportal.job_portal.repository.JobRepository;
import com.jobportal.job_portal.repository.SkillRepository;
import com.jobportal.job_portal.repository.specification.JobSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobCategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final JobMapper jobMapper;
    private final CloudinaryService cloudinaryService;

    private final EmployerSubscriptionRepository subscriptionRepository;

    // --- LUỒNG PUBLIC ---
    @Transactional(readOnly = true)
    public Page<JobResponse> getAllOpenJobs(Pageable pageable) {
        return jobRepository.findByStatusOrderByCreatedAtDesc("OPEN", pageable)
                .map(jobMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        JobEntity job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm này"));
        return jobMapper.toResponse(job);
    }

    // --- LUỒNG EMPLOYER ---
    @Transactional(readOnly = true)
    public Page<JobResponse> getMyJobs(String email, Pageable pageable) {
        Page<JobEntity> jobs = jobRepository.findByCompany_User_Email(email, pageable);
        return jobs.map(jobMapper::toResponse); // Dùng map thay vì toResponseList
    }

    @Transactional
    public JobResponse createJob(String email, JobRequest request) {
        CompanyEntity company = companyRepository.findByUser_Email(email)
                .orElseThrow(() -> new ApiException("Bạn cần cập nhật Profile Công ty trước khi đăng việc làm!"));

        // ==========================================
        // LOGIC KIỂM TRA GÓI DỊCH VỤ Ở ĐÂY
        // ==========================================
        EmployerSubscriptionEntity activeSub = subscriptionRepository
                .findFirstByEmployer_EmailAndStatusOrderByIdDesc(email, "ACTIVE")
                .orElseThrow(() -> new ApiException(
                        "Bạn không có gói dịch vụ nào đang hoạt động. Vui lòng mua gói để đăng tin!"));

        // 1. Kiểm tra thời hạn
        if (LocalDateTime.now().isAfter(activeSub.getEndDate())) {
            activeSub.setStatus("EXPIRED");
            subscriptionRepository.save(activeSub);
            throw new ApiException(
                    "Gói dịch vụ của bạn đã hết hạn vào lúc " + activeSub.getEndDate() + ". Vui lòng mua gói mới!");
        }

        // 2. Kiểm tra số lượng bài đăng
        if (activeSub.getJobsPosted() >= activeSub.getMaxJobs()) {
            activeSub.setStatus("EXPIRED"); // Dùng hết lượt thì cũng coi như hết gói
            subscriptionRepository.save(activeSub);
            throw new ApiException("Bạn đã dùng hết " + activeSub.getMaxJobs() + " lượt đăng tin của gói này!");
        }
        // ==========================================

        JobCategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại"));

        JobEntity job = new JobEntity();
        mapRequestToEntity(request, job, category);

        job.setCompany(company);
        job.setStatus("OPEN");

        JobEntity savedJob = jobRepository.save(job);

        // ==========================================
        // CỘNG LƯỢT ĐĂNG BÀI LÊN 1
        // ==========================================
        activeSub.setJobsPosted(activeSub.getJobsPosted() + 1);

        // Nếu vừa đăng xong bài này mà hết sạch quota thì chuyển thành EXPIRED luôn
        if (activeSub.getJobsPosted().equals(activeSub.getMaxJobs())) {
            activeSub.setStatus("EXPIRED");
        }
        subscriptionRepository.save(activeSub);
        // ==========================================

        log.info("Đăng việc làm mới thành công: {}", savedJob.getTitle());
        return jobMapper.toResponse(savedJob);
    }

    @Transactional
    public JobResponse updateJob(String email, Long jobId, JobRequest request) {
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy việc làm"));

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

    // --- HÀM HỖ TRỢ MAP DỮ LIỆU ---
    private void mapRequestToEntity(JobRequest request, JobEntity job, JobCategoryEntity category) {
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setThumbnail(request.getThumbnail());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setLocation(request.getLocation());
        job.setEmploymentType(request.getEmploymentType());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setCategory(category);

        // ĐÃ SỬA: Ép kiểu LocalDate thành LocalDateTime (Cuối ngày)
        if (request.getDeadline() != null) {
            job.setDeadline(request.getDeadline());
        }

        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
            Set<SkillEntity> skills = skillRepository.findByIdIn(request.getSkillIds());
            job.setSkills(skills);
        } else {
            job.setSkills(new HashSet<>());
        }
    }

    @Transactional(readOnly = true)
    public Page<JobResponse> getFilteredJobs(String keyword, String type, String level, String location, Double salary,
            Pageable pageable) {
        Specification<JobEntity> spec = JobSpecification.filterJobs(keyword, type, level, location, salary);
        Page<JobEntity> jobPage = jobRepository.findAll(spec, pageable);
        return jobPage.map(jobMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFilterOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("locations", jobRepository.findDistinctLocations());
        options.put("employmentTypes", jobRepository.findDistinctEmploymentTypes());
        options.put("experienceLevels", jobRepository.findDistinctExperienceLevels());
        return options;
    }

    // --- LUỒNG XỬ LÝ ẢNH THUMBNAIL ---
    public void uploadJobThumbnail(String email, Long jobId, MultipartFile file) {
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc"));

        if (!job.getCompany().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa bài đăng này");
        }

        if (file != null && !file.isEmpty()) {
            // Đẩy lên Cloudinary vào thư mục "jobs"
            String fileUrl = cloudinaryService.uploadFile(file, "jobs");

            job.setThumbnail(fileUrl);
            jobRepository.save(job);
        }
    }
}