package com.dcm.demo.controller;

import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.Chat.ConversationService;
import com.dcm.demo.service.Chat.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final MessageService messageService;
    private final ConversationService conversationService;
    @GetMapping
    public ResponseEntity<?> getAllConversations() {
        return ResponseEntity.ok(
                new ApiResponse<>(conversationService.getAll(), "Lấy danh sách cuộc trò chuyện thành công")
        );
    }
    @GetMapping("/ai")
    public ResponseEntity<?> getAllAIConversations() {
        return ResponseEntity.ok(
                new ApiResponse<>(conversationService.getAllAiConversations(), "Lấy danh sách cuộc trò chuyện AI thành công")
        );
    }
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<?> loadMessageFirst(@PathVariable Integer conversationId) {
        return ResponseEntity.ok(
                new ApiResponse<>(conversationService.loadMessageFirst(conversationId), "Lấy lịch sử trò chuyện thành công")
        );
    }

    @GetMapping("/{conversationId}/messages/more")
    public ResponseEntity<?> loadMoreMessages(
            @PathVariable Integer conversationId,
            @RequestParam Integer beforeId){
        return ResponseEntity.ok(
                new ApiResponse<>(conversationService.loadMoreOldMessage(conversationId, beforeId), "Lấy thêm tin nhắn thành công")
        );
    }
    @PutMapping("/{conversationId}")
    public ResponseEntity<?> update(@PathVariable Integer conversationId, @RequestParam String name) {

        return ResponseEntity.ok(
                new ApiResponse<>(conversationService.updateName(conversationId, name), "Cập nhật tên cuộc trò chuyện thành công")
        );

    }
    @DeleteMapping("/{conversationId}/messages/clear" )
    public ResponseEntity<?> clearMessages(@PathVariable Integer conversationId) {
        conversationService.clearMessage(conversationId);
        return ResponseEntity.ok(
                new ApiResponse<>(null, "Xóa tin nhắn thành công")
        );
    }

}
