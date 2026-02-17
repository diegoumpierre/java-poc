package com.poc.kanban;

import com.poc.shared.security.SecurityContext;
import com.poc.shared.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

/**
 * Base class for unit tests with Mockito.
 * Provides TenantContext setup and common test utilities.
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {

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
     * Generate a random UUID for test entities.
     */
    protected UUID randomId() {
        return UUID.randomUUID();
    }
}
