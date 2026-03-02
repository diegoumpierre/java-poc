package com.poc.chat.controller;

import com.poc.chat.dto.chat.ChatNotificationDTO;
import com.poc.chat.service.ChatNotificationService;
import com.poc.shared.security.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ChatNotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<ChatNotificationDTO>> getNotifications(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(notificationService.getNotifications(offset, limit));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Integer>> getUnreadCount() {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount()));
    }

    @PutMapping("/{id}/read")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }
}
