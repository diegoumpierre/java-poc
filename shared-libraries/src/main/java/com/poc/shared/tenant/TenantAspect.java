package com.poc.shared.tenant;

import com.poc.shared.exception.TenantRequiredException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Aspect that enforces tenant context validation for methods annotated with @TenantAware.
 * Ensures multi-tenant data isolation by requiring tenant context before executing business logic.
 */
@Aspect
@Component
@Slf4j
public class TenantAspect {

    @Around("@annotation(com.poc.shared.tenant.TenantAware) || @within(com.poc.shared.tenant.TenantAware)")
    public Object validateTenantContext(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        TenantAware tenantAware = method.getAnnotation(TenantAware.class);
        if (tenantAware == null) {
            tenantAware = joinPoint.getTarget().getClass().getAnnotation(TenantAware.class);
        }

        boolean required = tenantAware == null || tenantAware.required();

        if (required && !TenantContext.hasTenantContext()) {
            log.error("Tenant context required but not present for method: {}", method.getName());
            throw new TenantRequiredException("Tenant context is required for this operation");
        }

        log.debug("Tenant context validated for method: {}, tenant: {}",
                method.getName(), TenantContext.getCurrentTenant());

        return joinPoint.proceed();
    }
}
