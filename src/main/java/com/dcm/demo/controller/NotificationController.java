package com.dcm.demo.controller;

import com.dcm.demo.dto.request.EmailRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.Notification;
import com.dcm.demo.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> findAllNotificationsSystem(
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        notificationService.findAllNotificationsSystem(pageable),
                        "Get notifications successfully"
                )
        );
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        notificationService.create(notification),
                        "Create notification successfully"
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findNotificationById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        notificationService.findById(id),
                        "Get notification successfully"
                )
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotificationById(@PathVariable Integer id) {
        notificationService.deleteById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "",
                        "Delete notification successfully"
                )
        );
    }
    @PutMapping
    public ResponseEntity<?> updateNotification(@RequestBody Notification notification) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        notificationService.update(notification),
                        "Update notification successfully"
                )
        );
    }

}
