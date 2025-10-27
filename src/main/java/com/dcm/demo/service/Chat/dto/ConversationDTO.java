package com.dcm.demo.service.Chat.dto;

import lombok.Data;

@Data
public class ConversationDTO {
    private String id;
    private String patientName;
    private String responder;
    private boolean isNewMessage;
}
