package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.PaymentHistoryResponse;
import com.jobportal.job_portal.entity.EmployerSubscriptionEntity;
import com.jobportal.job_portal.repository.EmployerSubscriptionRepository; // <-- SỬA IMPORT Ở ĐÂY
import com.jobportal.job_portal.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // <-- SỬA LẠI KHAI BÁO REPOSITORY NÀY
    private final EmployerSubscriptionRepository employerSubscriptionRepository;

    // 1. API TRẢ VỀ LINK THANH TOÁN
    @PostMapping("/create/{planId}")
    public ResponseEntity<ApiResponse<String>> createPayment(
            Principal principal,
            @PathVariable Long planId,
            HttpServletRequest request) {

        String email = principal.getName();
        String paymentUrl = paymentService.createPaymentUrl(email, planId, request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo URL thành công", paymentUrl));
    }

    // 2. API ĐỂ VNPAY GỌI NGẦM (IPN)
    @GetMapping("/vnpay-ipn")
    public Map<String, String> vnpayIpn(@RequestParam Map<String, String> params) {
        return paymentService.processVnpayIpn(params);
    }

    // 3. API Lấy thông tin gói đang Active của user
    @GetMapping("/my-subscription")
    public ResponseEntity<ApiResponse<EmployerSubscriptionEntity>> getMySubscription(Principal principal) {

        // <-- DÙNG ĐÚNG REPO Ở ĐÂY
        Optional<EmployerSubscriptionEntity> sub = employerSubscriptionRepository
                .findFirstByEmployer_EmailAndStatusOrderByIdDesc(principal.getName(), "ACTIVE");

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin gói thành công", sub.orElse(null)));
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<ApiResponse<String>> vnpayReturn(@RequestParam Map<String, String> params) {

        // 1. Kiểm tra xem dữ liệu có bị hacker giả mạo không
        boolean isValidSignature = paymentService.verifyVnpaySignature(params);
        if (!isValidSignature) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Chữ ký không hợp lệ, dữ liệu có thể đã bị can thiệp", null));
        }

        // 2. VÌ ĐANG CHẠY LOCALHOST (VNPay không gọi được IPN ngầm)
        // NÊN CHÚNG TA PHẢI ÉP NÓ CẬP NHẬT DB NGAY TẠI ĐÂY!
        Map<String, String> result = paymentService.processVnpayIpn(params);
        String rspCode = result.get("RspCode");

        // 3. Đọc kết quả
        String vnp_ResponseCode = params.get("vnp_ResponseCode");

        // Nếu VNPay trừ tiền thành công (00) VÀ DB cập nhật thành công (00) hoặc đã cập
        // nhật rồi (02)
        if ("00".equals(vnp_ResponseCode) && ("00".equals(rspCode) || "02".equals(rspCode))) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Xác nhận thanh toán thành công", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Khách hàng hủy giao dịch hoặc thanh toán thất bại", null));
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('MANAGE_OWN_COMPANY')")
    public ResponseEntity<ApiResponse<Page<PaymentHistoryResponse>>> getPaymentHistory(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PaymentHistoryResponse> history = paymentService.getEmployerPaymentHistory(principal.getName(), page,
                size);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy lịch sử giao dịch thành công", history));
    }
}