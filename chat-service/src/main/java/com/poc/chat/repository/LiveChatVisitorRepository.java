package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatVisitor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiveChatVisitorRepository extends CrudRepository<LiveChatVisitor, Long>, LiveChatVisitorRepositoryCustom {
}
