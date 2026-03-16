package com.jobportal.job_portal.config;

import com.jobportal.job_portal.entity.*;
import com.jobportal.job_portal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Đang kiểm tra và khởi tạo dữ liệu Roles & Permissions...");

        // 1. TẠO CÁC ROLE CƠ BẢN
        RoleEntity adminRole = createRoleIfNotFound("ADMIN");
        RoleEntity employerRole = createRoleIfNotFound("EMPLOYER");
        RoleEntity candidateRole = createRoleIfNotFound("CANDIDATE");

        // 2. TẠO CÁC PERMISSION CHI TIẾT
        // Quyền của Admin
        PermissionEntity pManageUsers = createPermissionIfNotFound("MANAGE_USERS");
        PermissionEntity pManageCompanies = createPermissionIfNotFound("MANAGE_COMPANIES");
        PermissionEntity pManageCategories = createPermissionIfNotFound("MANAGE_CATEGORIES");
        PermissionEntity pManageSkills = createPermissionIfNotFound("MANAGE_SKILLS");

        // Quyền của Employer
        PermissionEntity pManageOwnCompany = createPermissionIfNotFound("MANAGE_OWN_COMPANY");
        PermissionEntity pManageOwnJobs = createPermissionIfNotFound("MANAGE_OWN_JOBS");
        PermissionEntity pManageApplications = createPermissionIfNotFound("MANAGE_APPLICATIONS");

        // Quyền của Candidate
        PermissionEntity pManageOwnProfile = createPermissionIfNotFound("MANAGE_OWN_PROFILE");
        PermissionEntity pApplyJobs = createPermissionIfNotFound("APPLY_JOBS");
        PermissionEntity pManageResumes = createPermissionIfNotFound("MANAGE_RESUMES");

        // 3. GẮN QUYỀN CHO ADMIN
        assignPermissionToRole(adminRole, pManageUsers);
        assignPermissionToRole(adminRole, pManageCompanies);
        assignPermissionToRole(adminRole, pManageCategories);
        assignPermissionToRole(adminRole, pManageSkills);

        // 4. GẮN QUYỀN CHO EMPLOYER
        assignPermissionToRole(employerRole, pManageOwnCompany);
        assignPermissionToRole(employerRole, pManageOwnJobs);
        assignPermissionToRole(employerRole, pManageApplications);

        // 5. GẮN QUYỀN CHO CANDIDATE
        assignPermissionToRole(candidateRole, pManageOwnProfile);
        assignPermissionToRole(candidateRole, pApplyJobs);
        assignPermissionToRole(candidateRole, pManageResumes);

        log.info("Hoàn tất nạp dữ liệu Roles & Permissions!");
    }

    private RoleEntity createRoleIfNotFound(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            RoleEntity role = new RoleEntity();
            role.setName(name);
            return roleRepository.save(role);
        });
    }

    private PermissionEntity createPermissionIfNotFound(String name) {
        return permissionRepository.findByName(name).orElseGet(() -> {
            PermissionEntity permission = new PermissionEntity();
            permission.setName(name);
            return permissionRepository.save(permission);
        });
    }

    private void assignPermissionToRole(RoleEntity role, PermissionEntity permission) {
        RolePermissionId id = new RolePermissionId(role.getId(), permission.getId());
        if (!rolePermissionRepository.existsById(id)) {
            RolePermissionEntity rp = new RolePermissionEntity(id, role, permission);
            rolePermissionRepository.save(rp);
        }
    }
}