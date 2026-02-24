package com.poc.notification.domain;

public enum EmailStatus {
    PENDING,      // Saved, waiting for scheduled time
    QUEUED,       // In Kafka queue
    SENDING,      // Being processed
    SENT,         // Successfully sent
    FAILED,       // Failed, will retry
    DEAD          // Max retries exceeded
}
