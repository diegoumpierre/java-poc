--liquibase formatted sql

-- ============================================================================
-- TEST ONLY - Membership Tables (DDL only, no data migration from USR_*)
-- ============================================================================

--changeset tenant:test-schema-acc-memberships
CREATE TABLE IF NOT EXISTS TNT_ACC_MEMBERSHIPS (
    ID CHAR(36) NOT NULL,
    USER_ID CHAR(36) NOT NULL,
    TENANT_ID CHAR(36) NOT NULL,
    STATUS VARCHAR(255) DEFAULT 'ACTIVE',
    IS_OWNER TINYINT(1) DEFAULT 0,
    DELETED_AT TIMESTAMP(6),
    DELETED_BY CHAR(36),
    CREATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    UPDATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (ID),
    UNIQUE KEY IDX_TNT_ACC_MEMBERSHIPS_USER_TENANT (USER_ID, TENANT_ID)
);

--changeset tenant:test-schema-acc-membership-roles
CREATE TABLE IF NOT EXISTS TNT_ACC_MEMBERSHIP_ROLES (
    MEMBERSHIP_ID CHAR(36) NOT NULL,
    ROLE_ID CHAR(36) NOT NULL,
    PRIMARY KEY (MEMBERSHIP_ID, ROLE_ID),
    CONSTRAINT FK_TNT_ACC_MR_MEMBERSHIP FOREIGN KEY (MEMBERSHIP_ID)
        REFERENCES TNT_ACC_MEMBERSHIPS(ID) ON DELETE CASCADE
);
