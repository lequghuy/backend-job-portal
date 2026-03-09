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

@Service
@RequiredArgsConstructor
@Slf4j // Thêm logging
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager; // Dùng Auth Manager chuẩn
    private final UserMapper userMapper;

    public AuthResponse register(RegisterRequest request) {
        log.info("Bắt đầu đăng ký user với email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email đã tồn tại: {}", request.getEmail());
            throw new UserAlreadyExistsException("Email đã tồn tại trong hệ thống");
        }

        RoleEntity role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role không hợp lệ"));

        UserEntity user = userMapper.toEntity(request, role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        log.info("Đăng ký thành công cho user: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user.getEmail()))
                .refreshToken(jwtService.generateRefreshToken(user.getEmail()))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Bắt đầu xác thực cho email: {}", request.getEmail());

        try {
            // Spring Security sẽ tự động mã hóa password truyền vào và so sánh với DB
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            // Bắt lỗi khi sai Email hoặc sai Mật khẩu
            log.warn("Đăng nhập thất bại: Sai email hoặc mật khẩu cho tài khoản {}", request.getEmail());
            throw new InvalidPasswordException("Email hoặc mật khẩu không chính xác");
        }

        // Lúc này chắc chắn đăng nhập đúng rồi thì mới chạy xuống đây
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));

        if (!user.getIsActive()) {
            log.warn("Tài khoản đã bị vô hiệu hóa: {}", request.getEmail());
            throw new ApiException("Tài khoản của bạn đã bị vô hiệu hóa");
        }

        log.info("Đăng nhập thành công cho user: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user.getEmail()))
                .refreshToken(jwtService.generateRefreshToken(user.getEmail()))
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Bắt đầu xử lý cấp lại token...");

        String requestRefreshToken = request.getRefreshToken();
        String userEmail;

        try {
            // Lấy email từ Refresh Token
            userEmail = jwtService.extractUsername(requestRefreshToken);
        } catch (Exception e) {
            log.warn("Refresh Token không hợp lệ hoặc đã bị lỗi can thiệp");
            throw new ApiException("Refresh Token không hợp lệ");
        }

        if (userEmail != null) {
            // Kiểm tra user có tồn tại trong hệ thống không
            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));

            // Kiểm tra token có đúng của user này và còn hạn hay không
            if (jwtService.isTokenValid(requestRefreshToken, userEmail)) {

                // Cấp lại cặp Token mới tinh (Token Rotation)
                String newAccessToken = jwtService.generateToken(userEmail);
                String newRefreshToken = jwtService.generateRefreshToken(userEmail);

                log.info("Cấp lại Token thành công cho user: {}", userEmail);

                return AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            } else {
                log.warn("Refresh Token đã hết hạn cho user: {}", userEmail);
                throw new ApiException("Refresh Token đã hết hạn, vui lòng đăng nhập lại");
            }
        }

        throw new ApiException("Không thể xác thực Refresh Token");
    }
}