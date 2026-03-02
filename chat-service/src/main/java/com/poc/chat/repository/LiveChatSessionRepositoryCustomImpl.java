package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatSession;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LiveChatSessionRepositoryCustomImpl implements LiveChatSessionRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public Optional<LiveChatSession> findBySessionToken(String token) {
        return jdbcClient.sql("""
                SELECT * FROM CHAT_LIVECHAT_SESSION
                WHERE SESSION_TOKEN = :token
                """)
                .param("token", token)
                .query(LiveChatSession.class)
                .optional();
    }

    @Override
    public List<LiveChatSession> findByTenantIdAndStatus(UUID tenantId, String status) {
        return findByTenantIdAndStatus(tenantId, status, null);
    }

    @Override
    public List<LiveChatSession> findByTenantIdAndStatus(UUID tenantId, String status, String sourceService) {
        String sql = "SELECT * FROM CHAT_LIVECHAT_SESSION WHERE TENANT_ID = :tenantId AND STATUS = :status";
        if (sourceService != null) {
            sql += " AND SOURCE_SERVICE = :sourceService";
        }
        sql += " ORDER BY CREATED_AT ASC";
        var query = jdbcClient.sql(sql)
                .param("tenantId", tenantId.toString())
                .param("status", status);
        if (sourceService != null) {
            query = query.param("sourceService", sourceService);
        }
        return query.query(LiveChatSession.class).list();
    }

    @Override
    public List<LiveChatSession> findActiveByTenantId(UUID tenantId) {
        return findActiveByTenantId(tenantId, null);
    }

    @Override
    public List<LiveChatSession> findActiveByTenantId(UUID tenantId, String sourceService) {
        String sql = "SELECT * FROM CHAT_LIVECHAT_SESSION WHERE TENANT_ID = :tenantId AND STATUS = 'ACTIVE'";
        if (sourceService != null) {
            sql += " AND SOURCE_SERVICE = :sourceService";
        }
        sql += " ORDER BY LAST_ACTIVITY_AT DESC";
        var query = jdbcClient.sql(sql)
                .param("tenantId", tenantId.toString());
        if (sourceService != null) {
            query = query.param("sourceService", sourceService);
        }
        return query.query(LiveChatSession.class).list();
    }

    @Override
    public List<LiveChatSession> findByTenantIdAndAssignedAgentId(UUID tenantId, Long agentId) {
        return findByTenantIdAndAssignedAgentId(tenantId, agentId, null);
    }

    @Override
    public List<LiveChatSession> findByTenantIdAndAssignedAgentId(UUID tenantId, Long agentId, String sourceService) {
        String sql = "SELECT * FROM CHAT_LIVECHAT_SESSION WHERE TENANT_ID = :tenantId AND ASSIGNED_AGENT_ID = :agentId";
        if (sourceService != null) {
            sql += " AND SOURCE_SERVICE = :sourceService";
        }
        sql += " ORDER BY LAST_ACTIVITY_AT DESC";
        var query = jdbcClient.sql(sql)
                .param("tenantId", tenantId.toString())
                .param("agentId", agentId);
        if (sourceService != null) {
            query = query.param("sourceService", sourceService);
        }
        return query.query(LiveChatSession.class).list();
    }

    @Override
    public List<LiveChatSession> findAllByTenantId(UUID tenantId) {
        return findAllByTenantId(tenantId, null);
    }

    @Override
    public List<LiveChatSession> findAllByTenantId(UUID tenantId, String sourceService) {
        String sql = "SELECT * FROM CHAT_LIVECHAT_SESSION WHERE TENANT_ID = :tenantId";
        if (sourceService != null) {
            sql += " AND SOURCE_SERVICE = :sourceService";
        }
        sql += " ORDER BY CREATED_AT DESC";
        var query = jdbcClient.sql(sql)
                .param("tenantId", tenantId.toString());
        if (sourceService != null) {
            query = query.param("sourceService", sourceService);
        }
        return query.query(LiveChatSession.class).list();
    }

    @Override
    public List<LiveChatSession> findInactiveSessions(Instant cutoff) {
        return jdbcClient.sql("""
                SELECT * FROM CHAT_LIVECHAT_SESSION
                WHERE STATUS IN ('WAITING', 'ACTIVE')
                AND LAST_ACTIVITY_AT < :cutoff
                """)
                .param("cutoff", cutoff)
                .query(LiveChatSession.class)
                .list();
    }

    @Override
    public void updateStatus(Long id, String status, Instant updatedAt) {
        jdbcClient.sql("""
                UPDATE CHAT_LIVECHAT_SESSION
                SET STATUS = :status, UPDATED_AT = :updatedAt
                WHERE ID = :id
                """)
                .param("id", id)
                .param("status", status)
                .param("updatedAt", updatedAt)
                .update();
    }

    @Override
    public void updateAssignedAgent(Long id, Long agentId, Instant agentJoinedAt, Instant updatedAt) {
        jdbcClient.sql("""
                UPDATE CHAT_LIVECHAT_SESSION
                SET ASSIGNED_AGENT_ID = :agentId, AGENT_JOINED_AT = :agentJoinedAt, UPDATED_AT = :updatedAt
                WHERE ID = :id
                """)
                .param("id", id)
                .param("agentId", agentId)
                .param("agentJoinedAt", agentJoinedAt)
                .param("updatedAt", updatedAt)
                .update();
    }

    @Override
    public void updateRating(Long id, Integer rating, String feedback, Instant updatedAt) {
        jdbcClient.sql("""
                UPDATE CHAT_LIVECHAT_SESSION
                SET RATING = :rating, FEEDBACK = :feedback, UPDATED_AT = :updatedAt
                WHERE ID = :id
                """)
                .param("id", id)
                .param("rating", rating)
                .param("feedback", feedback)
                .param("updatedAt", updatedAt)
                .update();
    }

    @Override
    public Optional<LiveChatSession> findOpenByExternalPhone(UUID tenantId, String phone) {
        return jdbcClient.sql("""
                SELECT * FROM CHAT_LIVECHAT_SESSION
                WHERE TENANT_ID = :tenantId
                AND EXTERNAL_CONTACT_PHONE = :phone
                AND STATUS IN ('WAITING', 'ACTIVE')
                ORDER BY CREATED_AT DESC LIMIT 1
                """)
                .param("tenantId", tenantId.toString())
                .param("phone", phone)
                .query(LiveChatSession.class)
                .optional();
    }

    @Override
    public Optional<LiveChatSession> findOpenByExternalEmail(UUID tenantId, String email) {
        return jdbcClient.sql("""
                SELECT * FROM CHAT_LIVECHAT_SESSION
                WHERE TENANT_ID = :tenantId
                AND EXTERNAL_CONTACT_EMAIL = :email
                AND STATUS IN ('WAITING', 'ACTIVE')
                ORDER BY CREATED_AT DESC LIMIT 1
                """)
                .param("tenantId", tenantId.toString())
                .param("email", email)
                .query(LiveChatSession.class)
                .optional();
    }

    @Override
    public void incrementMessageCount(Long id, Instant lastActivityAt, Instant updatedAt) {
        jdbcClient.sql("""
                UPDATE CHAT_LIVECHAT_SESSION
                SET MESSAGE_COUNT = MESSAGE_COUNT + 1, LAST_ACTIVITY_AT = :lastActivityAt, UPDATED_AT = :updatedAt
                WHERE ID = :id
                """)
                .param("id", id)
                .param("lastActivityAt", lastActivityAt)
                .param("updatedAt", updatedAt)
                .update();
    }
}
