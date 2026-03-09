package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.SkillRequest;
import com.jobportal.job_portal.dto.SkillResponse;
import com.jobportal.job_portal.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    // API Public: Ai cũng có thể lấy danh sách kỹ năng
    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getAllSkills() {
        return ResponseEntity
                .ok(new ApiResponse<>(true, "Lấy danh sách kỹ năng thành công", skillService.getAllSkills()));
    }

    // Các API dưới đây thực tế nên được cấu hình chỉ cho Role ADMIN trong
    // SecurityConfig
    @PostMapping
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(@Valid @RequestBody SkillRequest request) {
        SkillResponse response = skillService.createSkill(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo kỹ năng thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillRequest request) {
        SkillResponse response = skillService.updateSkill(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật kỹ năng thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa kỹ năng thành công", null));
    }
}