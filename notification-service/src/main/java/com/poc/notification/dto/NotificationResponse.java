package com.poc.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private boolean success;
    private String message;
    private String messageId;

    public static NotificationResponse success(String messageId) {
        return new NotificationResponse(true, "Email queued successfully", messageId);
    }

    public static NotificationResponse error(String message) {
        return new NotificationResponse(false, message, null);
    }
}
