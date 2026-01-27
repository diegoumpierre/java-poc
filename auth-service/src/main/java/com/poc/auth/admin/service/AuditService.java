package com.poc.auth.service;

import com.poc.auth.domain.AuditLog;
import com.poc.auth.repository.JpaRepositoryAuditLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final JpaRepositoryAuditLog auditLogRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Log an audit event asynchronously to avoid impacting performance
     * Uses REQUIRES_NEW to create independent transaction
     * Uses direct JDBC INSERT to avoid Spring Data JDBC UPDATE issues
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(AuditLog auditLog) {
        try {
            String sql = "INSERT INTO AUTH_AUDIT_LOG (id, tenant_id, user_id, email, action, status, resource_type, resource_id, ip_address, user_agent, failure_reason, metadata, created_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            String id = UUID.randomUUID().toString();
            String tenantId = auditLog.getTenantId() != null ? auditLog.getTenantId().toString() : null;
            String userId = auditLog.getUserId() != null ? auditLog.getUserId().toString() : null;
            String resourceId = auditLog.getResourceId() != null ? auditLog.getResourceId().toString() : null;
            Instant now = Instant.now();

            jdbcTemplate.update(sql,
                id,
                tenantId,
                userId,
                auditLog.getEmail(),
                auditLog.getAction(),
                auditLog.getStatus(),
                auditLog.getResourceType(),
                resourceId,
                auditLog.getIpAddress(),
                auditLog.getUserAgent(),
                auditLog.getFailureReason(),
                auditLog.getMetadata(),
                now
            );

            log.debug("Audit log created: {} - {} - {} (tenant: {})", auditLog.getAction(), auditLog.getStatus(), auditLog.getEmail(), tenantId);
        } catch (Exception ex) {
            log.error("Failed to save audit log: {}", ex.getMessage());
        }
    }

    /**
     * Log login success
     */
    public void logLoginSuccess(UUID userId, String email) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.loginSuccess(userId, email, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Log login failure
     */
    public void logLoginFailure(String email, String reason) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.loginFailure(email, reason, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Log logout
     */
    public void logLogout(UUID userId, String email) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.logoutSuccess(userId, email, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Log registration success
     */
    public void logRegistration(UUID userId, String email) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.registerSuccess(userId, email, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Log password reset
     */
    public void logPasswordReset(String email) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.passwordResetSuccess(email, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Log refresh token success
     */
    public void logRefreshTokenSuccess(UUID userId, String email) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.refreshTokenSuccess(userId, email, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Log refresh token failure
     */
    public void logRefreshTokenFailure(String reason) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.refreshTokenFailure(reason, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Log user deletion
     */
    public void logUserDeleted(UUID userId, String email, UUID deletedBy) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.userDeleted(userId, email, deletedBy, ip, ua));
    }

    /**
     * Log tenant creation
     */
    public void logTenantCreated(UUID tenantId, String tenantName, UUID createdBy) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.builder()
                .action("TENANT_CREATED")
                .status("SUCCESS")
                .resourceType("TENANT")
                .resourceId(tenantId)
                .tenantId(createdBy)
                .metadata("Tenant created: " + tenantName)
                .ipAddress(ip)
                .userAgent(ua)
                .build());
    }

    /**
     * Log tenant deletion
     */
    public void logTenantDeleted(UUID tenantId, String tenantName, UUID deletedBy) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.tenantDeleted(tenantId, tenantName, deletedBy, ip, ua));
    }

    /**
     * Log membership deletion
     */
    public void logMembershipDeleted(UUID membershipId, UUID deletedBy) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.membershipDeleted(membershipId, deletedBy, ip, ua));
    }

    /**
     * Log role deletion
     */
    public void logRoleDeleted(UUID roleId, String roleName, UUID deletedBy) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.roleDeleted(roleId, roleName, deletedBy, ip, ua));
    }

    /**
     * Log permission deletion
     */
    public void logPermissionDeleted(UUID permissionId, String permissionCode, UUID deletedBy) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.permissionDeleted(permissionId, permissionCode, deletedBy, ip, ua));
    }

    /**
     * Get audit logs for a user
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<AuditLog> getUserAuditLogs(UUID userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get failed login attempts for an email in the last hour
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentFailedLoginAttempts(String email) {
        Instant since = Instant.now().minus(1, ChronoUnit.HOURS);
        return auditLogRepository.findFailedLoginAttempts(email, since);
    }

    /**
     * Count failed login attempts from an IP in the last hour
     */
    @Transactional(readOnly = true)
    public long countRecentFailedLoginsByIp(String ipAddress) {
        Instant since = Instant.now().minus(1, ChronoUnit.HOURS);
        return auditLogRepository.countFailedLoginAttemptsByIp(ipAddress, since);
    }

    // ==================== Tenant-Aware Audit Methods ====================

    /**
     * Get audit logs for a tenant
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getTenantAuditLogs(UUID tenantId) {
        return auditLogRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    /**
     * Get audit logs for a tenant within a time period
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getTenantAuditLogsSince(UUID tenantId, Instant since) {
        return auditLogRepository.findByTenantIdAndCreatedAtAfterOrderByCreatedAtDesc(tenantId, since);
    }

    /**
     * Get audit logs by action type for a tenant
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getTenantAuditLogsByAction(UUID tenantId, String action) {
        return auditLogRepository.findByTenantIdAndActionOrderByCreatedAtDesc(tenantId, action);
    }

    /**
     * Get failed actions for a tenant (for security monitoring)
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getTenantFailedActions(UUID tenantId, Instant since) {
        return auditLogRepository.findFailedActionsByTenant(tenantId, since);
    }

    /**
     * Count actions by type for a tenant (for metrics)
     */
    @Transactional(readOnly = true)
    public long countTenantActionsByType(UUID tenantId, String action, Instant since) {
        return auditLogRepository.countByTenantIdAndActionAndCreatedAtAfter(tenantId, action, since);
    }

    // ==================== Entity CRUD Audit Methods ====================

    /**
     * Log entity creation
     */
    public void logEntityCreated(UUID tenantId, UUID userId, String entityType, UUID entityId, String metadata) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.entityCreated(entityType, entityId, tenantId, userId, ip, ua, metadata));
    }

    /**
     * Log entity update
     */
    public void logEntityUpdated(UUID tenantId, UUID userId, String entityType, UUID entityId, String changes) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.entityUpdated(entityType, entityId, tenantId, userId, ip, ua, changes));
    }

    /**
     * Log access granted
     */
    public void logAccessGranted(UUID tenantId, UUID userId, String resourceType, UUID resourceId) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.accessGranted(tenantId, userId, resourceType, resourceId, ip, ua));
    }

    /**
     * Log access denied
     */
    public void logAccessDenied(UUID tenantId, UUID userId, String resourceType, UUID resourceId, String reason) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.accessDenied(tenantId, userId, resourceType, resourceId, reason, ip, ua));
    }

    // ==================== RBAC Audit Methods ====================

    /**
     * Log role assignment
     */
    public void logRoleAssigned(UUID tenantId, UUID targetUserId, UUID roleId, String roleName, UUID assignedBy) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.roleAssigned(tenantId, targetUserId, roleId, roleName, assignedBy, ip, ua));
    }

    /**
     * Log role removal
     */
    public void logRoleRemoved(UUID tenantId, UUID targetUserId, UUID roleId, String roleName, UUID removedBy) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.roleRemoved(tenantId, targetUserId, roleId, roleName, removedBy, ip, ua));
    }

    /**
     * Log permission granted
     */
    public void logPermissionGranted(UUID tenantId, UUID targetId, String targetType, String permissionCode, UUID grantedBy) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.permissionGranted(tenantId, targetId, targetType, permissionCode, grantedBy, ip, ua));
    }

    /**
     * Log permission revoked
     */
    public void logPermissionRevoked(UUID tenantId, UUID targetId, String targetType, String permissionCode, UUID revokedBy) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "system";
        String ua = request != null ? getUserAgent(request) : "system";
        log(AuditLog.permissionRevoked(tenantId, targetId, targetType, permissionCode, revokedBy, ip, ua));
    }

    // ==================== Security Audit Methods ====================

    /**
     * Log suspicious activity
     */
    public void logSuspiciousActivity(UUID tenantId, UUID userId, String activityType, String details) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIP(request) : "unknown";
        String ua = request != null ? getUserAgent(request) : "unknown";
        log(AuditLog.suspiciousActivity(tenantId, userId, activityType, details, ip, ua));
    }

    /**
     * Log 2FA enabled
     */
    public void logTwoFactorEnabled(UUID tenantId, UUID userId, String email) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.twoFactorEnabled(tenantId, userId, email, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Log 2FA disabled
     */
    public void logTwoFactorDisabled(UUID tenantId, UUID userId, String email) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.twoFactorDisabled(tenantId, userId, email, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Log 2FA verification success
     */
    public void logTwoFactorVerified(UUID userId, String email) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.twoFactorVerified(userId, email, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Log 2FA verification failure
     */
    public void logTwoFactorFailed(String email, String reason) {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            log(AuditLog.twoFactorFailed(email, reason, getClientIP(request), getUserAgent(request)));
        }
    }

    /**
     * Extract client IP from request
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Extract User-Agent from request
     */
    private String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.length() > 500) {
            return userAgent.substring(0, 500);
        }
        return userAgent;
    }

    /**
     * Get current HTTP request from context
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception ex) {
            return null;
        }
    }
}
