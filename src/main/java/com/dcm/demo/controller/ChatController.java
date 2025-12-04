package com.dcm.demo.controller;

import com.dcm.demo.service.Chat.ConversationService;
import com.dcm.demo.service.Chat.MessageService;
import com.dcm.demo.service.Chat.dto.ConversationDTO;
import com.dcm.demo.service.Chat.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ConversationService conversationService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDTO request, Principal principal) {
        try {
            MessageDTO newMessage = messageService.create(request);

            messagingTemplate.convertAndSend(
                    "/topic/chat/" + newMessage.getConversationId(),
                    newMessage);
                    system.out.println("ok");
        } catch (Exception e) {
            log.error("❌ Error processing chat message: {}", e.getMessage(), e);
            throw e; // Re-throw để Spring WebSocket xử lý
        }
    }

    @ResponseBody
    @PostMapping("/api/chat")
    public MessageDTO restSendMessage(@RequestBody MessageDTO request) {
        boolean isNewConversation = false;
        if(request.getConversationId() == null){
            ConversationDTO conversationDTO = conversationService.create("AI");
            request.setConversationId(conversationDTO.getId());
            isNewConversation = true;
        }
        MessageDTO newMessage = messageService.create(request);
        newMessage.setCreateNewAIConversation(isNewConversation);
        return newMessage;
    }
}
