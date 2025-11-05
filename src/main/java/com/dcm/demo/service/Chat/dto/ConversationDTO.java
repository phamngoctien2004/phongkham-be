package com.dcm.demo.service.Chat.dto;

import lombok.Data;

@Data
public class ConversationDTO {
    private Integer id;
    private String patientName;
    private String responder;
    private boolean isNewMessage;
}
