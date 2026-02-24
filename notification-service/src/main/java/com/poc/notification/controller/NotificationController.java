package com.poc.notification.controller;

import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.EmailTemplate;
import com.poc.notification.dto.NotificationRequest;
import com.poc.notification.dto.NotificationResponse;
import com.poc.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Email notification endpoints")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    @Operation(summary = "Queue an email for sending")
    public ResponseEntity<NotificationResponse> send(
            @RequestBody @Valid NotificationRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("Received notification request to: {} template: {}", request.getTo(), request.getTemplate());
        return ResponseEntity.ok(notificationService.queueEmail(request, userId));
    }

    @GetMapping("/history")
    @Operation(summary = "Get email history for current user")
    public ResponseEntity<List<EmailHistory>> getHistory(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(notificationService.getHistoryByUser(userId));
    }

    @GetMapping("/templates")
    @Operation(summary = "List available email templates")
    public ResponseEntity<List<Map<String, String>>> getTemplates() {
        return ResponseEntity.ok(
                Arrays.stream(EmailTemplate.values())
                        .map(t -> Map.of(
                                "name", t.name(),
                                "templateName", t.getTemplateName(),
                                "defaultSubject", t.getDefaultSubject()
                        ))
                        .toList()
        );
    }

    @GetMapping("/test-connection")
    @Operation(summary = "Test SMTP connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        return ResponseEntity.ok(notificationService.testSmtpConnection());
    }
}
