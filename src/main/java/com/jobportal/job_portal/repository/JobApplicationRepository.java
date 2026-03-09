package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.JobApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity, Long> {

    // Lấy danh sách công việc mà một ứng viên đã nộp
    List<JobApplicationEntity> findByCandidate_EmailOrderByAppliedAtDesc(String email);

    // Lấy danh sách hồ sơ ứng tuyển của một công việc cụ thể
    List<JobApplicationEntity> findByJob_IdOrderByAppliedAtDesc(Long jobId);

    // Kiểm tra xem ứng viên đã nộp CV vào Job này chưa (tránh nộp trùng 2 lần)
    boolean existsByJob_IdAndCandidate_Email(Long jobId, String email);

    // HÀM MỚI BẢO MẬT: Tìm tất cả CV nộp vào các Công việc do Công ty của
    // User(email) đăng
    // Thay thế hàm cũ bằng hàm có @Query này
    @Query("SELECT a FROM JobApplicationEntity a " +
            "WHERE a.job.company.user.email = :email " +
            "ORDER BY a.appliedAt DESC")
    List<JobApplicationEntity> findByEmployerEmail(@Param("email") String email);
}