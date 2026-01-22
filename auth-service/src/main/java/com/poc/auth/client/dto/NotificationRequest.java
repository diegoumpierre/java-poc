package com.poc.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    private String to;
    private String template;  // EmailTemplate name: "VERIFICATION", "WELCOME", etc.
    private String subject;
    private Map<String, Object> variables;
    private Instant scheduledAt;

    public static NotificationRequest passwordReset(String to, String userName, String resetToken) {
        return NotificationRequest.builder()
                .to(to)
                .template("PASSWORD_RESET")
                .variables(Map.of(
                        "userName", userName,
                        "resetToken", resetToken,
                        "expirationMinutes", 15
                ))
                .build();
    }

    public static NotificationRequest welcome(String to, String userName) {
        return NotificationRequest.builder()
                .to(to)
                .template("WELCOME")
                .variables(Map.of(
                        "userName", userName,
                        "loginLink", "http://localhost:3001/login"
                ))
                .build();
    }

    public static NotificationRequest verification(String to, String userName, String code) {
        return NotificationRequest.builder()
                .to(to)
                .template("VERIFICATION")
                .variables(Map.of(
                        "userName", userName,
                        "verificationCode", code,
                        "expirationMinutes", 15
                ))
                .build();
    }

    public static NotificationRequest twoFactorCode(String to, String userName, String code, int expirationMinutes) {
        return NotificationRequest.builder()
                .to(to)
                .template("TWO_FACTOR")
                .variables(Map.of(
                        "userName", userName,
                        "verificationCode", code,
                        "expirationMinutes", expirationMinutes
                ))
                .build();
    }

    public static NotificationRequest newDeviceLogin(String to, String userName, String deviceInfo, String ipAddress, String location) {
        return NotificationRequest.builder()
                .to(to)
                .template("NEW_DEVICE_LOGIN")
                .variables(Map.of(
                        "userName", userName,
                        "deviceInfo", deviceInfo,
                        "ipAddress", ipAddress,
                        "location", location != null ? location : "Unknown"
                ))
                .build();
    }
}
