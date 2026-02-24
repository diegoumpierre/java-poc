package com.poc.notification.controller;

import com.poc.notification.dto.ConversationDTO;
import com.poc.notification.dto.MessageDTO;
import com.poc.notification.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversations", description = "Email conversations (atendimento)")
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    @Operation(summary = "List conversations for current tenant")
    public ResponseEntity<List<ConversationDTO>> listConversations() {
        return ResponseEntity.ok(conversationService.listConversations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get conversation details")
    public ResponseEntity<ConversationDTO> getConversation(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversation(id));
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Get all messages in conversation")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getMessages(id));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Mark conversation as read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        conversationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
