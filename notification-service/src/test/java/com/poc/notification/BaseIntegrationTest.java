package com.poc.notification;

import com.poc.shared.security.SecurityContext;
import com.poc.shared.tenant.TenantContext;
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
 * Provides TenantContext setup, database cleanup utilities.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    // Tenant e User fixos para testes
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
    void setUpTenantContext() {
        TenantContext.setCurrentTenant(TEST_TENANT_ID);
        TenantContext.setCurrentUser(TEST_USER_ID);
        SecurityContext.setPermissions(TEST_PERMISSIONS);
    }

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
        SecurityContext.clear();
    }

    /**
     * Clean all records from a table.
     */
    protected void cleanTable(String tableName) {
        jdbcTemplate.execute("DELETE FROM " + tableName);
    }

    /**
     * Clean all notification tables.
     */
    protected void cleanNotificationTables() {
        jdbcTemplate.execute("DELETE FROM NOTF_EMAIL_HISTORY");
        jdbcTemplate.execute("DELETE FROM NOTF_NOTIFICATIONS");
    }

    /**
     * Generate a random UUID for test entities.
     */
    protected UUID randomId() {
        return UUID.randomUUID();
    }
}
