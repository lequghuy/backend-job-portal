package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.ChangePasswordRequest;
import com.jobportal.job_portal.dto.UserResponse;
import com.jobportal.job_portal.dto.UserUpdateRequest;
import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.exception.ApiException;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.UserMapper;
import com.jobportal.job_portal.repository.RoleRepository;
import com.jobportal.job_portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

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

    @Transactional
    public UserResponse updateMyProfile(String currentEmail, UserUpdateRequest request) {
        UserEntity user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        // Chỉ cho phép đổi Họ và Tên
        user.setFullName(request.getFullName());

        UserEntity updatedUser = userRepository.save(user);
        log.info("Tài khoản {} vừa cập nhật Họ tên thành {}", currentEmail, request.getFullName());

        return userMapper.toResponse(updatedUser);
    }
    // --- LUỒNG QUẢN TRỊ (Chỉ dành cho ADMIN) ---

    // 3. Lấy danh sách toàn bộ User
    public Page<UserResponse> getAllUsers(int page, int size) {
        // Sắp xếp ID giảm dần để User mới đăng ký lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<UserEntity> userPage = userRepository.findAll(pageable);

        // Map từ Page<UserEntity> sang Page<UserResponse>
        return userPage.map(userMapper::toResponse);
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

    @Transactional
    public String updateAvatar(String email, MultipartFile file) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

        if (file.isEmpty()) {
            throw new ApiException("Vui lòng chọn ảnh");
        }

        try {
            // Tạo thư mục uploads/avatars nếu chưa có
            Path uploadPath = Paths.get("uploads/avatars").toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Đổi tên file để không bị trùng
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Lưu file
            Path targetLocation = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Lưu tên file vào Database
            String fileUrl = "/uploads/avatars/" + newFileName;
            user.setAvatarUrl(fileUrl);
            userRepository.save(user);

            log.info("Tài khoản {} vừa cập nhật avatar", email);
            return fileUrl;

        } catch (Exception ex) {
            log.error("Lỗi lưu ảnh: ", ex);
            throw new ApiException("Không thể lưu ảnh đại diện. Vui lòng thử lại!");
        }
    }

    // Thêm hàm này: Tạo tài khoản từ trang Admin
    @Transactional
    public UserResponse createUserByAdmin(com.jobportal.job_portal.dto.CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email này đã tồn tại trong hệ thống!");
        }

        // Tìm Role theo tên (đã ép viết hoa)
        com.jobportal.job_portal.entity.RoleEntity role = roleRepository.findByName(request.getRoleName().toUpperCase())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy nhóm quyền: " + request.getRoleName()));

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Mã hóa mật khẩu
        user.setFullName(request.getFullName());
        user.setRole(role);
        user.setIsActive(true); // Tài khoản tạo ra mặc định kích hoạt luôn

        UserEntity savedUser = userRepository.save(user);
        log.info("Admin vừa tạo tài khoản mới: {} với chức vụ: {}", savedUser.getEmail(), role.getName());

        return userMapper.toResponse(savedUser);
    }
}