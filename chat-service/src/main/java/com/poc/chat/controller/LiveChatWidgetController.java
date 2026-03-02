package com.poc.chat.controller;

import com.poc.chat.dto.livechat.LiveChatMessageDTO;
import com.poc.chat.dto.livechat.LiveChatSessionDTO;
import com.poc.chat.dto.livechat.LiveChatWidgetConfigDTO;
import com.poc.chat.dto.livechat.SendLiveChatMessageRequest;
import com.poc.chat.dto.livechat.StartLiveChatRequest;
import com.poc.chat.service.LiveChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/livechat/widget")
@RequiredArgsConstructor
@Tag(name = "LiveChat Widget", description = "Public endpoints for the live chat widget (no auth required)")
@Slf4j
public class LiveChatWidgetController {

    private final LiveChatService liveChatService;

    @GetMapping("/config")
    @Operation(summary = "Get widget configuration", description = "Returns the live chat widget configuration for the tenant, optionally filtered by sourceService")
    public ResponseEntity<LiveChatWidgetConfigDTO> getWidgetConfig(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false) String sourceService) {
        return ResponseEntity.ok(liveChatService.getWidgetConfig(UUID.fromString(tenantId), sourceService));
    }

    @PostMapping("/start")
    @Operation(summary = "Start a live chat session", description = "Creates a new live chat session for a visitor")
    public ResponseEntity<LiveChatSessionDTO> startChat(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestBody StartLiveChatRequest request,
            HttpServletRequest httpRequest) {
        String visitorIp = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        return ResponseEntity.ok(liveChatService.startChat(UUID.fromString(tenantId), request, visitorIp, userAgent));
    }

    @GetMapping("/session/{token}")
    @Operation(summary = "Resume a live chat session", description = "Returns session details for reconnection")
    public ResponseEntity<LiveChatSessionDTO> resumeSession(@PathVariable String token) {
        return ResponseEntity.ok(liveChatService.resumeChat(token));
    }

    @PostMapping("/session/{token}/messages")
    @Operation(summary = "Send a message as visitor", description = "Sends a message in the live chat session")
    public ResponseEntity<LiveChatMessageDTO> sendMessage(
            @PathVariable String token,
            @RequestBody SendLiveChatMessageRequest request) {
        return ResponseEntity.ok(liveChatService.sendVisitorMessage(token, request));
    }

    @GetMapping("/session/{token}/messages")
    @Operation(summary = "Get messages for visitor", description = "Returns messages for the live chat session, optionally since a given timestamp")
    public ResponseEntity<List<LiveChatMessageDTO>> getMessages(
            @PathVariable String token,
            @RequestParam(required = false) Instant since) {
        return ResponseEntity.ok(liveChatService.getMessagesForVisitor(token, since));
    }

    @PostMapping("/session/{token}/end")
    @Operation(summary = "End chat session", description = "Visitor ends the live chat session")
    public ResponseEntity<Void> endChat(@PathVariable String token) {
        liveChatService.endChatByVisitor(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/session/{token}/rate")
    @Operation(summary = "Rate chat session", description = "Visitor rates the live chat session after it ends")
    public ResponseEntity<Void> rateChat(
            @PathVariable String token,
            @RequestParam Integer rating,
            @RequestParam(required = false) String feedback) {
        liveChatService.rateChatByVisitor(token, rating, feedback);
        return ResponseEntity.ok().build();
    }
}
