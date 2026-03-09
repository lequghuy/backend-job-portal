package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.SkillRequest;
import com.jobportal.job_portal.dto.SkillResponse;
import com.jobportal.job_portal.entity.SkillEntity;
import com.jobportal.job_portal.exception.ApiException;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.SkillMapper;
import com.jobportal.job_portal.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    // 1. Dành cho Public: Lấy tất cả kỹ năng
    public List<SkillResponse> getAllSkills() {
        return skillMapper.toResponseList(skillRepository.findAll());
    }

    // 2. Dành cho Admin: Thêm kỹ năng mới
    public SkillResponse createSkill(SkillRequest request) {
        if (skillRepository.existsByName(request.getName())) {
            throw new ApiException("Kỹ năng này đã tồn tại trong hệ thống");
        }

        SkillEntity skill = new SkillEntity();
        skill.setName(request.getName());

        SkillEntity savedSkill = skillRepository.save(skill);
        log.info("Đã tạo mới kỹ năng: {}", savedSkill.getName());
        return skillMapper.toResponse(savedSkill);
    }

    // 3. Dành cho Admin: Cập nhật tên kỹ năng
    public SkillResponse updateSkill(Long id, SkillRequest request) {
        SkillEntity skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kỹ năng"));

        // Kiểm tra xem tên mới có bị trùng với kỹ năng khác không
        if (!skill.getName().equalsIgnoreCase(request.getName()) && skillRepository.existsByName(request.getName())) {
            throw new ApiException("Tên kỹ năng này đã được sử dụng");
        }

        skill.setName(request.getName());
        return skillMapper.toResponse(skillRepository.save(skill));
    }

    // 4. Dành cho Admin: Xóa kỹ năng
    public void deleteSkill(Long id) {
        SkillEntity skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kỹ năng"));

        skillRepository.delete(skill);
        log.info("Đã xóa kỹ năng có ID: {}", id);
    }
}