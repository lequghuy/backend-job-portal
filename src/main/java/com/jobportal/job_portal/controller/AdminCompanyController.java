package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.CompanyResponse;
import com.jobportal.job_portal.service.AdminCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
// THAY ĐỔI: Sử dụng quyền cụ thể
@PreAuthorize("hasAuthority('MANAGE_COMPANIES')")
public class AdminCompanyController {

    private final AdminCompanyService adminCompanyService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CompanyResponse>>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CompanyResponse> companies = adminCompanyService.getAllCompanies(page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách công ty thành công", companies));
    }

    @PutMapping("/{id}/toggle-ban")
    public ResponseEntity<ApiResponse<Void>> toggleCompanyBan(@PathVariable Long id) {
        adminCompanyService.toggleCompanyBan(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đã cập nhật trạng thái hoạt động của công ty", null));
    }
}