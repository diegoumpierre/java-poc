--liquibase formatted sql

-- ============================================================================
-- AUTH_USERS seed - DEPRECATED
-- Table dropped by 007-remove-auth-users.sql
-- Users are managed by user-service (USR_USERS)
-- Platform admin seed exists in user-service seed data
-- ============================================================================

--changeset my-platform:data-001-seed-platform-admin
--comment: [DEPRECATED] AUTH_USERS dropped - users managed by user-service
--validCheckSum: ANY
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'AUTH_USERS'
INSERT IGNORE INTO AUTH_USERS (ID, EMAIL, PASSWORD, FULL_NAME, NICKNAME, EMAIL_VERIFIED, ENABLED, CREATED_AT, UPDATED_AT)
VALUES ('11111111-1111-1111-1111-111111111111', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Platform Administrator', 'platform-admin', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
