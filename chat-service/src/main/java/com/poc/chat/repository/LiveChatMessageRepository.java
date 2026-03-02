package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiveChatMessageRepository extends CrudRepository<LiveChatMessage, Long>, LiveChatMessageRepositoryCustom {
}
