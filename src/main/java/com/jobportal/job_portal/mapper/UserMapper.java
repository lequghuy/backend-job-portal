package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.RegisterRequest;
import com.jobportal.job_portal.dto.UserResponse;
import com.jobportal.job_portal.entity.RoleEntity;
import com.jobportal.job_portal.entity.UserEntity;
//dung thu vien mapstruct de tu dong map giua DTO va Entity, giam thieu code boilerplate
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Lấy tên Role từ RoleEntity lồng bên trong
    @Mapping(target = "roleName", source = "role.name")
    UserResponse toResponse(UserEntity entity);

    List<UserResponse> toResponseList(List<UserEntity> entities);

    // Hàm nhận dữ liệu từ Frontend (RegisterRequest) và RoleEntity (truy vấn từ DB)
    // để gộp lại thành UserEntity
    @Mapping(target = "id", ignore = true) // Bỏ qua ID vì ID tự tăng
    @Mapping(target = "isActive", constant = "true") // Mặc định tài khoản mới tạo là active
    @Mapping(target = "password", ignore = true) // Bỏ qua password ở đây, mã hóa bên AuthService sau
    @Mapping(target = "role", source = "role") // Lấy biến 'role' truyền vào gán vào Entity
    UserEntity toEntity(RegisterRequest request, RoleEntity role);
}