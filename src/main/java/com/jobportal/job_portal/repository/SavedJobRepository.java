package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.SavedJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJobEntity, Long> {

    // Lấy danh sách việc làm đã lưu của ứng viên (sắp xếp mới nhất lên đầu)
    List<SavedJobEntity> findByCandidate_EmailOrderBySavedAtDesc(String email);

    // Kiểm tra xem ứng viên đã lưu Job này chưa (tránh lưu trùng 2 lần)
    boolean existsByJob_IdAndCandidate_Email(Long jobId, String email);

    // Tìm một record cụ thể để thực hiện chức năng Bỏ lưu (Unsave)
    Optional<SavedJobEntity> findByJob_IdAndCandidate_Email(Long jobId, String email);
}