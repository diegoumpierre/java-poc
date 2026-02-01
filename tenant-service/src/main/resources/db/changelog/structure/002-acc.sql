--liquibase formatted sql

-- ============================================================================
-- TENANT SERVICE - Access Tables (TNT_ACC_*)
-- ============================================================================

-- ===========================================================================
-- TNT_ACC_INVITES
-- ===========================================================================

--changeset tenant:schema-acc-invites
CREATE TABLE IF NOT EXISTS TNT_ACC_INVITES (
    ID CHAR(36) NOT NULL,
    TENANT_ID CHAR(36) NOT NULL,
    EMAIL VARCHAR(255) NOT NULL,
    CODE VARCHAR(20) NOT NULL,
    ROLE_IDS VARCHAR(1000),
    STATUS VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    INVITED_BY CHAR(36) NOT NULL,
    EXPIRES_AT TIMESTAMP(6) NOT NULL,
    ACCEPTED_AT TIMESTAMP(6),
    ACCEPTED_BY_USER_ID CHAR(36),
    CREATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    UPDATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (ID),
    UNIQUE KEY UK_TNT_ACC_INVITES_CODE (CODE),
    INDEX IDX_TNT_ACC_INVITES_TENANT (TENANT_ID),
    INDEX IDX_TNT_ACC_INVITES_EMAIL (EMAIL),
    INDEX IDX_TNT_ACC_INVITES_STATUS (STATUS)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===========================================================================
-- TNT_ACC_ACCESS_REQUESTS
-- ===========================================================================

--changeset tenant:schema-acc-access-requests
CREATE TABLE IF NOT EXISTS TNT_ACC_ACCESS_REQUESTS (
    ID CHAR(36) NOT NULL,
    USER_ID CHAR(36) NOT NULL,
    TENANT_ID CHAR(36) NOT NULL,
    MESSAGE VARCHAR(500),
    STATUS VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    REVIEWED_BY CHAR(36),
    REVIEWED_AT TIMESTAMP(6),
    REJECTION_REASON VARCHAR(500),
    CREATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    UPDATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (ID),
    UNIQUE KEY UK_TNT_ACC_ACCESS_REQUESTS_USER_TENANT (USER_ID, TENANT_ID),
    INDEX IDX_TNT_ACC_ACCESS_REQUESTS_TENANT (TENANT_ID),
    INDEX IDX_TNT_ACC_ACCESS_REQUESTS_STATUS (STATUS)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
