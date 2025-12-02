package com.poc.shared.security;

import com.poc.shared.exception.PermissionDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class SecurityAspect {

    @Around("@annotation(com.poc.shared.security.RequiresPermission) || @within(com.poc.shared.security.RequiresPermission)")
    public Object validatePermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Method-level annotation takes precedence over class-level
        RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
        if (annotation == null) {
            annotation = joinPoint.getTarget().getClass().getAnnotation(RequiresPermission.class);
        }

        if (annotation == null) {
            return joinPoint.proceed();
        }

        String[] required = annotation.value();
        boolean allRequired = annotation.allRequired();

        // Skip enforcement when no permission context is available
        // (e.g., dev mode without gateway, or inter-service calls)
        if (!SecurityContext.hasContext()) {
            log.debug("No permission context available, skipping check for method: {}", method.getName());
            return joinPoint.proceed();
        }

        boolean hasAccess;
        if (allRequired) {
            hasAccess = SecurityContext.hasAllPermissions(required);
        } else {
            hasAccess = SecurityContext.hasAnyPermission(required);
        }

        if (!hasAccess) {
            String requiredStr = String.join(", ", required);
            log.warn("Permission denied for method {}: required=[{}], has={}",
                    method.getName(), requiredStr, SecurityContext.getPermissions());
            throw new PermissionDeniedException(requiredStr);
        }

        log.debug("Permission validated for method: {}, required: {}", method.getName(), Arrays.toString(required));
        return joinPoint.proceed();
    }
}
