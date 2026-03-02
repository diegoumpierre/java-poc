package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatVisitor;

import java.util.Optional;
import java.util.UUID;

public interface LiveChatVisitorRepositoryCustom {

    Optional<LiveChatVisitor> findByTenantIdAndVisitorId(UUID tenantId, String visitorId);
}
