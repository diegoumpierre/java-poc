package com.poc.tenant.membership.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.tenant.domain.OutboxEvent;
import com.poc.tenant.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Publishes membership events via the Transactional Outbox Pattern.
 * Events are saved to TNT_OUTBOX_EVENTS table within the current transaction,
 * then asynchronously published to Kafka by OutboxPublisher.
 */
@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class MembershipEventPublisher {

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.membership-events:membership-events}")
    private String membershipEventsTopic;

    public void publishMembershipCreated(UUID membershipId, UUID userId, UUID tenantId) {
        MembershipEventDTO event = MembershipEventDTO.created(membershipId, userId, tenantId);
        saveToOutbox(event, userId.toString());
    }

    public void publishMembershipDeleted(UUID membershipId, UUID userId, UUID tenantId) {
        MembershipEventDTO event = MembershipEventDTO.deleted(membershipId, userId, tenantId);
        saveToOutbox(event, userId.toString());
    }

    public void publishMembershipRolesUpdated(UUID membershipId, UUID userId, UUID tenantId) {
        MembershipEventDTO event = MembershipEventDTO.rolesUpdated(membershipId, userId, tenantId);
        saveToOutbox(event, userId.toString());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void saveToOutbox(MembershipEventDTO event, String key) {
        try {
            String json = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = OutboxEvent.create(membershipEventsTopic, key, json);
            outboxRepository.save(outboxEvent);
            log.debug("[MembershipEvent] Saved event to outbox: type={}, user={}, id={}",
                    event.getEventType(), event.getUserId(), outboxEvent.getId());
        } catch (JsonProcessingException e) {
            log.error("[MembershipEvent] Failed to serialize event: {}", e.getMessage());
        }
    }
}
