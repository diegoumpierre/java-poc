-- =================================================================
-- Live Chat Data Migration Script
-- Migrates data from 101_helpdesk to 101_chat
--
-- Source: 101_helpdesk.HELP_CHAT_SESSIONS, 101_helpdesk.HELP_CHAT_MESSAGES
-- Target: 101_chat.CHAT_LIVECHAT_VISITOR, CHAT_LIVECHAT_SESSION, CHAT_LIVECHAT_MESSAGE
--
-- IMPORTANT: Run this script MANUALLY after verifying Phases 1-9 work.
-- This is a ONE-TIME migration, NOT part of Liquibase.
--
-- Prerequisites:
--   1. Both databases accessible from the same MySQL client
--   2. CHAT_LIVECHAT_* tables already created (Phase 1 - changeset 010)
--   3. CHAT_USER table populated (users synced via Kafka)
--   4. No active live chat sessions in helpdesk (run during maintenance)
--
-- Column mapping notes (source -> target):
--   HELP_CHAT_SESSIONS.VISITOR_USER_AGENT  -> CHAT_LIVECHAT_SESSION.USER_AGENT
--   HELP_CHAT_SESSIONS.VISITOR_PAGE_URL    -> CHAT_LIVECHAT_SESSION.PAGE_URL
--   HELP_CHAT_SESSIONS.ENDED_AT            -> CHAT_LIVECHAT_SESSION.CLOSED_AT
--   HELP_CHAT_SESSIONS.LAST_MESSAGE_AT     -> CHAT_LIVECHAT_SESSION.LAST_ACTIVITY_AT
--   HELP_CHAT_SESSIONS.ASSIGNED_AGENT_ID   -> CHAT_USER.ID (via EXTERNAL_USER_ID lookup)
--   HELP_CHAT_SESSIONS has no UPDATED_AT   -> use CREATED_AT as fallback
--
-- Usage:
--   mysql -h 127.0.0.1 -P 3307 -u 101_user -p < migrate-helpdesk-chat.sql
-- =================================================================

-- Safety settings
SET @start_time = NOW();
SELECT CONCAT('Migration started at: ', @start_time) AS info;

-- =================================================================
-- PRE-MIGRATION VALIDATION
-- =================================================================

SELECT '=== PRE-MIGRATION VALIDATION ===' AS step;

-- Check source data exists
SELECT 'Source sessions:' AS metric, COUNT(*) AS total
FROM 101_helpdesk.HELP_CHAT_SESSIONS;

SELECT 'Source messages:' AS metric, COUNT(*) AS total
FROM 101_helpdesk.HELP_CHAT_MESSAGES;

-- Check target tables are empty (safety check)
SELECT 'Target visitors (should be 0):' AS metric, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_VISITOR;

SELECT 'Target sessions (should be 0):' AS metric, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_SESSION;

SELECT 'Target messages (should be 0):' AS metric, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_MESSAGE;

-- Check CHAT_USER has synced users (needed for agent mapping)
SELECT 'Chat users available:' AS metric, COUNT(*) AS total
FROM 101_chat.CHAT_USER;

-- Show agents that will NOT be mapped (missing from CHAT_USER)
SELECT 'Unmapped agents (sessions with agents not in CHAT_USER):' AS metric, COUNT(DISTINCT s.ASSIGNED_AGENT_ID) AS total
FROM 101_helpdesk.HELP_CHAT_SESSIONS s
WHERE s.ASSIGNED_AGENT_ID IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM 101_chat.CHAT_USER cu
    WHERE cu.EXTERNAL_USER_ID = s.ASSIGNED_AGENT_ID
      AND cu.TENANT_ID = s.TENANT_ID
  );

-- =================================================================
-- STEP 1: MIGRATE VISITORS
-- Deduplicate by TENANT_ID + VISITOR_ID from HELP_CHAT_SESSIONS
-- =================================================================

SELECT '=== STEP 1: Migrating visitors ===' AS step;

INSERT INTO 101_chat.CHAT_LIVECHAT_VISITOR (
    TENANT_ID,
    VISITOR_ID,
    NAME,
    EMAIL,
    METADATA,
    CREATED_AT,
    UPDATED_AT
)
SELECT
    sub.TENANT_ID,
    sub.VISITOR_ID,
    sub.VISITOR_NAME,
    sub.VISITOR_EMAIL,
    NULL AS METADATA,
    sub.FIRST_SEEN AS CREATED_AT,
    sub.LAST_SEEN AS UPDATED_AT
