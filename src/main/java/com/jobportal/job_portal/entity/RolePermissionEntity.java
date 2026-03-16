package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "role_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionEntity {

    @EmbeddedId
    private RolePermissionId id;

    // --- THÊM 2 DÒNG NÀY ---
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    // --- THÊM 2 DÒNG NÀY ---
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private PermissionEntity permission;
}