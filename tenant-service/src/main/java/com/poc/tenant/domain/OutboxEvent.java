package com.poc.tenant.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("TNT_OUTBOX_EVENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("TOPIC")
    private String topic;

    @Column("EVENT_KEY")
    private String eventKey;

    @Column("PAYLOAD")
    private String payload;

    @Column("STATUS")
    @Builder.Default
    private String status = "PENDING";

    @Column("RETRY_COUNT")
    @Builder.Default
    private Integer retryCount = 0;

    @Column("MAX_RETRIES")
    @Builder.Default
    private Integer maxRetries = 5;

    @Column("ERROR_MESSAGE")
    private String errorMessage;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("SENT_AT")
    private Instant sentAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }

    public static OutboxEvent create(String topic, String eventKey, String payload) {
        return OutboxEvent.builder()
                .id(UUID.randomUUID())
                .topic(topic)
                .eventKey(eventKey)
                .payload(payload)
                .status("PENDING")
                .retryCount(0)
                .maxRetries(5)
                .createdAt(Instant.now())
                .build();
    }
}
