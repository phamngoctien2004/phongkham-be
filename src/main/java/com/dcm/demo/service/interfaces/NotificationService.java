package com.dcm.demo.service.interfaces;

public interface NotificationService {
    void sendToTopic(String topic, Object data);
    void sendToUser(String userId, String dest, Object data);
}
