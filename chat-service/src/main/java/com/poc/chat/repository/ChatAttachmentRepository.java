package com.poc.chat.repository;

import com.poc.chat.domain.ChatAttachment;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatAttachmentRepository extends CrudRepository<ChatAttachment, Long>, ChatAttachmentRepositoryCustom {

    @Query("SELECT * FROM CHAT_ATTACHMENT WHERE MESSAGE_ID = :messageId")
    List<ChatAttachment> findByMessageId(@Param("messageId") Long messageId);
}
