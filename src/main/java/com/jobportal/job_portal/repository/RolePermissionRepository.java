package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.RolePermissionEntity;
import com.jobportal.job_portal.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, RolePermissionId> {

    @Modifying
    @Query("DELETE FROM RolePermissionEntity rp WHERE rp.role.id = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);
}