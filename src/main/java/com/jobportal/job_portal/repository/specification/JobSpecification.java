package com.jobportal.job_portal.repository.specification;

import com.jobportal.job_portal.entity.JobEntity;
import com.jobportal.job_portal.entity.SkillEntity; // Chú ý import thêm bảng Skill
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class JobSpecification {
    public static Specification<JobEntity> filterJobs(String keyword, String type, String level, String location,
            Double salary) {
        return (Root<JobEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. NÂNG CẤP LOGIC TÌM KIẾM KEYWORD TẠI ĐÂY
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKeyword = "%" + keyword.toLowerCase() + "%";

                // Điều kiện A: Tìm trong trường 'title' của Job
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchKeyword);

                // Điều kiện B: Tìm trong trường 'name' của bảng Skill
                // Lệnh Join này tương đương với SQL: LEFT JOIN job_skills -> LEFT JOIN skills
                Join<JobEntity, SkillEntity> skillsJoin = root.join("skills", JoinType.LEFT);
                Predicate skillMatch = cb.like(cb.lower(skillsJoin.get("name")), searchKeyword);

                // Gộp 2 điều kiện: Có trong Title HOẶC có trong Kỹ năng đều được
                predicates.add(cb.or(titleMatch, skillMatch));

                // Lệnh quan trọng: Xóa các kết quả bị nhân đôi (duplicate) do quá trình JOIN
                // bảng gây ra
                query.distinct(true);
            }

            // 2. CÁC LỌC KHÁC GIỮ NGUYÊN
            if (type != null && !type.isEmpty()) {
                predicates.add(cb.equal(root.get("employmentType"), type));
            }
            if (level != null && !level.isEmpty()) {
                predicates.add(cb.equal(root.get("experienceLevel"), level));
            }
            if (location != null && !location.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (salary != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salaryMax"), salary));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}