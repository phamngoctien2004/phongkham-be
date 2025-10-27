package com.dcm.demo.controller;

import com.dcm.demo.service.Chat.MessageService;
import com.dcm.demo.service.Chat.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDTO request, Principal principal) {
        try {
            MessageDTO newMessage = messageService.create(request);

            messagingTemplate.convertAndSend(
                    "/topic/chat/" + newMessage.getConversationId(),
                    newMessage);
        } catch (Exception e) {
            log.error("❌ Error processing chat message: {}", e.getMessage(), e);
            throw e; // Re-throw để Spring WebSocket xử lý
        }
    }


}
