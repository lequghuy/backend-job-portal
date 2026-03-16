package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "employer_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployerSubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "employer_id")
    private UserEntity employer;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlanEntity plan;

    // Đổi sang LocalDateTime để tính hạn chính xác từng giây
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // --- TRƯỜNG THÊM MỚI ---
    // Snapshot quyền lợi tại thời điểm mua. Tránh lỗi khi Admin sửa Plan.
    private Integer maxJobs;
    // ----------------------

    private Integer jobsPosted = 0; // Khởi tạo mặc định là 0

    private String status; // ACTIVE, EXPIRED, PENDING
}