FROM (
    SELECT
        s.TENANT_ID,
        s.VISITOR_ID,
        -- Pick the most recent non-null name and email per visitor
        SUBSTRING_INDEX(
            GROUP_CONCAT(s.VISITOR_NAME ORDER BY s.CREATED_AT DESC SEPARATOR '|||'),
            '|||', 1
        ) AS VISITOR_NAME,
        SUBSTRING_INDEX(
            GROUP_CONCAT(s.VISITOR_EMAIL ORDER BY s.CREATED_AT DESC SEPARATOR '|||'),
            '|||', 1
        ) AS VISITOR_EMAIL,
        MIN(s.CREATED_AT) AS FIRST_SEEN,
        MAX(s.CREATED_AT) AS LAST_SEEN
    FROM 101_helpdesk.HELP_CHAT_SESSIONS s
    WHERE s.VISITOR_ID IS NOT NULL
    GROUP BY s.TENANT_ID, s.VISITOR_ID
) sub
ON DUPLICATE KEY UPDATE
    NAME = COALESCE(VALUES(NAME), 101_chat.CHAT_LIVECHAT_VISITOR.NAME),
    EMAIL = COALESCE(VALUES(EMAIL), 101_chat.CHAT_LIVECHAT_VISITOR.EMAIL),
    UPDATED_AT = VALUES(UPDATED_AT);

SELECT 'Visitors migrated:' AS metric, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_VISITOR;

-- =================================================================
-- STEP 2: MIGRATE SESSIONS
-- Map ASSIGNED_AGENT_ID (UUID string) -> CHAT_USER.ID (BIGINT)
--   via CHAT_USER.EXTERNAL_USER_ID
-- Map VISITOR_ID (varchar) -> CHAT_LIVECHAT_VISITOR.ID (BIGINT)
--   via TENANT_ID + VISITOR_ID
-- =================================================================

SELECT '=== STEP 2: Migrating sessions ===' AS step;

INSERT INTO 101_chat.CHAT_LIVECHAT_SESSION (
    TENANT_ID,
    SESSION_TOKEN,
    VISITOR_ID,
    ASSIGNED_AGENT_ID,
    QUEUE_ID,
    SOURCE_SERVICE,
    STATUS,
    PAGE_URL,
    VISITOR_IP,
    USER_AGENT,
    MESSAGE_COUNT,
    RATING,
    FEEDBACK,
    AGENT_JOINED_AT,
    CLOSED_AT,
    LAST_ACTIVITY_AT,
    CREATED_AT,
    UPDATED_AT
)
SELECT
    s.TENANT_ID,
    s.SESSION_TOKEN,
    v.ID AS VISITOR_ID,
    cu.ID AS ASSIGNED_AGENT_ID,
    s.QUEUE_ID,
    'HELPDESK' AS SOURCE_SERVICE,
    s.STATUS,
    s.VISITOR_PAGE_URL AS PAGE_URL,
    s.VISITOR_IP,
    s.VISITOR_USER_AGENT AS USER_AGENT,
    s.MESSAGE_COUNT,
    s.RATING,
    s.FEEDBACK,
    s.AGENT_JOINED_AT,
    s.ENDED_AT AS CLOSED_AT,
    COALESCE(s.LAST_MESSAGE_AT, s.CREATED_AT) AS LAST_ACTIVITY_AT,
    s.CREATED_AT,
    s.CREATED_AT AS UPDATED_AT  -- Source has no UPDATED_AT, use CREATED_AT
FROM 101_helpdesk.HELP_CHAT_SESSIONS s
-- Join to get the visitor BIGINT ID (required, NOT NULL in target)
JOIN 101_chat.CHAT_LIVECHAT_VISITOR v
    ON v.TENANT_ID = s.TENANT_ID
    AND v.VISITOR_ID = s.VISITOR_ID
-- Left join to map agent UUID -> BIGINT (nullable, not all sessions have agents)
LEFT JOIN 101_chat.CHAT_USER cu
    ON s.ASSIGNED_AGENT_ID IS NOT NULL
    AND cu.EXTERNAL_USER_ID = s.ASSIGNED_AGENT_ID
    AND cu.TENANT_ID = s.TENANT_ID;

SELECT 'Sessions migrated:' AS metric, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_SESSION;

-- =================================================================
-- STEP 3: CREATE TEMPORARY SESSION ID MAPPING TABLE
-- Maps old UUID session IDs to new BIGINT session IDs
-- Uses SESSION_TOKEN as the stable linking key
-- =================================================================

SELECT '=== STEP 3: Building session ID mapping ===' AS step;

CREATE TEMPORARY TABLE tmp_session_id_map (
    OLD_SESSION_ID CHAR(36) NOT NULL,
    NEW_SESSION_ID BIGINT NOT NULL,
    PRIMARY KEY (OLD_SESSION_ID),
    INDEX IDX_NEW_SESSION (NEW_SESSION_ID)
) ENGINE=MEMORY;

INSERT INTO tmp_session_id_map (OLD_SESSION_ID, NEW_SESSION_ID)
SELECT
    hs.ID AS OLD_SESSION_ID,
    ns.ID AS NEW_SESSION_ID
FROM 101_helpdesk.HELP_CHAT_SESSIONS hs
JOIN 101_chat.CHAT_LIVECHAT_SESSION ns
    ON ns.SESSION_TOKEN = hs.SESSION_TOKEN;

SELECT 'Session mappings created:' AS metric, COUNT(*) AS total
FROM tmp_session_id_map;

