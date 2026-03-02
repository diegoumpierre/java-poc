package com.poc.chat.controller;

import com.poc.chat.dto.chat.*;
import com.poc.chat.service.ChatService;
import com.poc.shared.security.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Internal communicator endpoints")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // ==================== User Endpoints ====================

    @GetMapping("/users")
    @Operation(summary = "List available users for chat", description = "Returns all users in the same tenant that can be contacted")
    public ResponseEntity<List<ChatUserDTO>> getAvailableUsers() {
        log.debug("Getting available chat users");
        return ResponseEntity.ok(chatService.getAvailableUsers());
    }

    @GetMapping("/users/me")
    @Operation(summary = "Get current user's chat profile")
    public ResponseEntity<ChatUserDTO> getCurrentUser() {
        return chatService.getCurrentChatUser()
                .map(ChatUserDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== Conversation Endpoints ====================

    @GetMapping("/conversations")
    @Operation(summary = "List user's conversations", description = "Returns all conversations ordered by most recent message")
    public ResponseEntity<List<ChatConversationDTO>> getConversations() {
        log.debug("Getting conversations for current user");
        return ResponseEntity.ok(chatService.getConversations());
    }

    @GetMapping("/conversations/{id}")
    @Operation(summary = "Get conversation details")
    public ResponseEntity<ChatConversationDTO> getConversation(@PathVariable Long id) {
        return chatService.getConversation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/conversations")
    @Operation(summary = "Create or get existing conversation", description = "Creates a new conversation with another user or returns existing one")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<ChatConversationDTO> createConversation(@RequestBody @Valid CreateConversationRequest request) {
        log.info("Creating/getting conversation with user: {}", request.getOtherUserId());
        return ResponseEntity.ok(chatService.createOrGetConversation(request));
    }

    // ==================== Message Endpoints ====================

    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Get messages from a conversation", description = "Returns messages with optional pagination")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "50") int limit) {
        log.debug("Getting messages for conversation: {}, offset={}, limit={}", conversationId, offset, limit);
        return ResponseEntity.ok(chatService.getMessages(conversationId, offset, limit));
    }

    @PostMapping("/messages")
    @Operation(summary = "Send a message", description = "Sends a new message in a conversation")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody @Valid SendMessageRequest request) {
        log.debug("Sending message to conversation: {}", request.getConversationId());
        return ResponseEntity.ok(chatService.sendMessage(request));
    }

    @PutMapping("/messages/{id}/read")
    @Operation(summary = "Mark message as read")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long id) {
        chatService.markMessageAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/conversations/{conversationId}/read")
    @Operation(summary = "Mark all messages in conversation as read")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long conversationId) {
        chatService.markAllMessagesAsRead(conversationId);
        return ResponseEntity.ok().build();
    }
}
