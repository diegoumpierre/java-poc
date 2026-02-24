package com.poc.notification.dto;

import com.poc.notification.domain.EmailTemplate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String to;

    @NotNull(message = "Template is required")
    private EmailTemplate template;

    private String subject;  // Optional, uses template default if null

    private Map<String, Object> variables;

    private Instant scheduledAt;  // null = send now
}
