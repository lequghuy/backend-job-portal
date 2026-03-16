package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse; // Import ApiResponse của bạn
import com.jobportal.job_portal.dto.CompanyResponse;
import com.jobportal.job_portal.service.AdminCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
// Bắt buộc phải là quyền ADMIN mới gọi được API này
@PreAuthorize("hasAuthority('ADMIN')")
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
        String message = "Đã cập nhật trạng thái hoạt động của công ty và chủ sở hữu.";
        return ResponseEntity.ok(new ApiResponse<>(true, message, null));
    }
}