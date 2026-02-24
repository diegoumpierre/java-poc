package com.poc.notification.service;

import com.poc.notification.dto.MailModel;
import com.poc.notification.dto.MailPageResponse;

import java.util.List;
import java.util.UUID;

public interface MailService {

    // Folder operations
    MailPageResponse getInbox(UUID userId, int page, int size);

    MailPageResponse getStarred(UUID userId, int page, int size);

    MailPageResponse getImportant(UUID userId, int page, int size);

    MailPageResponse getSent(UUID userId, int page, int size);

    MailPageResponse getTrash(UUID userId, int page, int size);

    MailPageResponse getSpam(UUID userId, int page, int size);

    MailPageResponse getArchived(UUID userId, int page, int size);

    // Single mail operations
    MailModel getById(UUID id, UUID userId);

    List<MailModel> search(UUID userId, String query);

    List<MailModel> getAllMails(UUID userId);

    // CRUD operations
    MailModel create(UUID userId, MailModel mailModel);

    MailModel update(UUID id, UUID userId, MailModel mailModel);

    void delete(UUID id, UUID userId);

    void deleteBatch(List<UUID> ids, UUID userId);

    // Status operations
    MailModel toggleStar(UUID id, UUID userId);

    MailModel toggleImportant(UUID id, UUID userId);

    MailModel toggleArchived(UUID id, UUID userId);

    MailModel moveToTrash(UUID id, UUID userId);

    MailModel moveToSpam(UUID id, UUID userId);

    void clearMailActions(UUID id, UUID userId);

    // Batch operations
    void archiveMultiple(List<UUID> ids, UUID userId);

    void spamMultiple(List<UUID> ids, UUID userId);

    void trashMultiple(List<UUID> ids, UUID userId);

    // Get counts for sidebar badges
    MailCounts getCounts(UUID userId);

    record MailCounts(
            long inbox,
            long starred,
            long important,
            long sent,
            long trash,
            long spam,
            long archived
    ) {}
}
