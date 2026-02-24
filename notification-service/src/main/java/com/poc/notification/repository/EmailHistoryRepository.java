package com.poc.notification.repository;

import com.poc.notification.domain.EmailHistory;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailHistoryRepository extends CrudRepository<EmailHistory, Long> {

    Optional<EmailHistory> findByMessageId(String messageId);

    List<EmailHistory> findByUserIdOrderByCreatedAtDesc(String userId);

    @Query("SELECT * FROM NOTF_EMAIL_HISTORY WHERE STATUS = :status AND SCHEDULED_AT <= :now")
    List<EmailHistory> findByStatusAndScheduledAtBefore(
            @Param("status") String status,
            @Param("now") Instant now);

    @Query("SELECT * FROM NOTF_EMAIL_HISTORY WHERE STATUS = :status AND RETRY_COUNT < :maxRetries AND CONFIG_TYPE = 'NOTIFICATION'")
    List<EmailHistory> findTemplateByStatusAndRetryCountLessThan(
            @Param("status") String status,
            @Param("maxRetries") int maxRetries);

    @Modifying
    @Query("UPDATE NOTF_EMAIL_HISTORY SET STATUS = :status, UPDATED_AT = :now WHERE ID = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status, @Param("now") Instant now);

    // Multi-tenant methods
    List<EmailHistory> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<EmailHistory> findByUserIdAndTenantIdOrderByCreatedAtDesc(String userId, String tenantId);

    // Direct email (ATENDIMENTO) queries
    @Query("SELECT * FROM NOTF_EMAIL_HISTORY WHERE CONVERSATION_ID = :conversationId AND CONFIG_TYPE = 'ATENDIMENTO' ORDER BY CREATED_AT ASC")
    List<EmailHistory> findByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT * FROM NOTF_EMAIL_HISTORY WHERE STATUS = 'QUEUED' AND CONFIG_TYPE = 'ATENDIMENTO' ORDER BY CREATED_AT ASC LIMIT 50")
    List<EmailHistory> findQueuedDirectMessages();

    @Query("SELECT * FROM NOTF_EMAIL_HISTORY WHERE STATUS = 'FAILED' AND RETRY_COUNT < 3 AND CONFIG_TYPE = 'ATENDIMENTO' ORDER BY CREATED_AT ASC LIMIT 50")
    List<EmailHistory> findRetryableDirectMessages();

    @Query("SELECT * FROM NOTF_EMAIL_HISTORY WHERE TENANT_ID = :tenantId AND STATUS = :status AND CONFIG_TYPE = 'ATENDIMENTO' ORDER BY CREATED_AT ASC")
    List<EmailHistory> findDirectByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") String status);
}
