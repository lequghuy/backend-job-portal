package com.jobportal.job_portal.service;

import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String email)
                        throws UsernameNotFoundException {

                UserEntity user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                return User.builder()
                                .username(user.getEmail())
                                .password(user.getPassword())
                                .authorities(user.getRole().getName())
                                .build();
        }
}