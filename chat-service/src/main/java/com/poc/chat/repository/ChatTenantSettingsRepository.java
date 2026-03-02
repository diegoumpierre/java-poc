package com.poc.chat.repository;

import com.poc.chat.domain.ChatTenantSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatTenantSettingsRepository extends CrudRepository<ChatTenantSettings, Long>, ChatTenantSettingsRepositoryCustom {
}
