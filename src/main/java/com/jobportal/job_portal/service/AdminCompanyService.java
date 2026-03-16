package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.CompanyResponse;
import com.jobportal.job_portal.entity.CompanyEntity;
import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.CompanyMapper;
import com.jobportal.job_portal.repository.CompanyRepository;
import com.jobportal.job_portal.repository.UserRepository;
import com.jobportal.job_portal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository; // Bổ sung để khóa User
    private final JobRepository jobRepository; // Bổ sung để ẩn tin đăng
    private final CompanyMapper companyMapper;

    // 1. Lấy danh sách công ty phân trang
    public Page<CompanyResponse> getAllCompanies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CompanyEntity> companyPage = companyRepository.findAll(pageable);
        return companyPage.map(companyMapper::toResponse);
    }

    // 2. Chức năng KHÓA LIÊN HOÀN
    @Transactional
    public void toggleCompanyBan(Long companyId) {
        // Tìm công ty
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công ty ID: " + companyId));

        // Lấy User chủ quản (Employer)
        UserEntity employer = company.getUser();
        if (employer == null) {
            throw new ResourceNotFoundException("Công ty này không có tài khoản quản lý!");
        }

        // Đảo ngược trạng thái isActive của User
        boolean newStatus = !employer.getIsActive();
        employer.setIsActive(newStatus);
        userRepository.save(employer);

        // Xử lý ẩn/hiện tin tuyển dụng của công ty đó
        if (!newStatus) {
            // Nếu khóa User: Đưa tất cả job của cty này về trạng thái BANNED hoặc HIDDEN
            jobRepository.updateStatusByCompanyId(companyId, "BANNED");
            log.warn("Admin đã KHÓA công ty [{}] và tài khoản [{}]", company.getCompanyName(), employer.getEmail());
        } else {
            // Nếu mở lại: Đưa job về trạng thái ACTIVE (hoặc PENDING tùy bạn)
            jobRepository.updateStatusByCompanyId(companyId, "ACTIVE");
            log.info("Admin đã MỞ KHÓA công ty [{}] và tài khoản [{}]", company.getCompanyName(), employer.getEmail());
        }
    }
}