package com.poc.auth.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.poc.auth.domain.UserSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponse {

    private UUID id;
    private String deviceName;
    private String deviceType;
    private String browser;
    private String operatingSystem;
    private String ipAddress;
    private String location;
    private boolean isCurrent;
    private Instant lastActivityAt;
    private Instant createdAt;

    public static SessionResponse fromEntity(UserSession session) {
        return SessionResponse.builder()
                .id(session.getId())
                .deviceName(session.getDeviceName())
                .deviceType(session.getDeviceType())
                .browser(session.getBrowser())
                .operatingSystem(session.getOperatingSystem())
                .ipAddress(session.getIpAddress())
                .location(session.getLocation())
                .isCurrent(Boolean.TRUE.equals(session.getIsCurrent()))
                .lastActivityAt(session.getLastActivityAt())
                .createdAt(session.getCreatedAt())
                .build();
    }
}
