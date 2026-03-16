package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    private String paymentMethod; // Thường set cứng là "VNPAY"

    private String paymentStatus; // PENDING, SUCCESS, FAILED

    // --- CÁC TRƯỜNG THÊM MỚI CHO VNPAY ---

    @Column(unique = true)
    private String txnRef; // Mã tham chiếu giao dịch gửi sang VNPAY (vnp_TxnRef)

    private String vnpayTransactionNo; // Mã giao dịch do VNPAY trả về (vnp_TransactionNo)

    private String bankCode; // Mã ngân hàng (vnp_BankCode)

    private String orderInfo; // Nội dung thanh toán (vnp_OrderInfo)

    private String responseCode; // Mã phản hồi từ VNPAY (vnp_ResponseCode)
    // -------------------------------------

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}