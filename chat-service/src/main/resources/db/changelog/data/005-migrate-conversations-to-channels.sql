--liquibase formatted sql

--changeset chat:data-005-1-migrate-conversations-to-dm-channels preconditions-sql-check
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM CHAT_CONVERSATION

-- Create DM channels from existing conversations
INSERT INTO CHAT_CHANNEL (TENANT_ID, TYPE, NAME, CREATOR_ID, LAST_MESSAGE_AT, CREATED_AT, UPDATED_AT)
SELECT c.TENANT_ID, 'DM', NULL, c.PARTICIPANT_ONE_ID, c.LAST_MESSAGE_AT, c.CREATED_AT, c.UPDATED_AT
FROM CHAT_CONVERSATION c;

-- Create channel members for participant one
INSERT INTO CHAT_CHANNEL_MEMBER (CHANNEL_ID, USER_ID, ROLE, JOINED_AT)
SELECT ch.ID, c.PARTICIPANT_ONE_ID, 'MEMBER', c.CREATED_AT
FROM CHAT_CONVERSATION c
JOIN CHAT_CHANNEL ch ON ch.CREATOR_ID = c.PARTICIPANT_ONE_ID
    AND ch.TENANT_ID = c.TENANT_ID
    AND ch.TYPE = 'DM'
    AND ch.CREATED_AT = c.CREATED_AT;

-- Create channel members for participant two
INSERT INTO CHAT_CHANNEL_MEMBER (CHANNEL_ID, USER_ID, ROLE, JOINED_AT)
SELECT ch.ID, c.PARTICIPANT_TWO_ID, 'MEMBER', c.CREATED_AT
FROM CHAT_CONVERSATION c
JOIN CHAT_CHANNEL ch ON ch.CREATOR_ID = c.PARTICIPANT_ONE_ID
    AND ch.TENANT_ID = c.TENANT_ID
    AND ch.TYPE = 'DM'
    AND ch.CREATED_AT = c.CREATED_AT;

--changeset chat:data-005-2-update-messages-with-channel-id preconditions-sql-check
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM CHAT_MESSAGE WHERE CHANNEL_ID IS NULL

-- Link messages to their new channels
UPDATE CHAT_MESSAGE m
JOIN CHAT_CONVERSATION c ON m.CONVERSATION_ID = c.ID
JOIN CHAT_CHANNEL ch ON ch.CREATOR_ID = c.PARTICIPANT_ONE_ID
    AND ch.TENANT_ID = c.TENANT_ID
    AND ch.TYPE = 'DM'
    AND ch.CREATED_AT = c.CREATED_AT
SET m.CHANNEL_ID = ch.ID;
