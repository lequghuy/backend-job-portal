package com.jobportal.job_portal.service;

import com.jobportal.job_portal.config.VnpayConfig;
import com.jobportal.job_portal.entity.*;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EmployerSubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;
    @Value("${vnpay.hashSecret}")
    private String secretKey;
    @Value("${vnpay.payUrl}")
    private String vnp_PayUrl;
    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    // 1. TẠO URL THANH TOÁN GỬI CHO FRONTEND
    @Transactional
    public String createPaymentUrl(String email, Long planId, HttpServletRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SubscriptionPlanEntity plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
        long amount = (long) (plan.getPrice() * 100); // VNPAY tính tiền x100

        // Tạo gói tạm (PENDING)
        EmployerSubscriptionEntity subscription = new EmployerSubscriptionEntity();
        subscription.setEmployer(user);
        subscription.setPlan(plan);
        subscription.setStatus("PENDING_PAYMENT");
        subscription.setJobsPosted(0);
        EmployerSubscriptionEntity savedSub = subscriptionRepository.save(subscription);

        // Tạo lịch sử giao dịch tạm (PENDING)
        PaymentEntity payment = new PaymentEntity();
        payment.setEmployer(user);
        payment.setSubscription(savedSub);
        payment.setAmount(plan.getPrice());
        payment.setTxnRef(vnp_TxnRef);
        payment.setOrderInfo("Thanh_Toan_Goi_Dich_Vu_" + plan.getId() + "_MaGD_" + vnp_TxnRef);
        payment.setPaymentMethod("VNPAY");
        payment.setPaymentStatus("PENDING");
        paymentRepository.save(payment);

        // Map cấu hình params gửi VNPAY
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", payment.getOrderInfo());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", VnpayConfig.getIpAddress(request));

        // Format ngày theo chuẩn VNPAY (GMT+7)
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15); // Hết hạn sau 15 phút
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build string và hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        try {
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (i < fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VnpayConfig.hmacSHA512(secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            return vnp_PayUrl + "?" + queryUrl;
        } catch (Exception e) {
            log.error("Lỗi tạo URL VNPAY", e);
            throw new RuntimeException("Không thể tạo URL thanh toán");
        }
    }

    // 2. IPN CALLBACK - VNPAY GỌI NGẦM VÀO ĐÂY KHI THANH TOÁN XONG
    @Transactional
    public Map<String, String> processVnpayIpn(Map<String, String> params) {
        Map<String, String> response = new HashMap<>();
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");

            // TẠO BẢN SAO ĐỂ TRÁNH LỖI KHI REMOVE
            Map<String, String> fields = new HashMap<>(params);
            fields.remove("vnp_SecureHash");
            fields.remove("vnp_SecureHashType");

            // Tạo lại chuỗi hash để kiểm tra xem có bị hacker giả mạo không
            List<String> fieldNames = new ArrayList<>(fields.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = fields.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (i < fieldNames.size() - 1) {
                        hashData.append('&');
                    }
                }
            }
            String secureHash = VnpayConfig.hmacSHA512(secretKey, hashData.toString());

            if (secureHash.equals(vnp_SecureHash)) {
                // Bảo mật OK -> Tìm đơn hàng
                String txnRef = fields.get("vnp_TxnRef");
                Optional<PaymentEntity> paymentOpt = paymentRepository.findByTxnRef(txnRef);

                if (paymentOpt.isPresent()) {
                    PaymentEntity payment = paymentOpt.get();
                    if ("PENDING".equals(payment.getPaymentStatus())) {
                        String responseCode = fields.get("vnp_ResponseCode");
                        payment.setVnpayTransactionNo(fields.get("vnp_TransactionNo"));
                        payment.setBankCode(fields.get("vnp_BankCode"));
                        payment.setResponseCode(responseCode);

                        EmployerSubscriptionEntity sub = payment.getSubscription();

                        if ("00".equals(responseCode)) { // "00" là thành công theo chuẩn VNPAY
                            payment.setPaymentStatus("SUCCESS");

                            // ==========================================
                            // BẮT ĐẦU LOGIC CỘNG DỒN GÓI CƯỚC
                            // ==========================================

                            // 1. TÌM XEM KHÁCH CÓ GÓI NÀO ĐANG ACTIVE KHÔNG
                            Optional<EmployerSubscriptionEntity> existingSubOpt = subscriptionRepository
                                    .findFirstByEmployer_EmailAndStatusOrderByIdDesc(sub.getEmployer().getEmail(),
                                            "ACTIVE");

                            int totalMaxJobs = sub.getPlan().getJobPostLimit();
                            LocalDateTime newEndDate = LocalDateTime.now().plusDays(sub.getPlan().getDurationDays());

                            // 2. NẾU CÓ GÓI CŨ -> THỰC HIỆN CỘNG DỒN
                            if (existingSubOpt.isPresent()) {
                                EmployerSubscriptionEntity oldSub = existingSubOpt.get();

                                // Chuyển gói cũ thành trạng thái UPGRADED (Đã nâng cấp) để không dùng lại nữa
                                oldSub.setStatus("UPGRADED");
                                subscriptionRepository.save(oldSub);

                                // Cộng dồn số tin đăng còn thừa (Lấy Max - Đã dùng)
                                int remainingJobs = Math.max(0, oldSub.getMaxJobs() - oldSub.getJobsPosted());
                                totalMaxJobs += remainingJobs;

                                // Cộng dồn ngày sử dụng (Nếu gói cũ vẫn còn hạn ở tương lai)
                                if (oldSub.getEndDate() != null && oldSub.getEndDate().isAfter(LocalDateTime.now())) {
                                    long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(),
                                            oldSub.getEndDate());
                                    newEndDate = newEndDate.plusDays(remainingDays);
                                }
                            }

                            // 3. CẬP NHẬT QUYỀN LỢI CHO GÓI MỚI (Đã bao gồm tài sản cũ)
                            sub.setStatus("ACTIVE");
                            sub.setStartDate(LocalDateTime.now());
                            sub.setEndDate(newEndDate);
                            sub.setMaxJobs(totalMaxJobs);
                            sub.setJobsPosted(0); // Gói mới bắt đầu đếm từ 0

                            log.info("Thanh toán thành công & Đã cộng dồn quyền lợi cho đơn: " + txnRef);
                            // ==========================================
                        } else {
                            payment.setPaymentStatus("FAILED");
                            sub.setStatus("CANCELLED");
                            log.warn("Thanh toán thất bại cho đơn: " + txnRef);
                        }

                        paymentRepository.save(payment);
                        subscriptionRepository.save(sub);

                        response.put("RspCode", "00");
                        response.put("Message", "Confirm Success");
                    } else {
                        response.put("RspCode", "02");
                        response.put("Message", "Order already confirmed");
                    }
                } else {
                    response.put("RspCode", "01");
                    response.put("Message", "Order not found");
                }
            } else {
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
            }
        } catch (Exception e) {
            log.error("Lỗi xử lý IPN", e);
            response.put("RspCode", "99");
            response.put("Message", "Unknow error");
        }
        return response;
    }

    // Xác thực chữ ký dùng cho Redirect URL
    public boolean verifyVnpaySignature(Map<String, String> params) {
        try {
            Map<String, String> fields = new HashMap<>(params);
            String vnp_SecureHash = fields.remove("vnp_SecureHash");
            fields.remove("vnp_SecureHashType");

            List<String> fieldNames = new ArrayList<>(fields.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();

            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = fields.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (i < fieldNames.size() - 1) {
                        hashData.append('&');
                    }
                }
            }
            String secureHash = VnpayConfig.hmacSHA512(secretKey, hashData.toString());
            return secureHash.equals(vnp_SecureHash);
        } catch (Exception e) {
            log.error("Lỗi xác thực chữ ký VNPAY", e);
            return false;
        }
    }
}