package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.CompanyRequest;
import com.jobportal.job_portal.dto.CompanyResponse;
import com.jobportal.job_portal.entity.CompanyEntity;
import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.exception.ApiException;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.CompanyMapper;
import com.jobportal.job_portal.repository.CompanyRepository;
import com.jobportal.job_portal.repository.UserRepository;
import com.jobportal.job_portal.repository.specification.CompanySpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;
    private final CloudinaryService cloudinaryService;

    // 1. Dành cho Employer: Lấy thông tin công ty của chính mình
    public CompanyResponse getMyCompany(String email) {
        CompanyEntity company = companyRepository.findByUser_Email(email)
                .orElse(null);

        // NẾU CHƯA CÓ THÔNG TIN (Do tài khoản mới tạo) -> Trả về đối tượng rỗng để
        // Frontend hiện form trống
        if (company == null) {
            return new CompanyResponse();
        }

        return companyMapper.toResponse(company);
    }

    // 2. Dành cho Employer: Tạo mới hoặc Cập nhật thông tin công ty
    @Transactional
    public CompanyResponse updateMyCompany(String email, CompanyRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản nhà tuyển dụng"));

        // Tìm công ty cũ, nếu chưa có thì khởi tạo mới
        CompanyEntity company = companyRepository.findByUser_Email(email)
                .orElse(new CompanyEntity());

        company.setUser(user);
        company.setCompanyName(request.getCompanyName());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setLocation(request.getLocation());

        CompanyEntity savedCompany = companyRepository.save(company);
        log.info("Cập nhật hồ sơ công ty thành công cho tài khoản: {}", email);

        return companyMapper.toResponse(savedCompany);
    }

    // 3. Dành cho Public (Ứng viên): Xem chi tiết 1 công ty bất kỳ theo ID
    public CompanyResponse getCompanyById(Long id) {
        CompanyEntity company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công ty này"));
        return companyMapper.toResponse(company);
    }

    // 4. Dành cho Public (Ứng viên): Xem danh sách tất cả công ty
    public Page<CompanyResponse> getAllCompanies(Pageable pageable) {
        // Lấy tất cả từ DB
        Page<CompanyEntity> companies = companyRepository.findAll(pageable);

        // Map sang danh sách Response (DTO)
        return companies.map(companyMapper::toResponse);

    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> getFilteredCompanies(String keyword, String location, Pageable pageable) {
        Specification<CompanyEntity> spec = CompanySpecification.filterCompanies(keyword, location);
        // Nhớ đảm bảo CompanyRepository extends JpaSpecificationExecutor<CompanyEntity>
        // nhé
        return companyRepository.findAll(spec, pageable).map(companyMapper::toResponse);
    }

    @Transactional
    public String uploadLogo(String email, MultipartFile file) {
        CompanyEntity company = companyRepository.findByUser_Email(email)
                .orElseThrow(() -> new ApiException("Không tìm thấy thông tin công ty"));

        if (file != null && !file.isEmpty()) {
            // Đẩy lên Cloudinary vào thư mục "logos"
            String fileUrl = cloudinaryService.uploadFile(file, "logos");

            company.setLogo(fileUrl);
            companyRepository.save(company);
            return fileUrl;
        }
        throw new ApiException("File không hợp lệ");
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctLocations() {
        return companyRepository.findDistinctLocations();
    }

}