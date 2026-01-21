--liquibase formatted sql

-- ============================================================================
-- PORTAL DEMO DATA - DEPRECATED
-- AUTH_USERS table dropped by 007-remove-auth-users.sql
-- Users are managed by user-service (USR_USERS)
-- Demo user seeds (Pedro, Maria) exist in user-service seed data
-- ============================================================================

--changeset auth:data-002-seed-pedro
--comment: [DEPRECATED] AUTH_USERS dropped - users managed by user-service
--validCheckSum: ANY
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'AUTH_USERS'
INSERT IGNORE INTO AUTH_USERS (ID, EMAIL, PASSWORD, FULL_NAME, NICKNAME, EMAIL_VERIFIED, ENABLED, TWO_FACTOR_ENABLED, TWO_FACTOR_METHOD)
VALUES ('11111111-1111-1111-1111-111111111112', 'pedro@lojadopedro.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Pedro Santos', 'Pedro', 1, 1, 0, 'EMAIL');

--changeset auth:data-002-seed-maria
--comment: [DEPRECATED] AUTH_USERS dropped - users managed by user-service
--validCheckSum: ANY
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'AUTH_USERS'
INSERT IGNORE INTO AUTH_USERS (ID, EMAIL, PASSWORD, FULL_NAME, NICKNAME, EMAIL_VERIFIED, ENABLED, TWO_FACTOR_ENABLED, TWO_FACTOR_METHOD)
VALUES ('11111111-1111-1111-1111-111111111113', 'maria@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Maria Silva', 'Maria', 1, 1, 0, 'EMAIL');
