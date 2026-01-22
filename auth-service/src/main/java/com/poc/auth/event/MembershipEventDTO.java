package com.poc.auth.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for membership events received from organization-service.
 * Used to invalidate membership cache.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipEventDTO {

    private String eventType;
    private UUID membershipId;
    private UUID userId;
    private UUID tenantId;
    private Long timestamp;

    public enum EventType {
        MEMBERSHIP_CREATED,
        MEMBERSHIP_DELETED,
        MEMBERSHIP_ROLES_UPDATED
    }

    public boolean isMembershipCreated() {
        return EventType.MEMBERSHIP_CREATED.name().equals(eventType);
    }

    public boolean isMembershipDeleted() {
        return EventType.MEMBERSHIP_DELETED.name().equals(eventType);
    }

    public boolean isMembershipRolesUpdated() {
        return EventType.MEMBERSHIP_ROLES_UPDATED.name().equals(eventType);
    }
}
