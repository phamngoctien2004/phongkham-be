package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.EmailRequest;
import com.dcm.demo.dto.response.NotificationResponse;
import com.dcm.demo.model.Notification;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    NotificationResponse findByUser();

    void send(String title, Notification.NotificationType type, Integer id);

    void markAsRead();

    Page<Notification> findAllNotificationsSystem(Pageable pageable);
    Notification findById(Integer id);
    Notification create(Notification request);
    Notification update(Notification notification);
    void deleteById(Integer id);

}
