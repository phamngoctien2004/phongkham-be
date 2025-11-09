package com.dcm.demo.service.impl;

import com.dcm.demo.model.Notification;
import com.dcm.demo.service.interfaces.EmailService;
import com.dcm.demo.service.interfaces.NotificationService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSendingService {
    private final UserService userService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    @Async
    public void sendNewsletterToAllUsers(Integer notificationId) {
        try{
            Notification notification = notificationService.findById(notificationId);
            userService.findAll().forEach(u -> {
                emailService.sendTemplate(
                        u.getEmail(),
                        notification.getTitle(),
                        "newsletter",
                        Map.of(
                                "name", u.getName(),
                                "title", notification.getTitle(),
                                "content", notification.getContent(),
                                "image", notification.getImage()
                        )
                );
            });
        }catch (Exception e){
            log.error("Error sending newsletter");
        }

    }
}
