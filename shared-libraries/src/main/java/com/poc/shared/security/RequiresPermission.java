package com.poc.shared.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods or classes that require specific permissions.
 *
 * <p>Usage:</p>
 * <pre>
 * &#64;RequiresPermission("KANBAN_MANAGE")                              // single
 * &#64;RequiresPermission({"FINANCE_MANAGE", "FINANCE_APPROVE"})        // OR (any)
 * &#64;RequiresPermission(value = {"A", "B"}, allRequired = true)       // AND (all)
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {

    /**
     * Permission codes required. Default logic is OR (any match).
     * Set allRequired = true for AND (all must match).
     */
    String[] value();

    /**
     * If true, ALL permissions must be present. Default is false (any one suffices).
     */
    boolean allRequired() default false;
}
