package com.poc.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailModel {

    private UUID id;

    // Sender info (for received mails)
    private String senderName;
    private String senderEmail;
    private String senderImage;

    // Recipient info
    private String to;
    private String toName;

    @NotBlank(message = "Email address is required")
    private String email;

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must be at most 500 characters")
    private String title;

    @Size(max = 50000, message = "Message must be at most 50000 characters")
    private String message;

    private String image;

    private String date;

    @Builder.Default
    private Boolean important = false;

    @Builder.Default
    private Boolean starred = false;

    @Builder.Default
    private Boolean trash = false;

    @Builder.Default
    private Boolean spam = false;

    @Builder.Default
    private Boolean archived = false;

    @Builder.Default
    private Boolean sent = false;

    @Builder.Default
    private Boolean read = false;
}
