package com.poc.kanban.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for audit logging.
 * When applied to a method, the AuditAspect will log:
 * - User ID (from X-User-Id header)
 * - Action performed
 * - Resource type and ID (if applicable)
 * - Timestamp
 * - Success/failure status
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * The action being performed (e.g., "CREATE", "UPDATE", "DELETE", "VIEW")
     */
    String action();

    /**
     * The resource type (e.g., "BOARD", "LIST", "CARD")
     */
    String resourceType() default "";

    /**
     * Description of the operation
     */
    String description() default "";
}
