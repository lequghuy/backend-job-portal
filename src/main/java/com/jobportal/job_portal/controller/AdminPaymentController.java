package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.PaymentHistoryResponse;
import com.jobportal.job_portal.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/payments") // KHAI BÁO URL Ở ĐÂY
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MANAGE_COMPANIES')")
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentHistoryResponse>>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PaymentHistoryResponse> history = paymentService.getAllPaymentsForAdmin(page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách giao dịch thành công", history));
    }
}