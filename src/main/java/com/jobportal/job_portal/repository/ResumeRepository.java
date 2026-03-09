package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {
    // Tìm danh sách tất cả CV của một ứng viên dựa vào email đăng nhập
    List<ResumeEntity> findByCandidate_Email(String email);
}