package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "employer_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployerSubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private UserEntity employer;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlanEntity plan;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer jobsPosted;

    private String status;
}
