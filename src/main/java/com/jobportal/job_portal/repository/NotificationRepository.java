package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    // Tìm tất cả thông báo của 1 user (thông qua email) và sắp xếp mới nhất lên đầu
    List<NotificationEntity> findByUser_EmailOrderByCreatedAtDesc(String email);
}