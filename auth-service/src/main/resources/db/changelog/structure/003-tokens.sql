--liquibase formatted sql

-- ============================================================================
-- AUTH_VERIFICATION_CODES - Codigos de verificacao (email, reset password)
-- ============================================================================

--changeset my-platform:structure-003-create-auth-verification-codes
CREATE TABLE AUTH_VERIFICATION_CODES (
    ID CHAR(36) NOT NULL,
    EMAIL VARCHAR(255) NOT NULL,
    CODE VARCHAR(10) NOT NULL,
    TYPE VARCHAR(50) NOT NULL,
    EXPIRES_AT TIMESTAMP(6) NOT NULL,
    USED TINYINT(1) DEFAULT 0,
    CREATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX IDX_VERIFICATION_CODES_EMAIL ON AUTH_VERIFICATION_CODES (EMAIL);

-- ============================================================================
-- AUTH_REFRESH_TOKENS - Tokens de refresh (JWT)
-- ============================================================================

--changeset my-platform:structure-003-create-auth-refresh-tokens
CREATE TABLE AUTH_REFRESH_TOKENS (
    ID CHAR(36) NOT NULL,
    TOKEN VARCHAR(500) NOT NULL UNIQUE,
    USER_ID CHAR(36) NOT NULL,
    EXPIRY_DATE TIMESTAMP(6) NOT NULL,
    CREATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE AUTH_REFRESH_TOKENS ADD CONSTRAINT FK_REFRESH_TOKENS_USER FOREIGN KEY (USER_ID) REFERENCES AUTH_USERS(ID) ON DELETE CASCADE;
CREATE INDEX IDX_REFRESH_TOKENS_TOKEN ON AUTH_REFRESH_TOKENS (TOKEN);
CREATE INDEX IDX_REFRESH_TOKENS_USER_ID ON AUTH_REFRESH_TOKENS (USER_ID);
