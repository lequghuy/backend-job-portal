package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.*;
import com.jobportal.job_portal.entity.*;
import com.jobportal.job_portal.exception.*;
import com.jobportal.job_portal.mapper.UserMapper;
import com.jobportal.job_portal.repository.*;
import com.jobportal.job_portal.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    // --- HÀM HỖ TRỢ BÓC TÁCH QUYỀN TỪ USER ---
    private List<String> extractPermissions(UserEntity user) {
        List<String> permissions = new ArrayList<>();
        if (user.getRole() != null && user.getRole().getRolePermissions() != null) {
            for (RolePermissionEntity rp : user.getRole().getRolePermissions()) {
                permissions.add(rp.getPermission().getName());
            }
        }
        return permissions;
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Bắt đầu đăng ký user với email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email đã tồn tại trong hệ thống");
        }

        RoleEntity role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role không hợp lệ"));

        UserEntity user = userMapper.toEntity(request, role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        UserEntity savedUser = userRepository.save(user);

        // Lấy danh sách quyền
        List<String> permissions = extractPermissions(savedUser);

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(savedUser.getEmail()))
                .refreshToken(jwtService.generateRefreshToken(savedUser.getEmail()))
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .avatarUrl(savedUser.getAvatarUrl())
                .role(savedUser.getRole().getName())
                .permissions(permissions) // TRẢ VỀ FRONTEND
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new InvalidPasswordException("Email hoặc mật khẩu không chính xác");
        }

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));

        if (!user.getIsActive()) {
            throw new ApiException("Tài khoản của bạn đã bị vô hiệu hóa");
        }

        // Lấy danh sách quyền
        List<String> permissions = extractPermissions(user);

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user.getEmail()))
                .refreshToken(jwtService.generateRefreshToken(user.getEmail()))
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().getName())
                .permissions(permissions) // TRẢ VỀ FRONTEND
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        // ... (Giữ nguyên logic cũ của refreshToken)
        String requestRefreshToken = request.getRefreshToken();
        String userEmail;

        try {
            userEmail = jwtService.extractUsername(requestRefreshToken);
        } catch (Exception e) {
            throw new ApiException("Refresh Token không hợp lệ");
        }

        if (userEmail != null) {
            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));

            if (jwtService.isTokenValid(requestRefreshToken, userEmail)) {
                String newAccessToken = jwtService.generateToken(userEmail);
                String newRefreshToken = jwtService.generateRefreshToken(userEmail);

                // Khi refresh token, ta không cần trả lại full info (hoặc trả full cũng được
                // tùy thiết kế)
                return AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            } else {
                throw new ApiException("Refresh Token đã hết hạn, vui lòng đăng nhập lại");
            }
        }
        throw new ApiException("Không thể xác thực Refresh Token");
    }
}