package com.poc.auth;

import com.poc.shared.security.SecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.UUID;

/**
 * Base class for integration tests with H2 in-memory database.
 * Provides database cleanup utilities.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    // Test tenant and user IDs
    protected static final UUID TEST_TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    protected static final UUID TEST_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    protected static final Set<String> TEST_PERMISSIONS = Set.of(
        "KANBAN_MANAGE", "KANBAN_VIEW", "FINANCE_MANAGE", "FINANCE_APPROVE",
        "HELPDESK_MANAGE", "HELPDESK_RESPOND", "CUSTOMER_MANAGE",
        "BPF_MANAGE", "RH_MANAGE", "PERICIA_MANAGE", "BILLING_MANAGE",
        "TENANT_MANAGE", "MENU_MANAGE", "RESELLER_MANAGE",
        "USER_MANAGE", "ROLE_MANAGE", "PLATFORM_ADMIN"
    );

    @BeforeEach
    void setUpSecurityContext() {
        SecurityContext.setPermissions(TEST_PERMISSIONS);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContext.clear();
    }

    /**
     * Clean all records from a table.
     */
    protected void cleanTable(String tableName) {
        jdbcTemplate.execute("DELETE FROM " + tableName);
    }

    /**
     * Clean all auth tables.
     */
    protected void cleanAuthTables() {
        jdbcTemplate.execute("DELETE FROM AUTH_USER_SESSIONS");
        jdbcTemplate.execute("DELETE FROM AUTH_REFRESH_TOKENS");
        jdbcTemplate.execute("DELETE FROM AUTH_ROLE_PERMISSIONS");
        jdbcTemplate.execute("DELETE FROM AUTH_PERMISSIONS");
        jdbcTemplate.execute("DELETE FROM AUTH_ROLES");
        jdbcTemplate.execute("DELETE FROM AUTH_USERS");
    }

    /**
     * Generate a random UUID for test entities.
     */
    protected UUID randomId() {
        return UUID.randomUUID();
    }
}
