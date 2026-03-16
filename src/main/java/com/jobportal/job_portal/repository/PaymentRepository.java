package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    // Rất quan trọng để tìm lại đơn hàng khi VNPAY gọi IPN
    Optional<PaymentEntity> findByTxnRef(String txnRef);
}
