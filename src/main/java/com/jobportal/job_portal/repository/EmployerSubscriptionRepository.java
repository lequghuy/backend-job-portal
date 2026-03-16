package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.EmployerSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EmployerSubscriptionRepository extends JpaRepository<EmployerSubscriptionEntity, Long> {

    // Hàm này rất hữu ích cho sau này: Dùng để kiểm tra xem Nhà tuyển dụng
    // có gói dịch vụ nào đang "ACTIVE" (còn hạn) để cho phép họ đăng bài hay không.
    Optional<EmployerSubscriptionEntity> findFirstByEmployer_EmailAndStatusOrderByIdDesc(String email, String status);

    // Lấy danh sách toàn bộ lịch sử mua gói của một Nhà tuyển dụng
    List<EmployerSubscriptionEntity> findByEmployer_EmailOrderByStartDateDesc(String email);

    @Modifying
    @Query("UPDATE EmployerSubscriptionEntity s SET s.status = 'EXPIRED' WHERE s.status = 'ACTIVE' AND s.endDate < CURRENT_TIMESTAMP")
    int updateExpiredSubscriptions();
}