-- =================================================================
-- STEP 4: MIGRATE MESSAGES
-- Map SESSION_ID (UUID) -> new BIGINT via session token mapping
-- Map SENDER_ID (UUID) -> CHAT_USER.ID for AGENT sender type
-- For VISITOR and SYSTEM types, SENDER_ID is set to NULL
-- =================================================================

SELECT '=== STEP 4: Migrating messages ===' AS step;

INSERT INTO 101_chat.CHAT_LIVECHAT_MESSAGE (
    SESSION_ID,
    SENDER_TYPE,
    SENDER_ID,
    CONTENT,
    MESSAGE_TYPE,
    ATTACHMENT_URL,
    ATTACHMENT_NAME,
    IS_READ,
    READ_AT,
    CREATED_AT
)
SELECT
    sim.NEW_SESSION_ID AS SESSION_ID,
    m.SENDER_TYPE,
    CASE
        WHEN m.SENDER_TYPE = 'AGENT' AND cu.ID IS NOT NULL THEN cu.ID
        ELSE NULL
    END AS SENDER_ID,
    m.CONTENT,
    COALESCE(m.MESSAGE_TYPE, 'TEXT') AS MESSAGE_TYPE,
    m.ATTACHMENT_URL,
    m.ATTACHMENT_NAME,
    COALESCE(m.IS_READ, 0) AS IS_READ,
    m.READ_AT,
    m.CREATED_AT
FROM 101_helpdesk.HELP_CHAT_MESSAGES m
-- Map old session UUID to new session BIGINT
JOIN tmp_session_id_map sim
    ON sim.OLD_SESSION_ID = m.SESSION_ID
-- Map agent UUID to CHAT_USER BIGINT (only for AGENT type)
LEFT JOIN 101_helpdesk.HELP_CHAT_SESSIONS hs
    ON hs.ID = m.SESSION_ID
LEFT JOIN 101_chat.CHAT_USER cu
    ON m.SENDER_TYPE = 'AGENT'
    AND cu.EXTERNAL_USER_ID = m.SENDER_ID
    AND cu.TENANT_ID = hs.TENANT_ID
ORDER BY m.CREATED_AT ASC;

SELECT 'Messages migrated:' AS metric, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_MESSAGE;

-- =================================================================
-- STEP 5: CLEANUP
-- =================================================================

SELECT '=== STEP 5: Cleanup ===' AS step;

DROP TEMPORARY TABLE IF EXISTS tmp_session_id_map;

-- =================================================================
-- POST-MIGRATION VERIFICATION
-- =================================================================

SELECT '=== POST-MIGRATION VERIFICATION ===' AS step;

-- Row count comparison
SELECT 'Source sessions' AS metric, COUNT(*) AS total
FROM 101_helpdesk.HELP_CHAT_SESSIONS
UNION ALL
SELECT 'Target sessions' AS metric, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_SESSION
UNION ALL
SELECT 'Source messages' AS metric, COUNT(*) AS total
FROM 101_helpdesk.HELP_CHAT_MESSAGES
UNION ALL
SELECT 'Target messages' AS metric, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_MESSAGE;

-- Unique visitors created
SELECT 'Unique visitors' AS metric, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_VISITOR;

-- Sessions with mapped agents vs total assigned sessions
SELECT 'Sessions with agent assigned (source)' AS metric,
    COUNT(*) AS total
FROM 101_helpdesk.HELP_CHAT_SESSIONS
WHERE ASSIGNED_AGENT_ID IS NOT NULL;

SELECT 'Sessions with agent mapped (target)' AS metric,
    COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_SESSION
WHERE ASSIGNED_AGENT_ID IS NOT NULL;

-- Sessions without agents (should match between source and target count difference)
SELECT 'Sessions without agent (source)' AS metric,
    COUNT(*) AS total
FROM 101_helpdesk.HELP_CHAT_SESSIONS
WHERE ASSIGNED_AGENT_ID IS NULL;

-- Check for orphaned messages (messages whose session was not migrated)
SELECT 'Orphaned messages (not migrated):' AS metric,
    COUNT(*) AS total
FROM 101_helpdesk.HELP_CHAT_MESSAGES m
WHERE NOT EXISTS (
    SELECT 1 FROM 101_chat.CHAT_LIVECHAT_SESSION ns
    JOIN 101_helpdesk.HELP_CHAT_SESSIONS hs ON hs.SESSION_TOKEN = ns.SESSION_TOKEN
    WHERE hs.ID = m.SESSION_ID
);

-- Message type distribution in target
SELECT 'Messages by sender type' AS metric, SENDER_TYPE AS detail, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_MESSAGE
GROUP BY SENDER_TYPE;

-- Session status distribution in target
SELECT 'Sessions by status' AS metric, STATUS AS detail, COUNT(*) AS total
FROM 101_chat.CHAT_LIVECHAT_SESSION
GROUP BY STATUS;

-- Timing
SET @end_time = NOW();
SELECT CONCAT('Migration completed at: ', @end_time) AS info;
SELECT CONCAT('Duration: ', TIMESTAMPDIFF(SECOND, @start_time, @end_time), ' seconds') AS info;

SELECT '=== MIGRATION COMPLETE ===' AS step;
