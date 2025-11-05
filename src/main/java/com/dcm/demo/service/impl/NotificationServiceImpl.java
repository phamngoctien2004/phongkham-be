package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.NotificationResponse;
import com.dcm.demo.model.Notification;
import com.dcm.demo.model.User;
import com.dcm.demo.repository.NotificationRepository;
import com.dcm.demo.service.interfaces.NotificationService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate template;
    private final NotificationRepository repository;
    private final UserService userService;

    @Override
    public NotificationResponse findByUser() {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole().equals(User.Role.BENH_NHAN)) {
            List<Notification> notifications = repository.findByReceiverId(currentUser.getId());
            return buildNotificationResponse(notifications, false);
        }

        List<Notification> notifications = repository.findByType(Notification.NotificationType.DAT_LICH);
        return buildNotificationResponse(notifications, true);
    }

    @Override
    public void send(String title, Notification.NotificationType type, Integer id) {
        User user = userService.getCurrentUser();
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setType(type);
        notification.setReceiverId(user.getId());
        notification.setTypeId(id);
        Notification saved = repository.save(notification);

        if(type.equals(Notification.NotificationType.DAT_LICH)) {
            template.convertAndSend("/topic/notifications/book." + id, saved);
        }
    }

    @Override
    public void markAsRead() {
        User currentUser = userService.getCurrentUser();
        List<Notification> notifications;
        if (currentUser.getRole().equals(User.Role.BENH_NHAN)) {
            notifications = repository.findByReceiverIdAndIsUserRead(currentUser.getId(), false);
            for (Notification notification : notifications) {
                notification.setIsUserRead(true);
            }
        } else {
            notifications = repository.findByTypeAndIsAdminRead(Notification.NotificationType.DAT_LICH, false);
            for (Notification notification : notifications) {
                notification.setIsAdminRead(true);
            }
        }



        repository.saveAll(notifications);
    }

    private NotificationResponse buildNotificationResponse(List<Notification> notifications, boolean isAdmin) {
        NotificationResponse response = new NotificationResponse();
        response.setNotifications(notifications);
        long unreadCount;
        if (!   isAdmin) {
            unreadCount = notifications.stream().filter(n -> !n.getIsUserRead()).count();
        } else {
            unreadCount = notifications.stream().filter(n -> !n.getIsAdminRead()).count();
        }
        response.setUnreadCount((int) unreadCount);
        return response;
    }

}
