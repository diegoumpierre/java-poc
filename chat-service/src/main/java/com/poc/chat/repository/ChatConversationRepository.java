package com.poc.chat.repository;

import com.poc.chat.domain.ChatConversation;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatConversationRepository extends CrudRepository<ChatConversation, Long>, ChatConversationRepositoryCustom {

    @Query("SELECT * FROM CHAT_CONVERSATION WHERE TENANT_ID = :tenantId " +
           "AND ((PARTICIPANT_ONE_ID = :userOneId AND PARTICIPANT_TWO_ID = :userTwoId) " +
           "OR (PARTICIPANT_ONE_ID = :userTwoId AND PARTICIPANT_TWO_ID = :userOneId))")
    Optional<ChatConversation> findByParticipants(@Param("tenantId") UUID tenantId,
                                                   @Param("userOneId") Long userOneId,
                                                   @Param("userTwoId") Long userTwoId);

    @Query("SELECT * FROM CHAT_CONVERSATION WHERE TENANT_ID = :tenantId " +
           "AND (PARTICIPANT_ONE_ID = :userId OR PARTICIPANT_TWO_ID = :userId) " +
           "ORDER BY LAST_MESSAGE_AT DESC NULLS LAST")
    List<ChatConversation> findByUserIdOrderByLastMessageAtDesc(@Param("tenantId") UUID tenantId,
                                                                 @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE CHAT_CONVERSATION SET LAST_MESSAGE_AT = :lastMessageAt, UPDATED_AT = :now WHERE ID = :id")
    void updateLastMessageAt(@Param("id") Long id, @Param("lastMessageAt") Instant lastMessageAt, @Param("now") Instant now);
}
