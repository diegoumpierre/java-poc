package com.poc.chat.controller;

import com.poc.chat.dto.livechat.LiveChatWidgetConfigDTO;
import com.poc.chat.service.LiveChatService;
import com.poc.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/livechat/admin")
@RequiredArgsConstructor
@Tag(name = "LiveChat Admin", description = "Admin endpoints for managing live chat configuration")
public class LiveChatAdminController {

    private final LiveChatService liveChatService;

    @GetMapping("/widget-config")
    @Operation(summary = "Get widget configuration", description = "Returns the live chat widget configuration for the current tenant, optionally for a specific sourceService")
    public ResponseEntity<LiveChatWidgetConfigDTO> getWidgetConfig(
            @RequestParam(required = false) String sourceService) {
        UUID tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(liveChatService.getWidgetConfig(tenantId, sourceService));
    }

    @GetMapping("/widget-configs")
    @Operation(summary = "List all widget configurations", description = "Returns all widget configurations for the current tenant (default + per-sourceService)")
    public ResponseEntity<List<LiveChatWidgetConfigDTO>> getAllWidgetConfigs() {
        UUID tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(liveChatService.getAllWidgetConfigs(tenantId));
    }

    @PutMapping("/widget-config")
    @Operation(summary = "Update widget configuration", description = "Updates the live chat widget configuration for the current tenant, optionally for a specific sourceService")
    public ResponseEntity<LiveChatWidgetConfigDTO> updateWidgetConfig(
            @RequestBody LiveChatWidgetConfigDTO config,
            @RequestParam(required = false) String sourceService) {
        UUID tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(liveChatService.updateWidgetConfig(tenantId, config, sourceService));
    }
}
