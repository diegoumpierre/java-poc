package com.poc.chat.repository;

import com.poc.chat.domain.ChatChannel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatChannelRepository extends CrudRepository<ChatChannel, Long>, ChatChannelRepositoryCustom {
}
