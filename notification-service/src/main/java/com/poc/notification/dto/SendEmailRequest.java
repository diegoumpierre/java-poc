package com.poc.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Recipient must be a valid email")
    private String recipientEmail;

    private String subject;

    private String contentText;

    private String contentHtml;

    private String inReplyTo;

    private String referencesHeader;

    private Long conversationId;
}
