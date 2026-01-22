package com.poc.auth.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit log entity for tracking all security-relevant events.
 * Supports multi-tenant segregation via tenantId field.
 *
 * Action types:
 * - Authentication: LOGIN, LOGOUT, REGISTER, PASSWORD_RESET, REFRESH_TOKEN, VERIFY_2FA
 * - CRUD: CREATE, UPDATE, DELETE, ACCESS
 * - Security: PERMISSION_GRANTED, PERMISSION_REVOKED, ROLE_ASSIGNED, ROLE_REMOVED
 * - System: CONFIG_CHANGE, EXPORT_DATA, IMPORT_DATA
 *
 * Status: SUCCESS, FAILURE, DENIED
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("AUTH_AUDIT_LOG")
public class AuditLog implements Persistable<UUID> {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("USER_ID")
    private UUID userId;

    @Column("EMAIL")
    private String email;

    @Column("ACTION")
    private String action;

    @Column("STATUS")
    private String status;

    @Column("RESOURCE_TYPE")
    private String resourceType;

    @Column("RESOURCE_ID")
    private UUID resourceId;

    @Column("IP_ADDRESS")
    private String ipAddress;

    @Column("USER_AGENT")
    private String userAgent;

    @Column("FAILURE_REASON")
    private String failureReason;

    @Column("METADATA")
    private String metadata;

    @Column("CREATED_AT")
    private Instant createdAt;

