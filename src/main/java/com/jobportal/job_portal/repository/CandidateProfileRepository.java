package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.CandidateProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfileEntity, Long> {

    // Tìm hồ sơ dựa vào email của User (Từ JWT Token)
    @Query("""
            SELECT p FROM CandidateProfileEntity p
            LEFT JOIN FETCH p.skills
            WHERE p.user.email = :email
            """)
    Optional<CandidateProfileEntity> findByUser_Email(String email);
}