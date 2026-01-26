package com.poc.auth.repository;

import com.poc.auth.domain.AuditLog;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaRepositoryAuditLog extends CrudRepository<AuditLog, UUID> {

    // ==================== User-based Queries ====================

    /**
     * Find all audit logs for a specific user
     */
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find all audit logs for a specific email
     */
    List<AuditLog> findByEmailOrderByCreatedAtDesc(String email);

    /**
     * Find failed login attempts for an email within a time period
     */
    @Query("SELECT * FROM AUTH_AUDIT_LOG WHERE email = :email AND action = 'LOGIN' AND status = 'FAILURE' AND created_at >= :since ORDER BY created_at DESC")
    List<AuditLog> findFailedLoginAttempts(@Param("email") String email, @Param("since") Instant since);

    /**
     * Count failed login attempts for an IP within a time period
     */
    @Query("SELECT COUNT(*) FROM AUTH_AUDIT_LOG WHERE ip_address = :ipAddress AND action = 'LOGIN' AND status = 'FAILURE' AND created_at >= :since")
    long countFailedLoginAttemptsByIp(@Param("ipAddress") String ipAddress, @Param("since") Instant since);

    // ==================== Tenant-based Queries ====================

    /**
     * Find all audit logs for a specific tenant
     */
    List<AuditLog> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    /**
     * Find audit logs for a tenant since a specific time
     */
    List<AuditLog> findByTenantIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID tenantId, Instant since);

    /**
     * Find audit logs by tenant and action type
     */
    List<AuditLog> findByTenantIdAndActionOrderByCreatedAtDesc(UUID tenantId, String action);

    /**
     * Find failed actions for a tenant
     */
    @Query("SELECT * FROM AUTH_AUDIT_LOG WHERE tenant_id = :tenantId AND status IN ('FAILURE', 'DENIED') AND created_at >= :since ORDER BY created_at DESC")
    List<AuditLog> findFailedActionsByTenant(@Param("tenantId") UUID tenantId, @Param("since") Instant since);

    /**
     * Count actions by type for a tenant
     */
    @Query("SELECT COUNT(*) FROM AUTH_AUDIT_LOG WHERE tenant_id = :tenantId AND action = :action AND created_at >= :since")
    long countByTenantIdAndActionAndCreatedAtAfter(@Param("tenantId") UUID tenantId, @Param("action") String action, @Param("since") Instant since);

    // ==================== Security Monitoring Queries ====================

    /**
     * Find suspicious activities across all tenants
     */
    @Query("SELECT * FROM AUTH_AUDIT_LOG WHERE action = 'SUSPICIOUS_ACTIVITY' AND created_at >= :since ORDER BY created_at DESC")
    List<AuditLog> findSuspiciousActivitiesSince(@Param("since") Instant since);

    /**
     * Find all failed login attempts across tenants (for rate limiting detection)
     */
    @Query("SELECT * FROM AUTH_AUDIT_LOG WHERE action = 'LOGIN' AND status = 'FAILURE' AND created_at >= :since ORDER BY created_at DESC LIMIT :limit")
    List<AuditLog> findRecentFailedLogins(@Param("since") Instant since, @Param("limit") int limit);

    /**
     * Count logins by tenant in a time period
     */
    @Query("SELECT COUNT(*) FROM AUTH_AUDIT_LOG WHERE tenant_id = :tenantId AND action = 'LOGIN' AND status = 'SUCCESS' AND created_at >= :since")
    long countSuccessfulLoginsByTenant(@Param("tenantId") UUID tenantId, @Param("since") Instant since);

    /**
     * Find access denied events for a tenant
     */
    @Query("SELECT * FROM AUTH_AUDIT_LOG WHERE tenant_id = :tenantId AND status = 'DENIED' AND created_at >= :since ORDER BY created_at DESC")
    List<AuditLog> findAccessDeniedByTenant(@Param("tenantId") UUID tenantId, @Param("since") Instant since);

    // ==================== Resource-based Queries ====================

    /**
     * Find audit logs for a specific resource
     */
    List<AuditLog> findByResourceTypeAndResourceIdOrderByCreatedAtDesc(String resourceType, UUID resourceId);

    /**
     * Find audit logs by resource type for a tenant
     */
    List<AuditLog> findByTenantIdAndResourceTypeOrderByCreatedAtDesc(UUID tenantId, String resourceType);

    // ==================== Statistics Queries ====================

    /**
     * Count distinct users active in a tenant
     */
    @Query("SELECT COUNT(DISTINCT user_id) FROM AUTH_AUDIT_LOG WHERE tenant_id = :tenantId AND created_at >= :since AND user_id IS NOT NULL")
    long countDistinctActiveUsersByTenant(@Param("tenantId") UUID tenantId, @Param("since") Instant since);

    /**
     * Count total actions by tenant
     */
    @Query("SELECT COUNT(*) FROM AUTH_AUDIT_LOG WHERE tenant_id = :tenantId AND created_at >= :since")
    long countTotalActionsByTenant(@Param("tenantId") UUID tenantId, @Param("since") Instant since);
}
