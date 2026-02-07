package com.poc.tenant.repository;

import com.poc.tenant.domain.OutboxEvent;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends CrudRepository<OutboxEvent, UUID> {

    @Query("SELECT * FROM TNT_OUTBOX_EVENTS WHERE STATUS = 'PENDING' ORDER BY CREATED_AT ASC LIMIT :limit")
    List<OutboxEvent> findPendingEvents(@Param("limit") int limit);

    @Modifying
    @Query("UPDATE TNT_OUTBOX_EVENTS SET STATUS = 'SENT', SENT_AT = CURRENT_TIMESTAMP(6) WHERE ID = :id")
    void markAsSent(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE TNT_OUTBOX_EVENTS SET STATUS = 'FAILED', RETRY_COUNT = RETRY_COUNT + 1, ERROR_MESSAGE = :errorMessage WHERE ID = :id")
    void markAsFailed(@Param("id") UUID id, @Param("errorMessage") String errorMessage);

    @Modifying
    @Query("UPDATE TNT_OUTBOX_EVENTS SET RETRY_COUNT = RETRY_COUNT + 1, ERROR_MESSAGE = :errorMessage WHERE ID = :id")
    void incrementRetry(@Param("id") UUID id, @Param("errorMessage") String errorMessage);

    @Modifying
    @Query("DELETE FROM TNT_OUTBOX_EVENTS WHERE STATUS = 'SENT' AND SENT_AT < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 7 DAY)")
    void cleanupOldEvents();

    @Query("SELECT COUNT(*) FROM TNT_OUTBOX_EVENTS WHERE STATUS = 'PENDING'")
    long countPending();

    @Query("SELECT COUNT(*) FROM TNT_OUTBOX_EVENTS WHERE STATUS = 'FAILED'")
    long countFailed();
}
