package com.jobportal.job_portal.mapper;

import com.jobportal.job_portal.dto.NotificationResponse;
import com.jobportal.job_portal.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(NotificationEntity entity) {
        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setMessage(entity.getMessage());
        response.setIsRead(entity.getIsRead());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}