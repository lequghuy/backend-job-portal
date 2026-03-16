package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.entity.SubscriptionPlanEntity;
import com.jobportal.job_portal.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanRepository planRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionPlanEntity>>> getAllPlans() {
        // Lấy toàn bộ gói cước trong DB ra
        List<SubscriptionPlanEntity> plans = planRepository.findAll();

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách gói cước thành công", plans));
    }
}