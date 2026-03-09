package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionEntity {

    @EmbeddedId
    private RolePermissionId id;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @ManyToOne
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private PermissionEntity permission;
}