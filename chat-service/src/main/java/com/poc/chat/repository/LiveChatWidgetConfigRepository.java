package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatWidgetConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiveChatWidgetConfigRepository extends CrudRepository<LiveChatWidgetConfig, Long>, LiveChatWidgetConfigRepositoryCustom {
}
