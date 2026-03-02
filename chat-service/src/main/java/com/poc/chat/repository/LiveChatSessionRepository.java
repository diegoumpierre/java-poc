package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiveChatSessionRepository extends CrudRepository<LiveChatSession, Long>, LiveChatSessionRepositoryCustom {
}
