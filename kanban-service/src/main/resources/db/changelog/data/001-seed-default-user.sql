--liquibase formatted sql

--changeset my-platform:data-001-insert-default-admin
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM KANB_USERS WHERE EMAIL = 'admin@example.com'
INSERT INTO KANB_USERS (ID, EMAIL, PASSWORD, FULL_NAME, NICKNAME, ROLE, EMAIL_VERIFIED, ENABLED, CREATED_AT, UPDATED_AT)
VALUES (UUID(), 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin User', 'admin', 'ADMIN', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

