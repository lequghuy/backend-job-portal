package com.jobportal.job_portal.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionId implements Serializable {

    private Long roleId;

    private Long permissionId;
}
