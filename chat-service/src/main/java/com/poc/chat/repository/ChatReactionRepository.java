package com.poc.chat.repository;

import com.poc.chat.domain.ChatReaction;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatReactionRepository extends CrudRepository<ChatReaction, Long>, ChatReactionRepositoryCustom {

    @Query("SELECT * FROM CHAT_REACTION WHERE MESSAGE_ID = :messageId")
    List<ChatReaction> findByMessageId(@Param("messageId") Long messageId);

    @Query("SELECT * FROM CHAT_REACTION WHERE MESSAGE_ID = :messageId AND USER_ID = :userId AND EMOJI = :emoji")
    Optional<ChatReaction> findByMessageIdAndUserIdAndEmoji(
            @Param("messageId") Long messageId,
            @Param("userId") Long userId,
            @Param("emoji") String emoji);

    @Modifying
    @Query("DELETE FROM CHAT_REACTION WHERE MESSAGE_ID = :messageId AND USER_ID = :userId AND EMOJI = :emoji")
    void deleteByMessageIdAndUserIdAndEmoji(
            @Param("messageId") Long messageId,
            @Param("userId") Long userId,
            @Param("emoji") String emoji);
}
