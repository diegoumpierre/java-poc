package com.poc.tenant;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Base class for integration tests.
 * Uses H2 in MySQL mode with Liquibase migrations.
 *
 * <p>The test Liquibase changelog (db.changelog-test.yaml) creates all
 * structure tables in H2-compatible SQL, skipping data migrations that
 * reference USR_* tables from user-service.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    // Shared test constants
    protected static final UUID TEST_TENANT_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");
    protected static final UUID TEST_USER_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    protected UUID randomId() {
        return UUID.randomUUID();
    }
}
