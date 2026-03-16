package com.jobportal.job_portal.service;

import com.jobportal.job_portal.repository.EmployerSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduler {

    private final EmployerSubscriptionRepository subscriptionRepository;

    // Chạy vào lúc 00:01 mỗi ngày
    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional
    public void scanExpiredSubscriptions() {
        log.info("Bắt đầu quét các gói dịch vụ hết hạn...");

        // Cập nhật tất cả các gói ACTIVE có endDate < thời gian hiện tại thành EXPIRED
        int updatedCount = subscriptionRepository.updateExpiredSubscriptions();

        log.info("Đã quét và khóa {} gói dịch vụ hết hạn.", updatedCount);
    }
}