package com.ecommers.notifications.service;

import com.ecommers.notifications.dto.NotificationDto.NotificationRequest;
import com.ecommers.notifications.dto.NotificationDto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse create(NotificationRequest request);
    List<NotificationResponse> getByUser(Long userId);
    List<NotificationResponse> getUnreadByUser(Long userId);
    long countUnread(Long userId);
    NotificationResponse markAsRead(Long id);
    void markAllAsRead(Long userId);
    void delete(Long id);
}
