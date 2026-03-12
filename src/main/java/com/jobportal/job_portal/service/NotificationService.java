package com.jobportal.job_portal.service;

import com.jobportal.job_portal.dto.NotificationResponse;
import com.jobportal.job_portal.entity.NotificationEntity;
import com.jobportal.job_portal.entity.UserEntity;
import com.jobportal.job_portal.exception.ResourceNotFoundException;
import com.jobportal.job_portal.mapper.NotificationMapper;
import com.jobportal.job_portal.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    // Lấy danh sách thông báo
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(String email) {
        return notificationRepository.findByUser_EmailOrderByCreatedAtDesc(email)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Đánh dấu 1 thông báo là đã đọc
    @Transactional
    public void markAsRead(String email, Long id) {
        NotificationEntity notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));

        // Kiểm tra xem thông báo này có đúng là của người đang đăng nhập không
        if (!notification.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên thông báo này");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    // Đánh dấu tất cả là đã đọc
    @Transactional
    public void markAllAsRead(String email) {
        List<NotificationEntity> notifications = notificationRepository.findByUser_EmailOrderByCreatedAtDesc(email);

        for (NotificationEntity notif : notifications) {
            notif.setIsRead(true);
        }

        notificationRepository.saveAll(notifications);
    }

    // ==========================================
    // HÀM DÙNG NỘI BỘ (Các Service khác gọi hàm này để sinh thông báo)
    // ==========================================
    @Transactional
    public void createNotification(UserEntity user, String message) {
        NotificationEntity notification = new NotificationEntity();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}