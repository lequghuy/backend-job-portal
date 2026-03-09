package com.jobportal.job_portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "candidate_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String fullName;
    private String phone;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String education;

    // Tạo bảng trung gian candidate_skills
    @ManyToMany
    @JoinTable(name = "candidate_skills", joinColumns = @JoinColumn(name = "candidate_profile_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<SkillEntity> skills;
}