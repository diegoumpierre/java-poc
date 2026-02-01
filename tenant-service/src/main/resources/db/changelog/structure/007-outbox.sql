--liquibase formatted sql

-- ============================================================================
-- TENANT SERVICE - Outbox Events Table
-- Transactional Outbox Pattern for reliable Kafka event publishing
-- ============================================================================

--changeset tenant:v2-create-tnt-outbox-events
CREATE TABLE IF NOT EXISTS TNT_OUTBOX_EVENTS (
    ID CHAR(36) NOT NULL,
    TOPIC VARCHAR(255) NOT NULL,
    EVENT_KEY VARCHAR(255),
    PAYLOAD LONGTEXT NOT NULL,
    STATUS VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    RETRY_COUNT INT NOT NULL DEFAULT 0,
    MAX_RETRIES INT NOT NULL DEFAULT 5,
    ERROR_MESSAGE VARCHAR(2000),
    CREATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    SENT_AT TIMESTAMP(6),
    PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--changeset tenant:v2-idx-tnt-outbox-status
CREATE INDEX IDX_TNT_OUTBOX_STATUS ON TNT_OUTBOX_EVENTS (STATUS, CREATED_AT);

--changeset tenant:v2-idx-tnt-outbox-cleanup
CREATE INDEX IDX_TNT_OUTBOX_CLEANUP ON TNT_OUTBOX_EVENTS (STATUS, SENT_AT);
