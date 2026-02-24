package com.poc.notification.controller;

import com.poc.notification.domain.ConfigType;
import com.poc.notification.domain.TenantConfig;
import com.poc.notification.dto.*;
import com.poc.notification.service.ConversationService;
import com.poc.notification.service.DirectEmailService;
import com.poc.notification.service.RateLimitService;
import com.poc.notification.service.TenantConfigService;
import com.poc.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Backward compatibility controller for /api/email/* endpoints.
 * Delegates to the same services as the new /api/notification/* endpoints.
 * This allows chat-service and other consumers to migrate gradually.
 */
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Hidden
public class LegacyEmailController {

    private final TenantConfigService tenantConfigService;
    private final DirectEmailService directEmailService;
    private final ConversationService conversationService;
    private final RateLimitService rateLimitService;

    // --- Config ---

    @PostMapping("/config")
    public ResponseEntity<TenantConfig> createOrUpdateConfig(@Valid @RequestBody TenantConfigRequest request) {
        if (request.getConfigType() == null) {
            request.setConfigType(ConfigType.ATENDIMENTO.name());
        }
        return ResponseEntity.ok(tenantConfigService.createOrUpdate(request));
    }

    @GetMapping("/config")
    public ResponseEntity<List<TenantConfig>> getConfig() {
        return ResponseEntity.ok(tenantConfigService.getConfigs());
    }

    @GetMapping("/config/status")
    public ResponseEntity<Map<String, Object>> testConnection() {
        return ResponseEntity.ok(tenantConfigService.testConnection(ConfigType.ATENDIMENTO.name()));
    }

    @PostMapping("/config/test-send")
    public ResponseEntity<Map<String, String>> testSend(@RequestParam String to) {
        directEmailService.sendTestEmail(to);
        return ResponseEntity.ok(Map.of("status", "sent", "to", to));
    }

    // --- Messages ---

    @PostMapping("/messages/send")
    public ResponseEntity<SendEmailResponse> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        return ResponseEntity.ok(directEmailService.queueEmail(request));
    }

    // --- Rate Limit ---

    @GetMapping("/rate-limit/status")
    public ResponseEntity<RateLimitStatusDTO> getRateLimitStatus() {
        String tenantId = TenantContext.getCurrentTenant().toString();
        return ResponseEntity.ok(rateLimitService.getStatus(tenantId, ConfigType.ATENDIMENTO.name()));
    }

    // --- Conversations ---

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> listConversations() {
        return ResponseEntity.ok(conversationService.listConversations());
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<ConversationDTO> getConversation(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getConversation(id));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(conversationService.getMessages(id));
    }

    @PostMapping("/conversations/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        conversationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
