--liquibase formatted sql

-- ============================================================================
-- Remove AUTH_USERS ghost table and broken FK constraints
-- AUTH_USERS has zero Java code references. User registration is handled
-- by user-service (USR_USERS) via Feign. The 4 FK constraints pointing to
-- AUTH_USERS(ID) are broken because new users are never inserted there.
-- USER_ID columns remain as CHAR(36) referential without constraint.
-- ============================================================================

--changeset system:007-1
--comment: Remove broken FK constraints pointing to AUTH_USERS ghost table
ALTER TABLE AUTH_USER_SESSIONS DROP FOREIGN KEY FK_USER_SESSIONS_USER;
ALTER TABLE AUTH_REFRESH_TOKENS DROP FOREIGN KEY FK_REFRESH_TOKENS_USER;
ALTER TABLE AUTH_TWO_FACTOR_CODES DROP FOREIGN KEY FK_TWO_FACTOR_CODES_USER;
ALTER TABLE AUTH_BACKUP_CODES DROP FOREIGN KEY FK_BACKUP_CODES_USER;

--changeset system:007-2
--comment: Drop AUTH_USERS ghost table (zero Java code references, users managed by user-service)
DROP TABLE IF EXISTS AUTH_USERS;
