package com.poc.chat.repository;

import com.poc.chat.domain.ChatMention;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMentionRepository extends CrudRepository<ChatMention, Long>, ChatMentionRepositoryCustom {

    @Query("SELECT * FROM CHAT_MENTION WHERE MESSAGE_ID = :messageId")
    List<ChatMention> findByMessageId(@Param("messageId") Long messageId);

    @Query("SELECT * FROM CHAT_MENTION WHERE MENTIONED_USER_ID = :userId ORDER BY CREATED_AT DESC LIMIT :limit")
    List<ChatMention> findByMentionedUserId(@Param("userId") Long userId, @Param("limit") int limit);
}
