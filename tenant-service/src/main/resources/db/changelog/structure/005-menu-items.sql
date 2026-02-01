--liquibase formatted sql

-- ============================================================================
-- TENANT SERVICE - Menu Items Table (TNT_MENU_ITEMS)
-- Migrated from catalog-service CAT_UI_MENU_ITEMS
-- ============================================================================

--changeset tenant:schema-menu-items
CREATE TABLE IF NOT EXISTS TNT_MENU_ITEMS (
    ID              CHAR(36)        NOT NULL,
    PARENT_ID       CHAR(36)        NULL,
    MENU_KEY        VARCHAR(100)    NOT NULL,
    LABEL           VARCHAR(200)    NOT NULL,
    ICON            VARCHAR(100)    NULL,
    ROUTE           VARCHAR(500)    NULL,
    URL             VARCHAR(500)    NULL,
    TARGET          VARCHAR(50)     NULL,
    CATEGORY        VARCHAR(50)     NOT NULL DEFAULT 'AUTHENTICATED',
    FEATURE_CODES   JSON            NULL,
    ROLES           JSON            NULL,
    PERMISSIONS     JSON            NULL,
    ORDER_INDEX     INT             NOT NULL DEFAULT 0,
    VISIBLE         TINYINT(1)      NOT NULL DEFAULT 1,
    BADGE           VARCHAR(100)    NULL,
    BADGE_CLASS     VARCHAR(100)    NULL,
    `SEPARATOR`     TINYINT(1)      NOT NULL DEFAULT 0,
    CREATED_AT      TIMESTAMP(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UPDATED_AT      TIMESTAMP(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (ID),
    UNIQUE KEY UK_TNT_MENU_KEY (MENU_KEY)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--changeset tenant:schema-menu-items-indexes
CREATE INDEX IDX_TNT_MENU_PARENT ON TNT_MENU_ITEMS (PARENT_ID);
CREATE INDEX IDX_TNT_MENU_CATEGORY ON TNT_MENU_ITEMS (CATEGORY);
CREATE INDEX IDX_TNT_MENU_ORDER ON TNT_MENU_ITEMS (ORDER_INDEX);
