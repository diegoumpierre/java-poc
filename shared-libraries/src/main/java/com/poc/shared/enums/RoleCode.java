package com.poc.shared.enums;

/**
 * System role codes - must match the seed data in user-service (001-seed-data.sql).
 * These are the 6 global roles available to all tenants.
 */
public enum RoleCode {

    SUPER_ADMIN("SUPER_ADMIN"),
    ADMIN("ADMIN"),
    MANAGER("MANAGER"),
    USER("USER"),
    VIEWER("VIEWER"),
    CUSTOMER("CUSTOMER");

    private final String code;

    RoleCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
