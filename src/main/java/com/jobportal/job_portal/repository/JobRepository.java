package com.jobportal.job_portal.repository;

import com.jobportal.job_portal.entity.JobEntity;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long>, JpaSpecificationExecutor<JobEntity> {
    // Lấy danh sách việc làm của một công ty cụ thể (Dành cho Employer)
    List<JobEntity> findByCompany_User_Email(String email);

    // Lấy danh sách việc làm theo trạng thái (Dành cho Public để chỉ hiển thị Job
    // đang "OPEN")
    Page<JobEntity> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    @Query("SELECT DISTINCT j.location FROM JobEntity j WHERE j.status = 'OPEN' AND j.location IS NOT NULL")
    List<String> findDistinctLocations();

    // 2. Quét tất cả các Loại công việc đang có
    @Query("SELECT DISTINCT j.employmentType FROM JobEntity j WHERE j.status = 'OPEN' AND j.employmentType IS NOT NULL")
    List<String> findDistinctEmploymentTypes();

    // 3. Quét tất cả các Cấp độ kinh nghiệm đang có
    @Query("SELECT DISTINCT j.experienceLevel FROM JobEntity j WHERE j.status = 'OPEN' AND j.experienceLevel IS NOT NULL")
    List<String> findDistinctExperienceLevels();

    Page<JobEntity> findByCompany_User_Email(String email, Pageable pageable);

    @Modifying
    @Transactional // Quan trọng: Phải có Transactional để thực hiện lệnh Update
    @Query("UPDATE JobEntity j SET j.status = :status WHERE j.company.id = :companyId")
    void updateStatusByCompanyId(@Param("companyId") Long companyId, @Param("status") String status);
}