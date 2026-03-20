package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    // Rất quan trọng để tìm lại đơn hàng khi VNPAY gọi IPN
    Optional<PaymentEntity> findByTxnRef(String txnRef);

    // Thêm hàm này: Lấy danh sách thanh toán của 1 User, mới nhất lên đầu
    Page<PaymentEntity> findByEmployer_Email(String email, Pageable pageable);
}
