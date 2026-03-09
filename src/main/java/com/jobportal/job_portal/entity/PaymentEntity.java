package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private UserEntity employer;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private EmployerSubscriptionEntity subscription;

    private Double amount;

    private String paymentMethod;

    private String paymentStatus;

    private LocalDateTime createdAt;
}