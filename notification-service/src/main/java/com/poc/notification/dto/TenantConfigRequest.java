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
public class TenantConfigRequest {

    private String configType;

    @NotBlank(message = "SMTP host is required")
    private String smtpHost;
    private Integer smtpPort;
    @NotBlank(message = "SMTP username is required")
    private String smtpUsername;
    @NotBlank(message = "SMTP password is required")
    private String smtpPassword;
    private Boolean smtpUseTls;

    private String imapHost;
    private Integer imapPort;
    private String imapUsername;
    private String imapPassword;
    private Boolean imapUseTls;
    private String imapFolder;

    @NotBlank(message = "From address is required")
    @Email(message = "From address must be a valid email")
    private String fromAddress;
    private String fromName;
    private String replyTo;
    private String baseUrl;

    private Integer pollIntervalSeconds;
    private Integer maxEmailsPerMinute;
    private Integer maxEmailsPerHour;
    private Integer maxEmailsPerDay;
    private Integer cooldownSeconds;
    private Boolean enabled;
}
