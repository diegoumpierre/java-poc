package com.poc.chat.dto.event;

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
}
