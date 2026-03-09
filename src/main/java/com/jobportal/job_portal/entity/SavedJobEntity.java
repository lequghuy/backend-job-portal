package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private UserEntity candidate;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private JobEntity job;

    private LocalDateTime savedAt;
}
