package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {
    // Lấy danh sách việc làm của một công ty cụ thể (Dành cho Employer)
    List<JobEntity> findByCompany_User_Email(String email);

    // Lấy danh sách việc làm theo trạng thái (Dành cho Public để chỉ hiển thị Job
    // đang "OPEN")
    List<JobEntity> findByStatusOrderByCreatedAtDesc(String status);
}