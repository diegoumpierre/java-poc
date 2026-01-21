--liquibase formatted sql

-- ============================================================================
-- AUTH_TWO_FACTOR_CODES - Codigos 2FA temporarios
-- ============================================================================

--changeset my-platform:structure-004-create-auth-two-factor-codes
CREATE TABLE AUTH_TWO_FACTOR_CODES (
    ID CHAR(36) NOT NULL,
    USER_ID CHAR(36) NOT NULL,
    CODE VARCHAR(10) NOT NULL,
    EXPIRES_AT TIMESTAMP(6) NOT NULL,
    USED TINYINT(1) DEFAULT 0,
    IP_ADDRESS VARCHAR(45),
    USER_AGENT VARCHAR(500),
    CREATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE AUTH_TWO_FACTOR_CODES ADD CONSTRAINT FK_TWO_FACTOR_CODES_USER FOREIGN KEY (USER_ID) REFERENCES AUTH_USERS(ID) ON DELETE CASCADE;
CREATE INDEX IDX_TWO_FACTOR_CODES_USER ON AUTH_TWO_FACTOR_CODES (USER_ID);
CREATE INDEX IDX_TWO_FACTOR_CODES_EXPIRES ON AUTH_TWO_FACTOR_CODES (EXPIRES_AT);

-- ============================================================================
-- AUTH_BACKUP_CODES - Codigos de backup para 2FA
-- ============================================================================

--changeset my-platform:structure-004-create-auth-backup-codes
CREATE TABLE AUTH_BACKUP_CODES (
    ID CHAR(36) NOT NULL,
    USER_ID CHAR(36) NOT NULL,
    CODE_HASH VARCHAR(255) NOT NULL,
    USED_AT TIMESTAMP(6),
    CREATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE AUTH_BACKUP_CODES ADD CONSTRAINT FK_BACKUP_CODES_USER FOREIGN KEY (USER_ID) REFERENCES AUTH_USERS(ID) ON DELETE CASCADE;
CREATE INDEX IDX_BACKUP_CODES_USER ON AUTH_BACKUP_CODES (USER_ID);
