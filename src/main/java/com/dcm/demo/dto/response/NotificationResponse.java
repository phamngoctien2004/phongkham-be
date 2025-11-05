package com.dcm.demo.dto.response;

import com.dcm.demo.model.Notification;
import lombok.Data;

import java.util.List;


@Data
public class NotificationResponse {
    private List<Notification> notifications;
    private Integer unreadCount;
}
