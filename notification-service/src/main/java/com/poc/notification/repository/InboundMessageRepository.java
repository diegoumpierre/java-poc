package com.poc.notification.repository;

import com.poc.notification.domain.InboundMessage;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InboundMessageRepository extends CrudRepository<InboundMessage, Long> {

    @Query("SELECT * FROM NOTF_INBOUND_MESSAGES WHERE CONVERSATION_ID = :conversationId ORDER BY CREATED_AT ASC")
    List<InboundMessage> findByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT * FROM NOTF_INBOUND_MESSAGES WHERE PROVIDER_MESSAGE_ID = :providerMessageId AND TENANT_ID = :tenantId")
    Optional<InboundMessage> findByProviderMessageIdAndTenantId(
            @Param("providerMessageId") String providerMessageId,
            @Param("tenantId") String tenantId);

    @Modifying
    @Query("UPDATE NOTF_INBOUND_MESSAGES SET IS_READ = 1 WHERE CONVERSATION_ID = :conversationId AND IS_READ = 0")
    void markAllReadByConversationId(@Param("conversationId") Long conversationId);
}
