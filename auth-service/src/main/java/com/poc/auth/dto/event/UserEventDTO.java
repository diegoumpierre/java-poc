package com.poc.auth.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDTO {
    private String eventType;
    private UUID userId;
    private UUID tenantId;
    private String name;
    private String email;
    private String avatarUrl;
    private Long timestamp;

    public enum EventType {
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED
    }

    public static UserEventDTO created(UUID userId, UUID tenantId, String name, String email, String avatarUrl) {
        return UserEventDTO.builder()
                .eventType(EventType.USER_CREATED.name())
                .userId(userId)
                .tenantId(tenantId)
                .name(name)
                .email(email)
                .avatarUrl(avatarUrl)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static UserEventDTO updated(UUID userId, UUID tenantId, String name, String email, String avatarUrl) {
        return UserEventDTO.builder()
                .eventType(EventType.USER_UPDATED.name())
                .userId(userId)
                .tenantId(tenantId)
                .name(name)
                .email(email)
                .avatarUrl(avatarUrl)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static UserEventDTO deleted(UUID userId, UUID tenantId) {
        return UserEventDTO.builder()
                .eventType(EventType.USER_DELETED.name())
                .userId(userId)
                .tenantId(tenantId)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
