package com.poc.chat;

import com.poc.shared.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {

    protected static final UUID TEST_TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    protected static final UUID TEST_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUpTenantContext() {
        TenantContext.setCurrentTenant(TEST_TENANT_ID);
        TenantContext.setCurrentUser(TEST_USER_ID);
    }

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }
}
