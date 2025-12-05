package com.poc.shared.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * DTO for notification events published to Kafka.
 * Used by all services to send notifications asynchronously
 * instead of Feign calls to notification-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDTO {

    private String to;                     // email recipient
    private String template;               // template name (e.g., "WELCOME", "INVOICE_CREATED")
    private String subject;                // email subject (optional, uses template default if null)
    private Map<String, Object> variables; // template variables
    private UUID tenantId;                 // tenant context
    private String sourceService;          // which service sent it (for tracking/debugging)
    private Long timestamp;                // event timestamp

    /**
     * Create a notification event with automatic timestamp.
     */
    public static NotificationEventDTO of(String to, String template, Map<String, Object> variables,
                                           UUID tenantId, String sourceService) {
        return NotificationEventDTO.builder()
                .to(to)
                .template(template)
                .variables(variables)
                .tenantId(tenantId)
                .sourceService(sourceService)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Create a notification event with subject override.
     */
    public static NotificationEventDTO of(String to, String template, String subject,
                                           Map<String, Object> variables,
                                           UUID tenantId, String sourceService) {
        return NotificationEventDTO.builder()
                .to(to)
                .template(template)
                .subject(subject)
                .variables(variables)
                .tenantId(tenantId)
                .sourceService(sourceService)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
