package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.CandidateProfileRequest;
import com.jobportal.job_portal.dto.CandidateProfileResponse;
import com.jobportal.job_portal.entity.CandidateProfileEntity;
import com.jobportal.job_portal.entity.SkillEntity;
import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.CandidateProfileMapper;
import com.jobportal.job_portal.repository.CandidateProfileRepository;
import com.jobportal.job_portal.repository.SkillRepository;
import com.jobportal.job_portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateProfileService {

    private final CandidateProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final CandidateProfileMapper profileMapper;

    // Xem hồ sơ của chính mình
    public CandidateProfileResponse getMyProfile(String email) {
        CandidateProfileEntity profile = profileRepository.findByUser_Email(email)
                .orElse(null);

        // NẾU CHƯA CÓ HỒ SƠ -> Trả về đối tượng rỗng thay vì báo lỗi 404
        if (profile == null) {
            CandidateProfileResponse emptyProfile = new CandidateProfileResponse();
            emptyProfile.setSkills(new java.util.HashSet<>()); // Khởi tạo mảng rỗng để tránh lỗi Frontend
            return emptyProfile;
        }

        return profileMapper.toResponse(profile);
    }

    // Tạo mới hoặc Cập nhật hồ sơ
    @Transactional
    public CandidateProfileResponse updateMyProfile(String email, CandidateProfileRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Tìm hồ sơ cũ, nếu chưa có thì tạo mới
        CandidateProfileEntity profile = profileRepository.findByUser_Email(email)
                .orElse(new CandidateProfileEntity());

        profile.setUser(user);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setExperience(request.getExperience());
        profile.setEducation(request.getEducation());

        // Cập nhật danh sách kỹ năng
        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
            Set<SkillEntity> skills = skillRepository.findByIdIn(request.getSkillIds());
            profile.setSkills(skills);
        } else {
            profile.setSkills(new HashSet<>()); // Xóa hết kỹ năng nếu truyền list rỗng
        }

        CandidateProfileEntity savedProfile = profileRepository.save(profile);
        log.info("Cập nhật hồ sơ thành công cho ứng viên: {}", email);

        return profileMapper.toResponse(savedProfile);
    }
}