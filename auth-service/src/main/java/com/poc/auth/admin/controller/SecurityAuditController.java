package com.poc.auth.admin.controller;

import com.poc.auth.domain.AuditLog;
import com.poc.auth.service.AuditService;
import com.poc.auth.admin.service.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Security Audit Controller for monitoring and reporting security events.
 *
 * Provides endpoints for:
 * - Tenant audit logs
 * - Security reports
 * - Failed action monitoring
 * - Compliance checks
 *
 * Note: These endpoints should be restricted to ADMIN or SUPER_ADMIN roles.
 * Since security is disabled (handled by API Gateway), access control
 * should be implemented at the gateway level.
 */
@RestController
@RequestMapping("/api/security-audit")
@RequiredArgsConstructor
@Slf4j
public class SecurityAuditController {

    private final AuditService auditService;
    private final SecurityAuditService securityAuditService;

    // ==================== Tenant Audit Logs ====================

    /**
     * Get audit logs for a tenant
     */
    @GetMapping("/tenant/{tenantId}/logs")
    public ResponseEntity<List<AuditLog>> getTenantAuditLogs(
            @PathVariable UUID tenantId,
            @RequestParam(defaultValue = "24") int hours) {

        Instant since = Instant.now().minus(hours, ChronoUnit.HOURS);
        List<AuditLog> logs = auditService.getTenantAuditLogsSince(tenantId, since);

        return ResponseEntity.ok(logs);
    }

    /**
     * Get audit logs by action type for a tenant
     */
    @GetMapping("/tenant/{tenantId}/logs/action/{action}")
    public ResponseEntity<List<AuditLog>> getTenantAuditLogsByAction(
            @PathVariable UUID tenantId,
            @PathVariable String action) {

        List<AuditLog> logs = auditService.getTenantAuditLogsByAction(tenantId, action);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get failed actions for a tenant (security monitoring)
     */
    @GetMapping("/tenant/{tenantId}/failed-actions")
    public ResponseEntity<List<AuditLog>> getTenantFailedActions(
            @PathVariable UUID tenantId,
            @RequestParam(defaultValue = "24") int hours) {

        Instant since = Instant.now().minus(hours, ChronoUnit.HOURS);
        List<AuditLog> logs = auditService.getTenantFailedActions(tenantId, since);

        return ResponseEntity.ok(logs);
    }

    // ==================== Security Reports ====================

    /**
     * Generate a security report for a tenant
     */
    @GetMapping("/tenant/{tenantId}/report")
    public ResponseEntity<SecurityReport> generateSecurityReport(
            @PathVariable UUID tenantId,
            @RequestParam(defaultValue = "24") int hours) {

        SecurityReport report = securityAuditService.generateSecurityReport(tenantId, hours);
        return ResponseEntity.ok(report);
    }

    /**
     * Get system-wide security metrics (admin only)
     */
    @GetMapping("/system/metrics")
    public ResponseEntity<SystemSecurityMetrics> getSystemSecurityMetrics(
            @RequestParam(defaultValue = "24") int hours) {

        SystemSecurityMetrics metrics = securityAuditService.getSystemSecurityMetrics(hours);
        return ResponseEntity.ok(metrics);
    }

    // ==================== Compliance Checks ====================

    /**
     * Check tenant isolation compliance
     */
    @GetMapping("/compliance/tenant-isolation")
    public ResponseEntity<ComplianceCheckResult> checkTenantIsolation() {
        ComplianceCheckResult result = securityAuditService.checkTenantIsolation();
        return ResponseEntity.ok(result);
    }

    /**
     * Check RBAC configuration compliance
     */
    @GetMapping("/compliance/rbac")
    public ResponseEntity<ComplianceCheckResult> checkRbacCompliance(
            @RequestParam(required = false) UUID tenantId) {

        ComplianceCheckResult result = securityAuditService.checkRbacCompliance(tenantId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get full compliance report
     */
    @GetMapping("/compliance/report")
    public ResponseEntity<ComplianceReport> getComplianceReport() {
        ComplianceReport report = securityAuditService.generateComplianceReport();
        return ResponseEntity.ok(report);
    }

    // ==================== DTOs ====================

    /**
     * Security report for a tenant
     */
    public record SecurityReport(
            UUID tenantId,
            Instant generatedAt,
            int periodHours,
            long totalActions,
            long successfulLogins,
            long failedLogins,
            long accessDeniedEvents,
            long suspiciousActivities,
            long activeUsers,
            List<AuditLog> recentFailedActions,
            List<AuditLog> recentSuspiciousActivities,
            Map<String, Long> actionCounts
    ) {}

    /**
     * System-wide security metrics
     */
    public record SystemSecurityMetrics(
            Instant generatedAt,
            int periodHours,
            long totalTenants,
            long totalActions,
            long totalFailedLogins,
            long totalAccessDenied,
            long totalSuspiciousActivities,
            List<TenantSecuritySummary> tenantSummaries,
            List<AuditLog> recentFailedLogins,
            Map<String, Long> actionDistribution
    ) {}

    /**
     * Summary of security metrics for a single tenant
     */
    public record TenantSecuritySummary(
            UUID tenantId,
            String tenantName,
            long totalActions,
            long failedLogins,
            long accessDenied,
            long activeUsers
    ) {}

    /**
     * Result of a compliance check
     */
    public record ComplianceCheckResult(
            String checkName,
            boolean passed,
            String status,
            List<String> findings,
            List<String> recommendations,
            Instant checkedAt
    ) {}

    /**
     * Full compliance report
     */
    public record ComplianceReport(
            Instant generatedAt,
            int totalChecks,
            int passedChecks,
            int failedChecks,
            double complianceScore,
            List<ComplianceCheckResult> checks,
            List<String> criticalFindings,
            List<String> recommendations
    ) {}
}
