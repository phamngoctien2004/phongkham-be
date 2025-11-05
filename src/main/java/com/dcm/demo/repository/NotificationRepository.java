package com.dcm.demo.repository;

import com.dcm.demo.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByReceiverId(Integer userId);
    List<Notification> findByType(Notification.NotificationType type);
    List<Notification> findByReceiverIdAndIsUserRead(Integer userId, Boolean isUserRead);
    List<Notification> findByTypeAndIsAdminRead(Notification.NotificationType type, Boolean isAdminRead);

}
