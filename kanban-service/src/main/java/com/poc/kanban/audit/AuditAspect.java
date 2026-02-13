package com.poc.kanban.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Aspect for audit logging of sensitive operations.
 * Logs all methods annotated with @Auditable.
 */
@Aspect
@Component
@Slf4j
public class AuditAspect {

    private static final String AUDIT_LOG_PREFIX = "AUDIT";

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();
        String clientIp = getClientIp();
        String method = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            Object result = joinPoint.proceed();

            // Log successful operation
            logAuditEvent(
                auditable.action(),
                auditable.resourceType(),
                auditable.description(),
                userId,
                clientIp,
                className + "." + method,
                "SUCCESS",
                null,
                System.currentTimeMillis() - startTime,
                extractResourceId(joinPoint)
            );

            return result;

        } catch (Exception ex) {
            // Log failed operation
            logAuditEvent(
                auditable.action(),
                auditable.resourceType(),
                auditable.description(),
                userId,
                clientIp,
                className + "." + method,
                "FAILURE",
                ex.getClass().getSimpleName() + ": " + ex.getMessage(),
                System.currentTimeMillis() - startTime,
                extractResourceId(joinPoint)
            );

            throw ex;
        }
    }

    @AfterThrowing(pointcut = "@annotation(auditable)", throwing = "ex")
    public void auditException(JoinPoint joinPoint, Auditable auditable, Exception ex) {
        // Exception already logged in @Around, but this catches any edge cases
    }

    private void logAuditEvent(String action,
                               String resourceType,
                               String description,
                               String userId,
                               String clientIp,
                               String method,
                               String status,
                               String errorMessage,
                               long durationMs,
                               String resourceId) {

        // Structured audit log format
        String logMessage = String.format(
            "%s | timestamp=%s | action=%s | resource_type=%s | resource_id=%s | user_id=%s | client_ip=%s | method=%s | status=%s | duration_ms=%d%s%s",
            AUDIT_LOG_PREFIX,
            Instant.now().toString(),
            action,
            resourceType.isEmpty() ? "N/A" : resourceType,
            resourceId != null ? resourceId : "N/A",
            userId != null ? userId : "anonymous",
            clientIp != null ? clientIp : "unknown",
            method,
            status,
            durationMs,
            description.isEmpty() ? "" : " | description=" + description,
            errorMessage != null ? " | error=" + sanitize(errorMessage) : ""
        );

        if ("SUCCESS".equals(status)) {
            log.info(logMessage);
        } else {
            log.warn(logMessage);
        }
    }

    private String getCurrentUserId() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                return request.getHeader("X-User-Id");
            }
        } catch (Exception ex) {
            // Ignore
        }
        return null;
    }

    private String getClientIp() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                    return xRealIp;
                }
                return request.getRemoteAddr();
            }
        } catch (Exception ex) {
            // Ignore
        }
        return null;
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Try to extract resource ID from method arguments
     */
    private String extractResourceId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            String paramName = parameterNames[i].toLowerCase();
            if (paramName.endsWith("id") && args[i] != null) {
                if (args[i] instanceof UUID) {
                    return args[i].toString();
                } else if (args[i] instanceof String) {
                    return (String) args[i];
                } else if (args[i] instanceof Long || args[i] instanceof Integer) {
                    return args[i].toString();
                }
            }
        }
        return null;
    }

    /**
     * Sanitize error message to prevent log injection
     */
    private String sanitize(String input) {
        if (input == null) {
            return "null";
        }
        return input.replaceAll("[\\r\\n]", " ").substring(0, Math.min(input.length(), 200));
    }
}
