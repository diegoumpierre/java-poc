package com.poc.auth.service;

import com.poc.auth.client.dto.NotificationRequest;
import com.poc.auth.client.dto.NotificationResponse;
import com.poc.shared.event.NotificationEventDTO;
import com.poc.shared.event.NotificationEventPublisher;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Service wrapper for notifications.
 * Publishes notification events to Kafka via NotificationEventPublisher.
 * When notification.enabled=false, logs the email content for testing instead.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationEventPublisher notificationEventPublisher;

    @Value("${app.notification.enabled:true}")
    private boolean notificationEnabled;

    /**
     * Send a notification. If notifications are disabled, logs the content instead.
     *
     * @param request The notification request
     * @return NotificationResponse (always success for fire-and-forget Kafka)
     */
    public NotificationResponse send(NotificationRequest request) {
        if (notificationEnabled) {
            UUID tenantId = TenantContext.getCurrentTenant();
            NotificationEventDTO event = request.getSubject() != null
                    ? NotificationEventDTO.of(
                        request.getTo(), request.getTemplate(), request.getSubject(),
                        request.getVariables(), tenantId, "auth-service")
                    : NotificationEventDTO.of(
                        request.getTo(), request.getTemplate(),
                        request.getVariables(), tenantId, "auth-service");

            notificationEventPublisher.send(event);
            log.debug("Notification event published for {} (template={})", request.getTo(), request.getTemplate());
            return NotificationResponse.builder()
                    .success(true)
                    .message("Notification event published to Kafka")
                    .build();
        } else {
            // Log the email content for testing
            logNotification(request);
            return NotificationResponse.builder()
                    .success(true)
                    .message("Notification logged (notification-service disabled)")
                    .messageId("mock-" + System.currentTimeMillis())
                    .build();
        }
    }

    private void logNotification(NotificationRequest request) {
        log.info("╔══════════════════════════════════════════════════════════════════╗");
        log.info("║                    NOTIFICATION (MOCK MODE)                       ║");
        log.info("╠══════════════════════════════════════════════════════════════════╣");
        log.info("║ To:       {}", padRight(request.getTo(), 54) + "║");
        log.info("║ Template: {}", padRight(request.getTemplate(), 54) + "║");
        if (request.getSubject() != null) {
            log.info("║ Subject:  {}", padRight(request.getSubject(), 54) + "║");
        }
        log.info("╠══════════════════════════════════════════════════════════════════╣");
        log.info("║ Variables:                                                        ║");
        if (request.getVariables() != null) {
            request.getVariables().forEach((key, value) -> {
                String line = String.format("  %s: %s", key, value);
                log.info("║ {}", padRight(line, 64) + "║");
            });
        }
        log.info("╚══════════════════════════════════════════════════════════════════╝");

        // Log verification code prominently if present
        if (request.getVariables() != null) {
            Object code = request.getVariables().get("verificationCode");
            if (code != null) {
                log.info("");
                log.info("  *** VERIFICATION CODE: {} ***", code);
                log.info("");
            }
            Object resetToken = request.getVariables().get("resetToken");
            if (resetToken != null) {
                log.info("");
                log.info("  *** RESET TOKEN: {} ***", resetToken);
                log.info("");
            }
        }
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() > n) {
            return s.substring(0, n - 3) + "...";
        }
        return String.format("%-" + n + "s", s);
    }

    /**
     * Convenience method to send an email notification.
     *
     * @param to Email recipient
     * @param template Email template name (e.g., "INVITE", "ACCESS_REQUEST")
     * @param variables Template variables
     * @return NotificationResponse
     */
    public NotificationResponse sendEmail(String to, String template, Map<String, Object> variables) {
        NotificationRequest request = NotificationRequest.builder()
                .to(to)
                .template(template)
                .variables(variables)
                .build();
        return send(request);
    }
}
