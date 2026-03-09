package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.CompanyResponse;
import com.jobportal.job_portal.service.CompanyService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class PublicCompanyController {

    private final CompanyService companyService;

    // API: Lấy danh sách tất cả công ty
    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAllCompanies() {
        List<CompanyResponse> responseData = companyService.getAllCompanies();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách công ty thành công", responseData));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(@PathVariable Long id) {
        CompanyResponse responseData = companyService.getCompanyById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy chi tiết công ty thành công", responseData));
    }
}