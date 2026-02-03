package com.poc.tenant.membership.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

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

    public static MembershipEventDTO created(UUID membershipId, UUID userId, UUID tenantId) {
        return MembershipEventDTO.builder()
                .eventType(EventType.MEMBERSHIP_CREATED.name())
                .membershipId(membershipId)
                .userId(userId)
                .tenantId(tenantId)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static MembershipEventDTO deleted(UUID membershipId, UUID userId, UUID tenantId) {
        return MembershipEventDTO.builder()
                .eventType(EventType.MEMBERSHIP_DELETED.name())
                .membershipId(membershipId)
                .userId(userId)
                .tenantId(tenantId)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static MembershipEventDTO rolesUpdated(UUID membershipId, UUID userId, UUID tenantId) {
        return MembershipEventDTO.builder()
                .eventType(EventType.MEMBERSHIP_ROLES_UPDATED.name())
                .membershipId(membershipId)
                .userId(userId)
                .tenantId(tenantId)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
