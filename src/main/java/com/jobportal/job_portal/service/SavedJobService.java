package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.SavedJobRequest;
import com.jobportal.job_portal.dto.SavedJobResponse;
import com.jobportal.job_portal.entity.JobEntity;
import com.jobportal.job_portal.entity.SavedJobEntity;
import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.exception.ApiException;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.SavedJobMapper;
import com.jobportal.job_portal.repository.JobRepository;
import com.jobportal.job_portal.repository.SavedJobRepository;
import com.jobportal.job_portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final SavedJobMapper savedJobMapper;

    @Transactional
    public SavedJobResponse saveJob(String email, SavedJobRequest request) {
        if (savedJobRepository.existsByJob_IdAndCandidate_Email(request.getJobId(), email)) {
            throw new ApiException("Bạn đã lưu công việc này rồi!");
        }

        UserEntity candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ứng viên"));

        JobEntity job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc"));

        SavedJobEntity savedJob = new SavedJobEntity();
        savedJob.setCandidate(candidate);
        savedJob.setJob(job);
        savedJob.setSavedAt(LocalDateTime.now()); // Entity của bạn không dùng @CreationTimestamp nên ta set tay

        SavedJobEntity result = savedJobRepository.save(savedJob);
        log.info("Ứng viên {} đã lưu việc làm ID: {}", email, request.getJobId());

        return savedJobMapper.toResponse(result);
    }

    public List<SavedJobResponse> getMySavedJobs(String email) {
        List<SavedJobEntity> savedJobs = savedJobRepository.findByCandidate_EmailOrderBySavedAtDesc(email);
        return savedJobMapper.toResponseList(savedJobs);
    }

    @Transactional
    public void unsaveJob(String email, Long jobId) {
        SavedJobEntity savedJob = savedJobRepository.findByJob_IdAndCandidate_Email(jobId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Bạn chưa lưu công việc này"));

        savedJobRepository.delete(savedJob);
        log.info("Ứng viên {} đã bỏ lưu việc làm ID: {}", email, jobId);
    }
}