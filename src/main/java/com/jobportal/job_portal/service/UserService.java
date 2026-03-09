package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.ChangePasswordRequest;
import com.jobportal.job_portal.dto.UserResponse;
import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.exception.ApiException;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.UserMapper;
import com.jobportal.job_portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    // --- LUỒNG CÁ NHÂN (Ai đăng nhập cũng dùng được) ---

    // 1. Lấy thông tin tài khoản đang đăng nhập (Get Me)
    public UserResponse getMyProfile(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));
        return userMapper.toResponse(user);
    }

    // 2. Đổi mật khẩu
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        // Kiểm tra mật khẩu cũ có khớp không
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ApiException("Mật khẩu cũ không chính xác!");
        }

        // Kiểm tra mật khẩu mới có trùng mật khẩu cũ không
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ApiException("Mật khẩu mới không được trùng với mật khẩu cũ!");
        }

        // Mã hóa và lưu mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Tài khoản {} vừa đổi mật khẩu thành công", email);
    }

    // --- LUỒNG QUẢN TRỊ (Chỉ dành cho ADMIN) ---

    // 3. Lấy danh sách toàn bộ User
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    // 4. Khóa / Mở khóa tài khoản (Ban/Unban)
    @Transactional
    public UserResponse toggleUserStatus(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Đảo ngược trạng thái hiện tại (Đang true thành false, đang false thành true)
        user.setIsActive(!user.getIsActive());

        UserEntity updatedUser = userRepository.save(user);
        log.info("Admin đã chuyển trạng thái tài khoản ID {} thành {}", userId, updatedUser.getIsActive());

        return userMapper.toResponse(updatedUser);
    }
}