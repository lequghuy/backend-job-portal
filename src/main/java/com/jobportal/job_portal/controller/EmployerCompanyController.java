package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.CompanyRequest;
import com.jobportal.job_portal.dto.CompanyResponse;
import com.jobportal.job_portal.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/employer/company")
@RequiredArgsConstructor
public class EmployerCompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> getMyCompany(Principal principal) {
        CompanyResponse responseData = companyService.getMyCompany(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin công ty thành công", responseData));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> updateMyCompany(
            Principal principal,
            @Valid @RequestBody CompanyRequest request) {
        CompanyResponse responseData = companyService.updateMyCompany(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật hồ sơ công ty thành công", responseData));
    }
}