package com.dcm.demo.service.impl;

import com.dcm.demo.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate template;

    @Override
    public void sendToTopic(String topic, Object data) {
        template.convertAndSend("/topic/" + topic, data);
    }

    @Override
    public void sendToUser(String userId, String dest, Object data) {
        template.convertAndSendToUser(userId, "/queue/" + dest, data);
    }


}
