package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, Long> {
    // Phục vụ cho việc update/create Profile hoặc Job
    Set<SkillEntity> findByIdIn(Set<Long> ids);

    // Kiểm tra xem kỹ năng đã tồn tại chưa (tránh tạo trùng)
    boolean existsByName(String name);
}