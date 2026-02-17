package com.poc.kanban.integration;

import com.poc.kanban.storage.client.StorageClient;
import com.poc.shared.security.SecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@SpringBootTest(properties = {
    "spring.liquibase.enabled=true",
    "spring.liquibase.drop-first=true"
})
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @MockitoBean
    protected StorageClient storageClient;

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
}
