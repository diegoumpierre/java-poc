package com.poc.chat.repository;

import java.util.UUID;

public interface ChatConversationRepositoryCustom {

    long countByTenantId(UUID tenantId);
}
