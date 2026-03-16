package com.jobportal.job_portal.service;

import com.jobportal.job_portal.entity.PermissionEntity;
import com.jobportal.job_portal.entity.RoleEntity;
import com.jobportal.job_portal.entity.RolePermissionEntity;
import com.jobportal.job_portal.entity.RolePermissionId;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.repository.PermissionRepository;
import com.jobportal.job_portal.repository.RolePermissionRepository;
import com.jobportal.job_portal.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    // Lấy toàn bộ Role và kèm theo danh sách ID các quyền của Role đó
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllRolesWithPermissions() {
        return roleRepository.findAll().stream().map(role -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", role.getId());
            map.put("name", role.getName());

            List<Long> permissionIds = role.getRolePermissions().stream()
                    .map(rp -> rp.getPermission().getId())
                    .collect(Collectors.toList());
            map.put("permissionIds", permissionIds);

            return map;
        }).collect(Collectors.toList());
    }

    // Lấy toàn bộ danh sách Quyền đang có trong hệ thống
    @Transactional(readOnly = true)
    public List<PermissionEntity> getAllPermissions() {
        return permissionRepository.findAll();
    }

    // Cập nhật quyền cho một Role
    @Transactional
    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Role"));

        // 1. Xóa toàn bộ quyền cũ của Role này
        rolePermissionRepository.deleteByRoleId(roleId);

        // 2. Thêm lại các quyền mới được tick chọn
        for (Long pId : permissionIds) {
            PermissionEntity permission = permissionRepository.findById(pId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Permission"));

            RolePermissionId id = new RolePermissionId(roleId, pId);
            RolePermissionEntity rp = new RolePermissionEntity(id, role, permission);
            rolePermissionRepository.save(rp);
        }
    }

    // Thêm hàm này vào dưới cùng của AdminRoleService
    @Transactional
    public void createRole(String roleName) {
        String cleanName = roleName.trim().toUpperCase(); // Ép viết hoa

        if (roleRepository.findByName(cleanName).isPresent()) {
            throw new com.jobportal.job_portal.exception.ApiException("Nhóm quyền này đã tồn tại!");
        }

        RoleEntity newRole = new RoleEntity();
        newRole.setName(cleanName);
        roleRepository.save(newRole);
    }

    // 1. SỬA TÊN ROLE
    @Transactional
    public void updateRoleName(Long roleId, String newName) {
        String cleanName = newName.trim().toUpperCase();

        // Kiểm tra xem tên mới đã bị ai dùng chưa
        if (roleRepository.findByName(cleanName).isPresent()) {
            throw new com.jobportal.job_portal.exception.ApiException("Tên nhóm quyền này đã tồn tại!");
        }

        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Role"));

        // Khóa việc sửa tên các Role gốc của hệ thống (RẤT QUAN TRỌNG)
        if (role.getName().equals("ADMIN") || role.getName().equals("EMPLOYER") || role.getName().equals("CANDIDATE")) {
            throw new com.jobportal.job_portal.exception.ApiException(
                    "Không được phép sửa tên nhóm quyền hệ thống mặc định!");
        }

        role.setName(cleanName);
        roleRepository.save(role);
    }

    // 2. XÓA ROLE
    @Transactional
    public void deleteRole(Long roleId) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Role"));

        // Khóa việc xóa các Role gốc
        if (role.getName().equals("ADMIN") || role.getName().equals("EMPLOYER") || role.getName().equals("CANDIDATE")) {
            throw new com.jobportal.job_portal.exception.ApiException(
                    "Không được phép xóa nhóm quyền hệ thống mặc định!");
        }

        // Ràng buộc toàn vẹn: Không cho xóa nếu đang có User mang chức vụ này
        if (!role.getUsers().isEmpty()) {
            throw new com.jobportal.job_portal.exception.ApiException(
                    "Không thể xóa! Đang có " + role.getUsers().size() + " người dùng thuộc nhóm này.");
        }

        // Phải xóa các quyền của Role này trong bảng trung gian trước khi xóa Role (nếu
        // không sẽ bị lỗi khóa ngoại)
        rolePermissionRepository.deleteByRoleId(roleId);

        // Xóa Role
        roleRepository.delete(role);
    }
}