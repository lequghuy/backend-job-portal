package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Set;
import java.time.LocalDate;

@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_job_status_created", columnList = "status, createdAt DESC"),
        @Index(name = "idx_job_location", columnList = "location"),
        @Index(name = "idx_job_type_level", columnList = "employmentType, experienceLevel")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE jobs SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")

public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private JobCategoryEntity category;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail")
    private String thumbnail; //////////

    private Double salaryMin;
    private Double salaryMax;
    private String location;
    private String status;

    // ----- THÊM MỚI TỪ ĐÂY -----
    private String employmentType; // Ví dụ: FULL_TIME, PART_TIME, FREELANCE
    private String experienceLevel; // Ví dụ: FRESHER, JUNIOR, MIDDLE, SENIOR

    private LocalDate deadline; // Hạn chót nộp hồ sơ

    // Bảng trung gian job_skills (Các kỹ năng yêu cầu)
    @ManyToMany
    @JoinTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<SkillEntity> skills;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}