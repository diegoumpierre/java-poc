--liquibase formatted sql

-- ============================================================================
-- TENANT SERVICE - Menu Tables (TNT_MENU_*)
-- ============================================================================

-- ===========================================================================
-- TNT_MENU_CONFIG
-- ===========================================================================

--changeset tenant:schema-menu-config
CREATE TABLE IF NOT EXISTS TNT_MENU_CONFIG (
    ID CHAR(36) NOT NULL,
    TENANT_ID CHAR(36) NOT NULL,
    MENU_ID VARCHAR(100) NOT NULL,
    ENABLED TINYINT(1) NOT NULL DEFAULT 1,
    CREATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    UPDATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    CREATED_BY CHAR(36),
    PRIMARY KEY (ID),
    UNIQUE KEY UK_TNT_MENU_CONFIG (TENANT_ID, MENU_ID),
    INDEX IDX_TNT_MENU_CONFIG_TENANT (TENANT_ID),
    INDEX IDX_TNT_MENU_CONFIG_MENU (MENU_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
