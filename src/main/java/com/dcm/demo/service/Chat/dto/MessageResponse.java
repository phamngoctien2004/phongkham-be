package com.dcm.demo.service.Chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private List<MessageDTO> messages;
    private int lastReadId;
    private int totalUnread;
    private int totalMessage;
    private boolean hasMoreOld;
    private boolean test;
}
