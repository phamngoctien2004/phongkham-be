package com.dcm.demo.service.Chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private Integer id;
    private Integer conversationId;
    private Integer senderId;
    private String message;
    private Integer lastMessageConversation;
    private LocalDateTime sentTime;
    private List<String> urls;
    private boolean isCreateNewAIConversation;
}
