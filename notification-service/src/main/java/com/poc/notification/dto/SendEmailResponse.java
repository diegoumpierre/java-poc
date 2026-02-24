package com.poc.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailResponse {

    private String messageId;
    private String status;
    private String recipientEmail;
    private Instant createdAt;
}
