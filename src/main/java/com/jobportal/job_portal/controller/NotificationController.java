package com.jobportal.job_portal.controller;

import com.jobportal.job_portal.dto.ApiResponse;
import com.jobportal.job_portal.dto.NotificationResponse;
import com.jobportal.job_portal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Lấy thông báo thành công", notificationService.getMyNotifications(principal.getName())));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(Principal principal, @PathVariable Long id) {
        notificationService.markAsRead(principal.getName(), id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đã đọc thông báo", null));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(Principal principal) {
        notificationService.markAllAsRead(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Đã đọc tất cả thông báo", null));
    }
}