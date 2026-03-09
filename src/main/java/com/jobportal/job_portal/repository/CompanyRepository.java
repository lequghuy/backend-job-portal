package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    // Tìm công ty dựa vào email của tài khoản Employer đang đăng nhập
    Optional<CompanyEntity> findByUser_Email(String email);
}