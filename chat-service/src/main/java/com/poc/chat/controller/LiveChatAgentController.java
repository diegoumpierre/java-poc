package com.poc.chat.controller;

import com.poc.chat.dto.livechat.LiveChatMessageDTO;
import com.poc.chat.dto.livechat.LiveChatSessionDTO;
import com.poc.chat.dto.livechat.LiveChatStatsDTO;
import com.poc.chat.dto.livechat.SendLiveChatMessageRequest;
import com.poc.chat.service.ChatService;
import com.poc.chat.service.LiveChatService;
import com.poc.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/livechat/sessions")
@RequiredArgsConstructor
@Tag(name = "LiveChat Agent", description = "Authenticated endpoints for agents to manage live chat sessions")
@Slf4j
public class LiveChatAgentController {

    private final LiveChatService liveChatService;
    private final ChatService chatService;

    @GetMapping("/waiting")
    @Operation(summary = "Get waiting sessions", description = "Returns all sessions in WAITING status for the tenant. Optionally filter by sourceService.")
    public ResponseEntity<List<LiveChatSessionDTO>> getWaitingSessions(
            @RequestParam(required = false) String sourceService) {
        return ResponseEntity.ok(liveChatService.getWaitingSessions(sourceService));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active sessions", description = "Returns all sessions in ACTIVE status for the tenant. Optionally filter by sourceService.")
    public ResponseEntity<List<LiveChatSessionDTO>> getActiveSessions(
            @RequestParam(required = false) String sourceService) {
        return ResponseEntity.ok(liveChatService.getActiveSessions(sourceService));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my sessions", description = "Returns all sessions assigned to the current agent. Optionally filter by sourceService.")
    public ResponseEntity<List<LiveChatSessionDTO>> getMySessions(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String sourceService) {
        Long agentId = resolveAgentId(userId);
        return ResponseEntity.ok(liveChatService.getAgentSessions(agentId, sourceService));
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Accept a waiting session", description = "Agent accepts a session from the waiting queue")
    public ResponseEntity<LiveChatSessionDTO> acceptChat(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        Long agentId = resolveAgentId(userId);
        return ResponseEntity.ok(liveChatService.acceptChat(id, agentId));
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Send message as agent", description = "Agent sends a message in an active session")
    public ResponseEntity<LiveChatMessageDTO> sendMessage(
            @PathVariable Long id,
            @RequestBody SendLiveChatMessageRequest request,
            @RequestHeader("X-User-Id") String userId) {
        Long agentId = resolveAgentId(userId);
        return ResponseEntity.ok(liveChatService.sendAgentMessage(id, request, agentId));
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Get session messages", description = "Returns all messages for a session")
    public ResponseEntity<List<LiveChatMessageDTO>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(liveChatService.getMessagesForAgent(id));
    }

    @PostMapping("/{id}/transfer")
    @Operation(summary = "Transfer session to another agent", description = "Transfers the session to a different agent")
    public ResponseEntity<LiveChatSessionDTO> transferChat(
            @PathVariable Long id,
            @RequestParam Long newAgentId,
            @RequestHeader("X-User-Id") String userId) {
        Long currentAgentId = resolveAgentId(userId);
        return ResponseEntity.ok(liveChatService.transferChat(id, newAgentId, currentAgentId));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close a session", description = "Agent closes a live chat session")
    public ResponseEntity<Void> closeChat(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        Long agentId = resolveAgentId(userId);
        liveChatService.closeChat(id, agentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get session details", description = "Returns details of a specific session")
    public ResponseEntity<LiveChatSessionDTO> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(liveChatService.getSession(id));
    }

    @GetMapping
    @Operation(summary = "Get all sessions", description = "Returns all live chat sessions for the tenant. Optionally filter by sourceService.")
    public ResponseEntity<List<LiveChatSessionDTO>> getAllSessions(
            @RequestParam(required = false) String sourceService) {
        return ResponseEntity.ok(liveChatService.getAllSessions(sourceService));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get live chat statistics", description = "Returns statistics about live chat sessions for the tenant")
    public ResponseEntity<LiveChatStatsDTO> getStats() {
        return ResponseEntity.ok(liveChatService.getChatStats());
    }

    private Long resolveAgentId(String externalUserId) {
        UUID userId = UUID.fromString(externalUserId);
        UUID tenantId = TenantContext.getCurrentTenant();
        return chatService.getOrCreateChatUser(
                userId, tenantId, "Agent", externalUserId + "@pending", null
        ).getId();
    }
}
