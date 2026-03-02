package com.poc.chat.repository;

import com.poc.chat.domain.ChatChannelMember;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatChannelMemberRepository extends CrudRepository<ChatChannelMember, Long>, ChatChannelMemberRepositoryCustom {
}
