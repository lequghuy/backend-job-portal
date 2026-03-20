package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications", indexes = {
        @Index(name = "idx_app_applied_at", columnList = "appliedAt DESC")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private JobEntity job;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private UserEntity candidate;

    // ----- THAY ĐỔI Ở ĐÂY -----
    // Thay vì String resumeUrl, ta map thẳng tới bản CV ứng viên đã chọn
    @ManyToOne
    @JoinColumn(name = "resume_id")
    private ResumeEntity resume;

    private String status; // PENDING, REVIEWED, ACCEPTED, REJECTED

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime appliedAt;
}