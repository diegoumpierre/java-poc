package com.poc.auth.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.auth.service.impl.CachedMembershipQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listens to membership events from organization-service.
 * Invalidates membership cache when memberships change.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class MembershipEventListener {

    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private CachedMembershipQueryService cachedMembershipQueryService;

    @KafkaListener(
            topics = "${app.kafka.topics.membership-events:membership-events}",
            groupId = "${spring.kafka.consumer.group-id:auth-service}"
    )
    public void handleMembershipEvent(String message) {
        try {
            MembershipEventDTO event = objectMapper.readValue(message, MembershipEventDTO.class);
            log.info("[MembershipEventListener] Received {} for user {} (membership: {})",
                    event.getEventType(), event.getUserId(), event.getMembershipId());

            // Invalidate cache if available
            if (cachedMembershipQueryService != null && event.getUserId() != null) {
                cachedMembershipQueryService.invalidateUserCache(event.getUserId());

                if (event.getMembershipId() != null) {
                    cachedMembershipQueryService.invalidateMembershipCache(event.getMembershipId());
                }

                log.info("[MembershipEventListener] Cache invalidated for user {}", event.getUserId());
            } else {
                log.debug("[MembershipEventListener] Cache service not available, skipping invalidation");
            }

        } catch (Exception e) {
            log.error("[MembershipEventListener] Failed to process event: {}", e.getMessage(), e);
        }
    }
}
