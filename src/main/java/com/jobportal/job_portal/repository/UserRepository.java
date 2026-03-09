package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 1. Dùng để đăng nhập hoặc tìm User theo Email
    Optional<UserEntity> findByEmail(String email);

    // 2. Dùng để kiểm tra trùng lặp Email lúc Đăng ký
    boolean existsByEmail(String email);
}