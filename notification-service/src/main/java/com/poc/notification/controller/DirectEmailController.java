package com.poc.notification.controller;

import com.poc.notification.domain.ConfigType;
import com.poc.notification.dto.RateLimitStatusDTO;
import com.poc.notification.dto.SendEmailRequest;
import com.poc.notification.dto.SendEmailResponse;
import com.poc.notification.service.DirectEmailService;
import com.poc.notification.service.RateLimitService;
import com.poc.shared.tenant.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "Direct Email", description = "Direct email sending (atendimento)")
public class DirectEmailController {

    private final DirectEmailService directEmailService;
    private final RateLimitService rateLimitService;

    @PostMapping("/messages/send")
    @Operation(summary = "Queue a direct email for sending")
    public ResponseEntity<SendEmailResponse> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        return ResponseEntity.ok(directEmailService.queueEmail(request));
    }

    @GetMapping("/rate-limit/status")
    @Operation(summary = "Get rate limit status for current tenant")
    public ResponseEntity<RateLimitStatusDTO> getRateLimitStatus(
            @RequestParam(defaultValue = "ATENDIMENTO") String configType) {
        String tenantId = TenantContext.getCurrentTenant().toString();
        return ResponseEntity.ok(rateLimitService.getStatus(tenantId, configType));
    }
}
