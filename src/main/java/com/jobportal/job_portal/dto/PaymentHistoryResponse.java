package com.jobportal.job_portal.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder // BẮT BUỘC có cái này để dùng .builder()
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponse {
    private Long id;
    private String planName;
    private Double amount;
    private String txnRef;
    private String paymentStatus;
    private String bankCode;
    private LocalDateTime createdAt;
    private String employerEmail;
}