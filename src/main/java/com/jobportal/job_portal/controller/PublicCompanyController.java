package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.CompanyResponse;
import com.jobportal.job_portal.service.CompanyService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class PublicCompanyController {

    private final CompanyService companyService;

    // API: Lấy danh sách tất cả công ty
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CompanyResponse>>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyResponse> responseData = companyService.getAllCompanies(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách công ty thành công", responseData));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(@PathVariable Long id) {
        CompanyResponse responseData = companyService.getCompanyById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy chi tiết công ty thành công", responseData));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<CompanyResponse>>> filterCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) { // Trả về 12 công ty 1 trang cho đẹp lưới

        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyResponse> companies = companyService.getFilteredCompanies(keyword, location, pageable);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách công ty thành công", companies));
    }

    @GetMapping("/filter-options")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFilterOptions() {
        Map<String, Object> options = new HashMap<>();

        // Gọi hàm quét địa điểm từ Repository (gọi trực tiếp hoặc qua Service đều được)
        options.put("locations", companyService.getDistinctLocations()); // Hoặc
                                                                         // companyRepository.findDistinctLocations()

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách địa điểm thành công", options));
    }
}