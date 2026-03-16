package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // --- THÊM 2 DÒNG NÀY ĐỂ TRÁNH LỖI VÒNG LẶP ---
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "role")
    private Set<UserEntity> users;

    // --- THÊM 2 DÒNG NÀY ĐỂ TRÁNH LỖI VÒNG LẶP ---
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    private Set<RolePermissionEntity> rolePermissions;
}