package com.poc.chat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected static final UUID TEST_TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    protected static final UUID TEST_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUpTenantContext() {
        com.poc.shared.tenant.TenantContext.setCurrentTenant(TEST_TENANT_ID);
        com.poc.shared.tenant.TenantContext.setCurrentUser(TEST_USER_ID);
    }

    @AfterEach
    void clearTenantContext() {
        com.poc.shared.tenant.TenantContext.clear();
    }

    protected void cleanTable(String tableName) {
        jdbcTemplate.execute("DELETE FROM " + tableName);
    }

    protected void cleanLiveChatTables() {
        jdbcTemplate.execute("DELETE FROM CHAT_LIVECHAT_MESSAGE");
        jdbcTemplate.execute("DELETE FROM CHAT_LIVECHAT_SESSION");
        jdbcTemplate.execute("DELETE FROM CHAT_LIVECHAT_VISITOR");
        jdbcTemplate.execute("DELETE FROM CHAT_LIVECHAT_WIDGET_CONFIG");
    }
}
