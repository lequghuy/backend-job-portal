package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long>, JpaSpecificationExecutor<CompanyEntity> {
    // Tìm công ty dựa vào email của tài khoản Employer đang đăng nhập
    Optional<CompanyEntity> findByUser_Email(String email);

    // Thêm hàm này vào interface CompanyRepository
    @Query("SELECT DISTINCT c.location FROM CompanyEntity c WHERE c.location IS NOT NULL AND c.location != ''")
    List<String> findDistinctLocations();
}