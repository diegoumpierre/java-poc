package com.poc.notification.repository;

import com.poc.notification.domain.Conversation;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends CrudRepository<Conversation, Long> {

    @Query("SELECT * FROM NOTF_CONVERSATIONS WHERE TENANT_ID = :tenantId ORDER BY LAST_MESSAGE_AT DESC")
    List<Conversation> findByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT * FROM NOTF_CONVERSATIONS WHERE TENANT_ID = :tenantId AND CONTACT_EMAIL = :contactEmail AND THREAD_ID = :threadId")
    Optional<Conversation> findByTenantIdAndContactEmailAndThreadId(
            @Param("tenantId") String tenantId,
            @Param("contactEmail") String contactEmail,
            @Param("threadId") String threadId);

    @Query("SELECT * FROM NOTF_CONVERSATIONS WHERE TENANT_ID = :tenantId AND CONTACT_EMAIL = :contactEmail AND THREAD_ID IS NULL LIMIT 1")
    Optional<Conversation> findByTenantIdAndContactEmailNoThread(
            @Param("tenantId") String tenantId,
            @Param("contactEmail") String contactEmail);

    @Query("SELECT * FROM NOTF_CONVERSATIONS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<Conversation> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") String tenantId);

    @Modifying
    @Query("UPDATE NOTF_CONVERSATIONS SET LAST_MESSAGE_AT = :lastMessageAt, LAST_MESSAGE_PREVIEW = :preview, UNREAD_COUNT = UNREAD_COUNT + :increment, UPDATED_AT = :updatedAt WHERE ID = :id")
    void updateLastMessage(@Param("id") Long id, @Param("lastMessageAt") Instant lastMessageAt,
                           @Param("preview") String preview, @Param("increment") int increment,
                           @Param("updatedAt") Instant updatedAt);
}