    public static AuditLog loginSuccess(UUID userId, String email, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .userId(userId)
                .email(email)
                .action("LOGIN")
                .status("SUCCESS")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog loginFailure(String email, String failureReason, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .email(email)
                .action("LOGIN")
                .status("FAILURE")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .failureReason(failureReason)
                .build();
    }

    public static AuditLog logoutSuccess(UUID userId, String email, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .userId(userId)
                .email(email)
                .action("LOGOUT")
                .status("SUCCESS")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog registerSuccess(UUID userId, String email, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .userId(userId)
                .email(email)
                .action("REGISTER")
                .status("SUCCESS")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog passwordResetSuccess(String email, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .email(email)
                .action("PASSWORD_RESET")
                .status("SUCCESS")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog refreshTokenSuccess(UUID userId, String email, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .userId(userId)
                .email(email)
                .action("REFRESH_TOKEN")
                .status("SUCCESS")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog refreshTokenFailure(String failureReason, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .action("REFRESH_TOKEN")
                .status("FAILURE")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .failureReason(failureReason)
                .build();
    }

    public static AuditLog entityDeleted(String entityType, UUID entityId, String entityIdentifier,
                                          UUID deletedBy, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .userId(deletedBy)
                .email(entityIdentifier)
                .action(entityType + "_DELETE")
                .status("SUCCESS")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(String.format("{\"entityId\":\"%s\",\"deletedBy\":\"%s\"}", entityId, deletedBy))
                .build();
    }

    public static AuditLog userDeleted(UUID userId, String email, UUID deletedBy, String ipAddress, String userAgent) {
        return entityDeleted("USER", userId, email, deletedBy, ipAddress, userAgent);
    }

    public static AuditLog tenantDeleted(UUID tenantId, String tenantName, UUID deletedBy, String ipAddress, String userAgent) {
        return entityDeleted("TENANT", tenantId, tenantName, deletedBy, ipAddress, userAgent);
    }

    public static AuditLog membershipDeleted(UUID membershipId, UUID deletedBy, String ipAddress, String userAgent) {
        return entityDeleted("MEMBERSHIP", membershipId, membershipId.toString(), deletedBy, ipAddress, userAgent);
    }

    public static AuditLog roleDeleted(UUID roleId, String roleName, UUID deletedBy, String ipAddress, String userAgent) {
        return entityDeleted("ROLE", roleId, roleName, deletedBy, ipAddress, userAgent);
    }

    public static AuditLog permissionDeleted(UUID permissionId, String permissionCode, UUID deletedBy, String ipAddress, String userAgent) {
        return entityDeleted("PERMISSION", permissionId, permissionCode, deletedBy, ipAddress, userAgent);
    }

    // ==================== CREATE Actions ====================

    public static AuditLog entityCreated(String entityType, UUID entityId, UUID tenantId, UUID userId,
                                         String ipAddress, String userAgent, String metadata) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action("CREATE")
                .status("SUCCESS")
                .resourceType(entityType)
                .resourceId(entityId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(metadata)
                .build();
    }

    public static AuditLog tenantCreated(UUID tenantId, String tenantName, UUID createdBy, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(createdBy)
                .action("CREATE")
                .status("SUCCESS")
                .resourceType("TENANT")
                .resourceId(tenantId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(String.format("{\"tenantName\":\"%s\"}", tenantName))
                .build();
    }

    public static AuditLog userCreated(UUID tenantId, UUID userId, String email, UUID createdBy, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(createdBy)
                .email(email)
                .action("CREATE")
                .status("SUCCESS")
                .resourceType("USER")
                .resourceId(userId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog roleCreated(UUID tenantId, UUID roleId, String roleName, UUID createdBy, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(createdBy)
                .action("CREATE")
                .status("SUCCESS")
                .resourceType("ROLE")
                .resourceId(roleId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(String.format("{\"roleName\":\"%s\"}", roleName))
                .build();
    }

    // ==================== UPDATE Actions ====================

    public static AuditLog entityUpdated(String entityType, UUID entityId, UUID tenantId, UUID userId,
                                         String ipAddress, String userAgent, String changes) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action("UPDATE")
                .status("SUCCESS")
                .resourceType(entityType)
                .resourceId(entityId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(changes)
                .build();
    }

    public static AuditLog profileUpdated(UUID tenantId, UUID userId, String email, String ipAddress, String userAgent, String changes) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .email(email)
                .action("UPDATE")
                .status("SUCCESS")
                .resourceType("PROFILE")
                .resourceId(userId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(changes)
                .build();
    }

    public static AuditLog passwordChanged(UUID tenantId, UUID userId, String email, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .email(email)
                .action("PASSWORD_CHANGE")
                .status("SUCCESS")
                .resourceType("USER")
                .resourceId(userId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    // ==================== ACCESS Actions ====================

    public static AuditLog accessGranted(UUID tenantId, UUID userId, String resourceType, UUID resourceId,
                                         String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action("ACCESS")
                .status("SUCCESS")
                .resourceType(resourceType)
                .resourceId(resourceId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog accessDenied(UUID tenantId, UUID userId, String resourceType, UUID resourceId,
                                        String reason, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action("ACCESS")
                .status("DENIED")
                .resourceType(resourceType)
                .resourceId(resourceId)
                .failureReason(reason)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    // ==================== RBAC Actions ====================

    public static AuditLog roleAssigned(UUID tenantId, UUID targetUserId, UUID roleId, String roleName,
                                        UUID assignedBy, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(assignedBy)
                .action("ROLE_ASSIGNED")
                .status("SUCCESS")
                .resourceType("MEMBERSHIP")
                .resourceId(targetUserId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(String.format("{\"roleId\":\"%s\",\"roleName\":\"%s\",\"targetUserId\":\"%s\"}",
                        roleId, roleName, targetUserId))
                .build();
    }

    public static AuditLog roleRemoved(UUID tenantId, UUID targetUserId, UUID roleId, String roleName,
                                       UUID removedBy, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(removedBy)
                .action("ROLE_REMOVED")
                .status("SUCCESS")
                .resourceType("MEMBERSHIP")
                .resourceId(targetUserId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(String.format("{\"roleId\":\"%s\",\"roleName\":\"%s\",\"targetUserId\":\"%s\"}",
                        roleId, roleName, targetUserId))
                .build();
    }

    public static AuditLog permissionGranted(UUID tenantId, UUID targetId, String targetType, String permissionCode,
                                             UUID grantedBy, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(grantedBy)
                .action("PERMISSION_GRANTED")
                .status("SUCCESS")
                .resourceType(targetType)
                .resourceId(targetId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(String.format("{\"permissionCode\":\"%s\"}", permissionCode))
                .build();
    }

    public static AuditLog permissionRevoked(UUID tenantId, UUID targetId, String targetType, String permissionCode,
                                             UUID revokedBy, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(revokedBy)
                .action("PERMISSION_REVOKED")
                .status("SUCCESS")
                .resourceType(targetType)
                .resourceId(targetId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(String.format("{\"permissionCode\":\"%s\"}", permissionCode))
                .build();
    }

    // ==================== 2FA Actions ====================

    public static AuditLog twoFactorEnabled(UUID tenantId, UUID userId, String email, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .email(email)
                .action("2FA_ENABLED")
                .status("SUCCESS")
                .resourceType("USER")
                .resourceId(userId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog twoFactorDisabled(UUID tenantId, UUID userId, String email, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .email(email)
                .action("2FA_DISABLED")
                .status("SUCCESS")
                .resourceType("USER")
                .resourceId(userId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog twoFactorVerified(UUID userId, String email, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .userId(userId)
                .email(email)
                .action("2FA_VERIFY")
                .status("SUCCESS")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog twoFactorFailed(String email, String reason, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .email(email)
                .action("2FA_VERIFY")
                .status("FAILURE")
                .failureReason(reason)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    // ==================== Data Export/Import Actions ====================

    public static AuditLog dataExported(UUID tenantId, UUID userId, String exportType, String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action("EXPORT_DATA")
                .status("SUCCESS")
                .resourceType(exportType)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog configChanged(UUID tenantId, UUID userId, String configKey, String oldValue, String newValue,
                                         String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action("CONFIG_CHANGE")
                .status("SUCCESS")
                .resourceType("CONFIGURATION")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(String.format("{\"key\":\"%s\",\"oldValue\":\"%s\",\"newValue\":\"%s\"}",
                        configKey, oldValue, newValue))
                .build();
    }

    // ==================== Security Events ====================

    public static AuditLog suspiciousActivity(UUID tenantId, UUID userId, String activityType, String details,
                                              String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action("SUSPICIOUS_ACTIVITY")
                .status("DETECTED")
                .resourceType(activityType)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(details)
                .build();
    }

    public static AuditLog sessionInvalidated(UUID tenantId, UUID userId, String email, String reason,
                                              String ipAddress, String userAgent) {
        return AuditLog.builder()
                .tenantId(tenantId)
                .userId(userId)
                .email(email)
                .action("SESSION_INVALIDATED")
                .status("SUCCESS")
                .resourceType("SESSION")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .metadata(String.format("{\"reason\":\"%s\"}", reason))
                .build();
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
