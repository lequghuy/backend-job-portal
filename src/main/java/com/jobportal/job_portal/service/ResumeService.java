package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.ResumeRequest;
import com.jobportal.job_portal.dto.ResumeResponse;
import com.jobportal.job_portal.entity.ResumeEntity;
import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.exception.ApiException;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.ResumeMapper;
import com.jobportal.job_portal.repository.ResumeRepository;
import com.jobportal.job_portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeMapper resumeMapper;
    private final CloudinaryService cloudinaryService;

    // 1. Thêm CV mới
    @Transactional
    public ResumeResponse addResume(String email, ResumeRequest request) {
        UserEntity candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản ứng viên"));

        // Nếu đây là CV đầu tiên, tự động đặt làm mặc định
        List<ResumeEntity> existingResumes = resumeRepository.findByCandidate_Email(email);
        boolean isFirstResume = existingResumes.isEmpty();

        ResumeEntity newResume = new ResumeEntity();
        newResume.setCandidate(candidate);
        newResume.setResumeName(request.getResumeName());
        newResume.setFileUrl(request.getFileUrl());
        newResume.setIsDefault(isFirstResume);

        ResumeEntity savedResume = resumeRepository.save(newResume);
        log.info("Thêm mới CV thành công cho ứng viên: {}", email);

        return resumeMapper.toResponse(savedResume);
    }

    // 2. Lấy danh sách CV của tôi
    public List<ResumeResponse> getMyResumes(String email) {
        List<ResumeEntity> resumes = resumeRepository.findByCandidate_Email(email);
        return resumeMapper.toResponseList(resumes);
    }

    // 3. Đặt CV làm mặc định
    @Transactional
    public void setDefaultResume(String email, Long resumeId) {
        List<ResumeEntity> myResumes = resumeRepository.findByCandidate_Email(email);

        ResumeEntity targetResume = myResumes.stream()
                .filter(r -> r.getId().equals(resumeId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CV này của bạn"));

        // Gỡ mặc định của tất cả CV, sau đó set CV được chọn thành true
        myResumes.forEach(r -> r.setIsDefault(false));
        targetResume.setIsDefault(true);

        resumeRepository.saveAll(myResumes);
        log.info("Đã đặt CV id {} làm mặc định cho ứng viên {}", resumeId, email);
    }

    // 4. Xóa CV
    @Transactional
    public void deleteResume(String email, Long resumeId) {
        ResumeEntity targetResume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy CV"));

        // Chặn xóa nếu không phải CV của mình
        if (!targetResume.getCandidate().getEmail().equals(email)) {
            throw new ApiException("Bạn không có quyền xóa CV này");
        }

        // Chặn xóa nếu đang là CV mặc định (Bắt buộc phải set CV khác làm mặc định rồi
        // mới được xóa)
        if (targetResume.getIsDefault()) {
            throw new ApiException("Không thể xóa CV đang được đặt làm mặc định");
        }

        resumeRepository.delete(targetResume);
        log.info("Đã xóa CV id {} của ứng viên {}", resumeId, email);
    }

    // HÀM MỚI: Upload file và lưu thẳng vào Database
    @Transactional
    public ResumeResponse uploadAndAddResume(String email, MultipartFile file, String resumeName) {
        UserEntity candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản ứng viên"));

        // 1. Đẩy CV (PDF) lên Cloudinary vào thư mục "resumes"
        String fileUrl = cloudinaryService.uploadFile(file, "resumes");

        // 2. Kiểm tra xem có phải CV đầu tiên không để set Default
        List<ResumeEntity> existingResumes = resumeRepository.findByCandidate_Email(email);
        boolean isFirstResume = existingResumes.isEmpty();

        // 3. Tạo Entity và lưu vào DB
        ResumeEntity newResume = new ResumeEntity();
        newResume.setCandidate(candidate);
        newResume.setResumeName(resumeName);
        newResume.setFileUrl(fileUrl); // Lưu đường link Cloudinary thẳng vào DB
        newResume.setIsDefault(isFirstResume);

        ResumeEntity savedResume = resumeRepository.save(newResume);
        log.info("Upload CV thành công cho ứng viên: {}", email);

        return resumeMapper.toResponse(savedResume);
    }
}