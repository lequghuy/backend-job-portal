package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.JobApplicationRequest;
import com.jobportal.job_portal.dto.JobApplicationResponse;
import com.jobportal.job_portal.dto.UpdateApplicationStatusRequest;
import com.jobportal.job_portal.entity.JobApplicationEntity;
import com.jobportal.job_portal.entity.JobEntity;
import com.jobportal.job_portal.entity.ResumeEntity;
import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.exception.ApiException;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.JobApplicationMapper;
import com.jobportal.job_portal.repository.JobApplicationRepository;
import com.jobportal.job_portal.repository.JobRepository;
import com.jobportal.job_portal.repository.ResumeRepository;
import com.jobportal.job_portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobApplicationMapper applicationMapper;

    // Đã được tiêm (inject) sẵn để dùng tạo thông báo
    private final NotificationService notificationService;

    // ==========================================
    // LUỒNG DÀNH CHO CÁC ỨNG VIÊN (CANDIDATE)
    // ==========================================

    @Transactional
    public JobApplicationResponse applyForJob(String email, JobApplicationRequest request) {
        if (applicationRepository.existsByJob_IdAndCandidate_Email(request.getJobId(), email)) {
            throw new ApiException("Bạn đã ứng tuyển vào công việc này rồi!");
        }

        UserEntity candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ứng viên"));

        JobEntity job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc"));

        if (!"OPEN".equalsIgnoreCase(job.getStatus())) {
            throw new ApiException("Công việc này đã đóng, không thể ứng tuyển");
        }

        ResumeEntity resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CV"));

        if (!resume.getCandidate().getEmail().equals(email)) {
            throw new ApiException("Bạn không thể dùng CV của người khác để ứng tuyển");
        }

        JobApplicationEntity application = new JobApplicationEntity();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setResume(resume);
        application.setStatus("PENDING"); // Mặc định là chờ duyệt

        JobApplicationEntity savedApplication = applicationRepository.save(application);
        log.info("Ứng viên {} đã nộp CV vào Job {}", email, job.getTitle());

        // -----------------------------------------------------------------
        // TỰ ĐỘNG THÔNG BÁO CHO NHÀ TUYỂN DỤNG KHI CÓ ỨNG VIÊN NỘP CV
        // -----------------------------------------------------------------
        UserEntity employerUser = job.getCompany().getUser();
        String candidateName = candidate.getFullName() != null ? candidate.getFullName() : candidate.getEmail();
        String messageToEmployer = "Ứng viên " + candidateName + " vừa nộp CV ứng tuyển vào vị trí [" + job.getTitle()
                + "].";
        notificationService.createNotification(employerUser, messageToEmployer);
        // -----------------------------------------------------------------

        return applicationMapper.toResponse(savedApplication);
    }

    public List<JobApplicationResponse> getMyApplications(String email) {
        List<JobApplicationEntity> applications = applicationRepository
                .findByCandidate_EmailOrderByAppliedAtDesc(email);
        return applicationMapper.toResponseList(applications);
    }

    // ==========================================
    // LUỒNG DÀNH CHO NHÀ TUYỂN DỤNG (EMPLOYER)
    // ==========================================

    public List<JobApplicationResponse> getApplicationsForMyJob(String email, Long jobId) {
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc"));

        // Kiểm tra xem việc làm này có đúng là của Employer đang đăng nhập không
        if (!job.getCompany().getUser().getEmail().equals(email)) {
            throw new ApiException("Bạn không có quyền xem hồ sơ của công việc này");
        }

        List<JobApplicationEntity> applications = applicationRepository.findByJob_IdOrderByAppliedAtDesc(jobId);
        return applicationMapper.toResponseList(applications);
    }

    @Transactional
    public JobApplicationResponse updateApplicationStatus(String email, Long applicationId,
            UpdateApplicationStatusRequest request) {
        JobApplicationEntity application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ ứng tuyển"));

        // Kiểm tra quyền: Chỉ chủ của Job mới được đổi trạng thái
        if (!application.getJob().getCompany().getUser().getEmail().equals(email)) {
            throw new ApiException("Bạn không có quyền cập nhật hồ sơ này");
        }

        String newStatus = request.getStatus().toUpperCase();
        application.setStatus(newStatus);
        JobApplicationEntity updatedApplication = applicationRepository.save(application);

        log.info("Hồ sơ ID {} đã được chuyển sang trạng thái {}", applicationId, newStatus);

        // -----------------------------------------------------------------
        // TỰ ĐỘNG THÔNG BÁO CHO ỨNG VIÊN KHI NHÀ TUYỂN DỤNG DUYỆT/LOẠI CV
        // -----------------------------------------------------------------
        UserEntity candidateUser = application.getCandidate();

        // Dịch trạng thái sang tiếng Việt cho thân thiện
        String statusVN = newStatus;
        if (newStatus.equals("ACCEPTED"))
            statusVN = "CHẤP NHẬN";
        else if (newStatus.equals("REJECTED"))
            statusVN = "TỪ CHỐI";

        String messageToCandidate = "Hồ sơ ứng tuyển vị trí [" + application.getJob().getTitle()
                + "] của bạn đã bị/được " + statusVN + ".";
        notificationService.createNotification(candidateUser, messageToCandidate);
        // -----------------------------------------------------------------

        return applicationMapper.toResponse(updatedApplication);
    }

    // Lấy tất cả ứng viên của CÔNG TY TÔI
    public List<JobApplicationResponse> getAllCandidatesForMyCompany(String email) {
        List<JobApplicationEntity> applications = applicationRepository.findByEmployerEmail(email);
        return applicationMapper.toResponseList(applications);
    }
}