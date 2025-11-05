package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.response.NotificationResponse;
import com.dcm.demo.model.Notification;

import java.util.List;

public interface NotificationService {
    NotificationResponse findByUser();

    void send(String title, Notification.NotificationType type, Integer id);

    void markAsRead();
}
