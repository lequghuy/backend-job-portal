package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByName(String name);
}