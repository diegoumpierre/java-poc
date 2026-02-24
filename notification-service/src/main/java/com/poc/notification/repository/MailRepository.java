package com.poc.notification.repository;

import com.poc.notification.domain.Mail;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MailRepository extends CrudRepository<Mail, UUID> {

    Optional<Mail> findByIdAndUserId(UUID id, UUID userId);

    // Inbox: not archived, not trash, not spam, not sent
    @Query("SELECT * FROM NOTF_MAILS WHERE USER_ID = :userId AND ARCHIVED = 0 AND TRASH = 0 AND SPAM = 0 AND SENT = 0 ORDER BY CREATED_AT DESC LIMIT :limit OFFSET :offset")
    List<Mail> findInbox(@Param("userId") UUID userId, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM NOTF_MAILS WHERE USER_ID = :userId AND ARCHIVED = 0 AND TRASH = 0 AND SPAM = 0 AND SENT = 0")
    long countInbox(@Param("userId") UUID userId);

    // Starred
    @Query("SELECT * FROM NOTF_MAILS WHERE USER_ID = :userId AND STARRED = 1 ORDER BY CREATED_AT DESC LIMIT :limit OFFSET :offset")
    List<Mail> findStarred(@Param("userId") UUID userId, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM NOTF_MAILS WHERE USER_ID = :userId AND STARRED = 1")
    long countStarred(@Param("userId") UUID userId);

    // Important
    @Query("SELECT * FROM NOTF_MAILS WHERE USER_ID = :userId AND IMPORTANT = 1 ORDER BY CREATED_AT DESC LIMIT :limit OFFSET :offset")
    List<Mail> findImportant(@Param("userId") UUID userId, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM NOTF_MAILS WHERE USER_ID = :userId AND IMPORTANT = 1")
    long countImportant(@Param("userId") UUID userId);

    // Sent
    @Query("SELECT * FROM NOTF_MAILS WHERE USER_ID = :userId AND SENT = 1 ORDER BY CREATED_AT DESC LIMIT :limit OFFSET :offset")
    List<Mail> findSent(@Param("userId") UUID userId, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM NOTF_MAILS WHERE USER_ID = :userId AND SENT = 1")
    long countSent(@Param("userId") UUID userId);

    // Trash
    @Query("SELECT * FROM NOTF_MAILS WHERE USER_ID = :userId AND TRASH = 1 ORDER BY CREATED_AT DESC LIMIT :limit OFFSET :offset")
    List<Mail> findTrash(@Param("userId") UUID userId, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM NOTF_MAILS WHERE USER_ID = :userId AND TRASH = 1")
    long countTrash(@Param("userId") UUID userId);

    // Spam
    @Query("SELECT * FROM NOTF_MAILS WHERE USER_ID = :userId AND SPAM = 1 ORDER BY CREATED_AT DESC LIMIT :limit OFFSET :offset")
    List<Mail> findSpam(@Param("userId") UUID userId, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM NOTF_MAILS WHERE USER_ID = :userId AND SPAM = 1")
    long countSpam(@Param("userId") UUID userId);

    // Archived
    @Query("SELECT * FROM NOTF_MAILS WHERE USER_ID = :userId AND ARCHIVED = 1 ORDER BY CREATED_AT DESC LIMIT :limit OFFSET :offset")
    List<Mail> findArchived(@Param("userId") UUID userId, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM NOTF_MAILS WHERE USER_ID = :userId AND ARCHIVED = 1")
    long countArchived(@Param("userId") UUID userId);

    // Search
    @Query("SELECT * FROM NOTF_MAILS WHERE USER_ID = :userId AND (SENDER_NAME LIKE :query OR TO_NAME LIKE :query OR TITLE LIKE :query OR MESSAGE LIKE :query) ORDER BY CREATED_AT DESC")
    List<Mail> search(@Param("userId") UUID userId, @Param("query") String query);

    // Get all mails for a user (for legacy compatibility)
    @Query("SELECT * FROM NOTF_MAILS WHERE USER_ID = :userId ORDER BY CREATED_AT DESC")
    List<Mail> findByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    // Update status flags
    @Modifying
    @Query("UPDATE NOTF_MAILS SET STARRED = :starred, UPDATED_AT = :now WHERE ID = :id AND USER_ID = :userId")
    void updateStarred(@Param("id") UUID id, @Param("userId") UUID userId, @Param("starred") int starred, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE NOTF_MAILS SET IMPORTANT = :important, UPDATED_AT = :now WHERE ID = :id AND USER_ID = :userId")
    void updateImportant(@Param("id") UUID id, @Param("userId") UUID userId, @Param("important") int important, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE NOTF_MAILS SET ARCHIVED = :archived, UPDATED_AT = :now WHERE ID = :id AND USER_ID = :userId")
    void updateArchived(@Param("id") UUID id, @Param("userId") UUID userId, @Param("archived") int archived, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE NOTF_MAILS SET TRASH = :trash, UPDATED_AT = :now WHERE ID = :id AND USER_ID = :userId")
    void updateTrash(@Param("id") UUID id, @Param("userId") UUID userId, @Param("trash") int trash, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE NOTF_MAILS SET SPAM = :spam, UPDATED_AT = :now WHERE ID = :id AND USER_ID = :userId")
    void updateSpam(@Param("id") UUID id, @Param("userId") UUID userId, @Param("spam") int spam, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM NOTF_MAILS WHERE ID = :id AND USER_ID = :userId")
    void deleteByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    // Multi-tenant methods
    @Query("SELECT * FROM NOTF_MAILS WHERE USER_ID = :userId AND TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<Mail> findByUserIdAndTenantIdOrderByCreatedAtDesc(@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);
}
