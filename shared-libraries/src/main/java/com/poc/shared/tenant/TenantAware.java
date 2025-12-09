package com.poc.shared.tenant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods or classes that require tenant context validation.
 * When applied, the TenantAspect will verify that a valid tenant context exists
 * before allowing the method to execute.
 *
 * <p>Usage:</p>
 * <pre>
 * &#64;TenantAware
 * public class MyService {
 *     // All methods require tenant context
 * }
 *
 * &#64;TenantAware(required = false)
 * public void optionalTenantMethod() {
 *     // Tenant context is optional
 * }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TenantAware {

    /**
     * Whether tenant context is required. Default is true.
     * If true and no tenant context exists, a TenantRequiredException is thrown.
     */
    boolean required() default true;
}
