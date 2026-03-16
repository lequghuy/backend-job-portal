package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.CompanyRequest;
import com.jobportal.job_portal.dto.CompanyResponse;
import com.jobportal.job_portal.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // SỬA Ở ĐÂY: Chỉ để lại phần đuôi của URL
    @PostMapping("/upload-logo") 
    public ResponseEntity<ApiResponse<String>> uploadLogo(
            Principal principal,
            @RequestParam("file") MultipartFile file) {

        // Gọi service xử lý (Mình sẽ viết hàm này ở bước dưới)
        String fileName = companyService.uploadLogo(principal.getName(), file);

        return ResponseEntity.ok(new ApiResponse<>(true, "Upload logo thành công", fileName));
    }
}