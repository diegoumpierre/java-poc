package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatSession;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LiveChatSessionRepositoryCustom {

    Optional<LiveChatSession> findBySessionToken(String token);

    List<LiveChatSession> findByTenantIdAndStatus(UUID tenantId, String status);

    List<LiveChatSession> findByTenantIdAndStatus(UUID tenantId, String status, String sourceService);

    List<LiveChatSession> findActiveByTenantId(UUID tenantId);

    List<LiveChatSession> findActiveByTenantId(UUID tenantId, String sourceService);

    List<LiveChatSession> findByTenantIdAndAssignedAgentId(UUID tenantId, Long agentId);

    List<LiveChatSession> findByTenantIdAndAssignedAgentId(UUID tenantId, Long agentId, String sourceService);

    List<LiveChatSession> findAllByTenantId(UUID tenantId);

    List<LiveChatSession> findAllByTenantId(UUID tenantId, String sourceService);

    List<LiveChatSession> findInactiveSessions(Instant cutoff);

    Optional<LiveChatSession> findOpenByExternalPhone(UUID tenantId, String phone);

    Optional<LiveChatSession> findOpenByExternalEmail(UUID tenantId, String email);

    void updateStatus(Long id, String status, Instant updatedAt);

    void updateAssignedAgent(Long id, Long agentId, Instant agentJoinedAt, Instant updatedAt);

    void updateRating(Long id, Integer rating, String feedback, Instant updatedAt);

    void incrementMessageCount(Long id, Instant lastActivityAt, Instant updatedAt);
}
