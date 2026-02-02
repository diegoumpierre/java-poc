package com.poc.tenant.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes messages from Dead Letter Topics (DLT).
 * Messages that fail all retry attempts are routed here for monitoring and investigation.
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class DeadLetterConsumer {

    @KafkaListener(
            topics = "${app.kafka.topics.billing-events:billing-events}.DLT",
            groupId = "tenant-service-dlt"
    )
    public void handleBillingEventsDlt(String message) {
        log.error("[DLT] Failed message from billing-events: {}", message);
    }

    @KafkaListener(
            topics = "${app.kafka.topics.user-events:user-events}.DLT",
            groupId = "tenant-service-dlt"
    )
    public void handleUserEventsDlt(String message) {
        log.error("[DLT] Failed message from user-events: {}", message);
    }
}
