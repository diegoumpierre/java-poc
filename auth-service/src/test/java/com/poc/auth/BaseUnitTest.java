package com.poc.auth;

import com.poc.shared.security.SecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

/**
 * Base class for unit tests with Mockito.
 * Provides common test utilities.
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {

    // Test tenant and user IDs
    protected static final UUID TEST_TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    protected static final UUID TEST_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    protected static final Set<String> TEST_PERMISSIONS = Set.of(
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
     * Generate a random UUID for test entities.
     */
    protected UUID randomId() {
        return UUID.randomUUID();
    }
}
