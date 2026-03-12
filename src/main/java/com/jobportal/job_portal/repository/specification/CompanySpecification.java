package com.jobportal.job_portal.repository.specification;

import com.jobportal.job_portal.entity.CompanyEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CompanySpecification {
    public static Specification<CompanyEntity> filterCompanies(String keyword, String location) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm theo tên công ty
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("companyName")), "%" + keyword.toLowerCase() + "%"));
            }

            // Lọc theo địa điểm
            if (location != null && !location.trim().isEmpty() && !location.equalsIgnoreCase("Tất cả")) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}