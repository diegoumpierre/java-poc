package com.poc.chat.repository;

import com.poc.chat.domain.ChatMessage;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long>, ChatMessageRepositoryCustom {

    @Query("SELECT * FROM CHAT_MESSAGE WHERE CONVERSATION_ID = :conversationId ORDER BY CREATED_AT ASC")
    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(@Param("conversationId") Long conversationId);

    @Query("SELECT * FROM CHAT_MESSAGE WHERE CONVERSATION_ID = :conversationId " +
           "ORDER BY CREATED_AT ASC LIMIT :limit OFFSET :offset")
    List<ChatMessage> findByConversationIdPaginated(@Param("conversationId") Long conversationId,
                                                     @Param("offset") int offset,
                                                     @Param("limit") int limit);

    @Query("SELECT COUNT(*) FROM CHAT_MESSAGE WHERE CONVERSATION_ID = :conversationId " +
           "AND SENDER_ID != :userId AND READ_AT IS NULL")
    int countUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE CHAT_MESSAGE SET READ_AT = :readAt, UPDATED_AT = :now WHERE ID = :id")
    void markAsRead(@Param("id") Long id, @Param("readAt") Instant readAt, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE CHAT_MESSAGE SET READ_AT = :readAt, UPDATED_AT = :now " +
           "WHERE CONVERSATION_ID = :conversationId AND SENDER_ID != :userId AND READ_AT IS NULL")
    void markAllAsRead(@Param("conversationId") Long conversationId, @Param("userId") Long userId,
                       @Param("readAt") Instant readAt, @Param("now") Instant now);

    @Query("SELECT * FROM CHAT_MESSAGE WHERE CONVERSATION_ID = :conversationId ORDER BY CREATED_AT DESC LIMIT 1")
    ChatMessage findLastMessage(@Param("conversationId") Long conversationId);

    // Channel-based queries
    @Query("SELECT * FROM CHAT_MESSAGE WHERE CHANNEL_ID = :channelId ORDER BY CREATED_AT ASC LIMIT :limit OFFSET :offset")
    List<ChatMessage> findByChannelIdPaginated(@Param("channelId") Long channelId,
                                                @Param("offset") int offset,
                                                @Param("limit") int limit);

    @Query("SELECT * FROM CHAT_MESSAGE WHERE CHANNEL_ID = :channelId ORDER BY CREATED_AT DESC LIMIT 1")
    ChatMessage findLastMessageInChannel(@Param("channelId") Long channelId);

    @Query("SELECT COUNT(*) FROM CHAT_MESSAGE WHERE CHANNEL_ID = :channelId " +
           "AND SENDER_ID != :userId AND CREATED_AT > :lastReadAt")
    int countUnreadInChannel(@Param("channelId") Long channelId, @Param("userId") Long userId,
                             @Param("lastReadAt") Instant lastReadAt);

    // Thread queries
    @Query("SELECT * FROM CHAT_MESSAGE WHERE PARENT_MESSAGE_ID = :parentMessageId ORDER BY CREATED_AT ASC")
    List<ChatMessage> findThreadReplies(@Param("parentMessageId") Long parentMessageId);

    @Query("SELECT * FROM CHAT_MESSAGE WHERE PARENT_MESSAGE_ID = :parentMessageId " +
           "ORDER BY CREATED_AT ASC LIMIT :limit OFFSET :offset")
    List<ChatMessage> findThreadRepliesPaginated(@Param("parentMessageId") Long parentMessageId,
                                                   @Param("offset") int offset,
                                                   @Param("limit") int limit);

    @Modifying
    @Query("UPDATE CHAT_MESSAGE SET REPLY_COUNT = REPLY_COUNT + 1, LAST_REPLY_AT = :lastReplyAt, " +
           "UPDATED_AT = :now WHERE ID = :parentMessageId")
    void incrementReplyCount(@Param("parentMessageId") Long parentMessageId,
                              @Param("lastReplyAt") Instant lastReplyAt,
                              @Param("now") Instant now);

    // Channel messages excluding thread replies (only top-level messages)
    @Query("SELECT * FROM CHAT_MESSAGE WHERE CHANNEL_ID = :channelId AND PARENT_MESSAGE_ID IS NULL " +
           "ORDER BY CREATED_AT ASC LIMIT :limit OFFSET :offset")
    List<ChatMessage> findTopLevelByChannelId(@Param("channelId") Long channelId,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);
}
