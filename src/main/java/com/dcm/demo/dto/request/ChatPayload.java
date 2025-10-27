package com.dcm.demo.dto.request;

import lombok.Data;

@Data
public class ChatPayload {
    private Integer roomId;
    private Integer senderId;
    private String message;
    private String[] attachmentUrl;
}
