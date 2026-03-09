package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.JobCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategoryEntity, Long> {
    // Kiểm tra xem tên danh mục đã tồn tại chưa
    boolean existsByName(String name);
}