package com.poc.chat.repository;

import java.util.UUID;

public interface ChatUserRepositoryCustom {

    long countByTenantId(UUID tenantId);
}
