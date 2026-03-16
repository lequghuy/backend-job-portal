package com.jobportal.job_portal.service;

import com.jobportal.job_portal.entity.RolePermissionEntity;
import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

                UserEntity user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found with email: " + email));

                List<GrantedAuthority> authorities = new ArrayList<>();

                // 1. Giữ lại Role gốc (có tiền tố ROLE_ để dự phòng)
                authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

                // 2. Quét và nhồi toàn bộ Quyền hạn (Permissions) vào
                if (user.getRole().getRolePermissions() != null) {
                        for (RolePermissionEntity rp : user.getRole().getRolePermissions()) {
                                authorities.add(new SimpleGrantedAuthority(rp.getPermission().getName()));
                        }
                }

                return User.builder()
                                .username(user.getEmail())
                                .password(user.getPassword())
                                .authorities(authorities)
                                .build();
        }
}