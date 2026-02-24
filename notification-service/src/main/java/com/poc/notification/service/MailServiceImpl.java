package com.poc.notification.service;

import com.poc.shared.tenant.TenantContext;
import com.poc.notification.domain.Mail;
import com.poc.notification.dto.MailModel;
import com.poc.notification.dto.MailPageResponse;
import com.poc.notification.repository.MailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MailServiceImpl implements MailService {

    private final MailRepository mailRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy");

    @Override
    @Transactional(readOnly = true)
    public MailPageResponse getInbox(UUID userId, int page, int size) {
        log.info("Getting inbox for user: {}, page: {}, size: {}", userId, page, size);
        int offset = page * size;
        List<Mail> mails = mailRepository.findInbox(userId, size, offset);
        long total = mailRepository.countInbox(userId);
        return MailPageResponse.of(mails.stream().map(this::toModel).collect(Collectors.toList()), total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public MailPageResponse getStarred(UUID userId, int page, int size) {
        log.info("Getting starred for user: {}", userId);
        int offset = page * size;
        List<Mail> mails = mailRepository.findStarred(userId, size, offset);
        long total = mailRepository.countStarred(userId);
        return MailPageResponse.of(mails.stream().map(this::toModel).collect(Collectors.toList()), total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public MailPageResponse getImportant(UUID userId, int page, int size) {
        log.info("Getting important for user: {}", userId);
        int offset = page * size;
        List<Mail> mails = mailRepository.findImportant(userId, size, offset);
        long total = mailRepository.countImportant(userId);
        return MailPageResponse.of(mails.stream().map(this::toModel).collect(Collectors.toList()), total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public MailPageResponse getSent(UUID userId, int page, int size) {
        log.info("Getting sent for user: {}", userId);
        int offset = page * size;
        List<Mail> mails = mailRepository.findSent(userId, size, offset);
        long total = mailRepository.countSent(userId);
        return MailPageResponse.of(mails.stream().map(this::toModel).collect(Collectors.toList()), total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public MailPageResponse getTrash(UUID userId, int page, int size) {
        log.info("Getting trash for user: {}", userId);
        int offset = page * size;
        List<Mail> mails = mailRepository.findTrash(userId, size, offset);
        long total = mailRepository.countTrash(userId);
        return MailPageResponse.of(mails.stream().map(this::toModel).collect(Collectors.toList()), total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public MailPageResponse getSpam(UUID userId, int page, int size) {
        log.info("Getting spam for user: {}", userId);
        int offset = page * size;
        List<Mail> mails = mailRepository.findSpam(userId, size, offset);
        long total = mailRepository.countSpam(userId);
        return MailPageResponse.of(mails.stream().map(this::toModel).collect(Collectors.toList()), total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public MailPageResponse getArchived(UUID userId, int page, int size) {
        log.info("Getting archived for user: {}", userId);
        int offset = page * size;
        List<Mail> mails = mailRepository.findArchived(userId, size, offset);
        long total = mailRepository.countArchived(userId);
        return MailPageResponse.of(mails.stream().map(this::toModel).collect(Collectors.toList()), total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public MailModel getById(UUID id, UUID userId) {
        log.info("Getting mail by id: {} for user: {}", id, userId);
        Mail mail = mailRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Mail not found"));
        return toModel(mail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MailModel> search(UUID userId, String query) {
        log.info("Searching mails for user: {} with query: {}", userId, query);
        String searchQuery = "%" + query + "%";
        List<Mail> mails = mailRepository.search(userId, searchQuery);
        return mails.stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MailModel> getAllMails(UUID userId) {
        log.info("Getting all mails for user: {}", userId);
        List<Mail> mails = mailRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return mails.stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public MailModel create(UUID userId, MailModel mailModel) {
        log.info("Creating mail for user: {}", userId);

        Mail mail = Mail.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(TenantContext.getCurrentTenant())
                .senderName(mailModel.getSenderName())
                .senderEmail(mailModel.getSenderEmail())
                .senderImage(mailModel.getSenderImage())
                .toName(mailModel.getToName())
                .toEmail(mailModel.getEmail())
                .title(mailModel.getTitle())
                .message(mailModel.getMessage())
                .date(mailModel.getDate() != null ? mailModel.getDate() : LocalDate.now().format(DATE_FORMATTER))
                .important(mailModel.getImportant() != null ? mailModel.getImportant() : false)
                .starred(mailModel.getStarred() != null ? mailModel.getStarred() : false)
                .trash(false)
                .spam(false)
                .archived(false)
                .sent(mailModel.getSent() != null ? mailModel.getSent() : false)
                .readStatus(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .isNew(true)
                .build();

        Mail savedMail = mailRepository.save(mail);
        log.info("Mail created with id: {}", savedMail.getId());

        return toModel(savedMail);
    }

    @Override
    public MailModel update(UUID id, UUID userId, MailModel mailModel) {
        log.info("Updating mail: {} for user: {}", id, userId);

        Mail mail = mailRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Mail not found"));

        if (mailModel.getTitle() != null) {
            mail.setTitle(mailModel.getTitle());
        }
        if (mailModel.getMessage() != null) {
            mail.setMessage(mailModel.getMessage());
        }
        if (mailModel.getImportant() != null) {
            mail.setImportant(mailModel.getImportant());
        }
        if (mailModel.getStarred() != null) {
            mail.setStarred(mailModel.getStarred());
        }
        if (mailModel.getTrash() != null) {
            mail.setTrash(mailModel.getTrash());
        }
        if (mailModel.getSpam() != null) {
            mail.setSpam(mailModel.getSpam());
        }
        if (mailModel.getArchived() != null) {
            mail.setArchived(mailModel.getArchived());
        }

        mail.setUpdatedAt(Instant.now());
        mail.markNotNew();

        Mail savedMail = mailRepository.save(mail);
        log.info("Mail updated: {}", savedMail.getId());

        return toModel(savedMail);
    }

    @Override
    public void delete(UUID id, UUID userId) {
        log.info("Deleting mail: {} for user: {}", id, userId);
        mailRepository.deleteByIdAndUserId(id, userId);
    }

    @Override
    public void deleteBatch(List<UUID> ids, UUID userId) {
        log.info("Deleting {} mails for user: {}", ids.size(), userId);
        for (UUID id : ids) {
            mailRepository.deleteByIdAndUserId(id, userId);
        }
    }

    @Override
    public MailModel toggleStar(UUID id, UUID userId) {
        log.info("Toggling star for mail: {} user: {}", id, userId);

        Mail mail = mailRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Mail not found"));

        mail.setStarred(!mail.getStarred());
        mail.setUpdatedAt(Instant.now());
        mail.markNotNew();

        Mail savedMail = mailRepository.save(mail);
        return toModel(savedMail);
    }

    @Override
    public MailModel toggleImportant(UUID id, UUID userId) {
        log.info("Toggling important for mail: {} user: {}", id, userId);

        Mail mail = mailRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Mail not found"));

        mail.setImportant(!mail.getImportant());
        mail.setUpdatedAt(Instant.now());
        mail.markNotNew();

        Mail savedMail = mailRepository.save(mail);
        return toModel(savedMail);
    }

    @Override
    public MailModel toggleArchived(UUID id, UUID userId) {
        log.info("Toggling archived for mail: {} user: {}", id, userId);

        Mail mail = mailRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Mail not found"));

        mail.setArchived(!mail.getArchived());
        mail.setUpdatedAt(Instant.now());
        mail.markNotNew();

        Mail savedMail = mailRepository.save(mail);
        return toModel(savedMail);
    }

    @Override
    public MailModel moveToTrash(UUID id, UUID userId) {
        log.info("Moving mail to trash: {} user: {}", id, userId);

        Mail mail = mailRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Mail not found"));

        mail.setTrash(true);
        mail.setImportant(false);
        mail.setStarred(false);
        mail.setArchived(false);
        mail.setSpam(false);
        mail.setUpdatedAt(Instant.now());
        mail.markNotNew();

        Mail savedMail = mailRepository.save(mail);
        return toModel(savedMail);
    }

    @Override
    public MailModel moveToSpam(UUID id, UUID userId) {
        log.info("Moving mail to spam: {} user: {}", id, userId);

        Mail mail = mailRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Mail not found"));

        mail.setSpam(true);
        mail.setImportant(false);
        mail.setStarred(false);
        mail.setArchived(false);
        mail.setTrash(false);
        mail.setUpdatedAt(Instant.now());
        mail.markNotNew();

        Mail savedMail = mailRepository.save(mail);
        return toModel(savedMail);
    }

    @Override
    public void clearMailActions(UUID id, UUID userId) {
        log.info("Clearing mail actions for: {} user: {}", id, userId);

        Mail mail = mailRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Mail not found"));

        mail.setImportant(false);
        mail.setStarred(false);
        mail.setArchived(false);
        mail.setTrash(false);
        mail.setSpam(false);
        mail.setUpdatedAt(Instant.now());
        mail.markNotNew();

        mailRepository.save(mail);
    }

    @Override
    public void archiveMultiple(List<UUID> ids, UUID userId) {
        log.info("Archiving {} mails for user: {}", ids.size(), userId);
        for (UUID id : ids) {
            mailRepository.findByIdAndUserId(id, userId).ifPresent(mail -> {
                mail.setArchived(true);
                mail.setUpdatedAt(Instant.now());
                mail.markNotNew();
                mailRepository.save(mail);
            });
        }
    }

    @Override
    public void spamMultiple(List<UUID> ids, UUID userId) {
        log.info("Marking {} mails as spam for user: {}", ids.size(), userId);
        for (UUID id : ids) {
            mailRepository.findByIdAndUserId(id, userId).ifPresent(mail -> {
                mail.setSpam(true);
                mail.setImportant(false);
                mail.setStarred(false);
                mail.setArchived(false);
                mail.setUpdatedAt(Instant.now());
                mail.markNotNew();
                mailRepository.save(mail);
            });
        }
    }

    @Override
    public void trashMultiple(List<UUID> ids, UUID userId) {
        log.info("Moving {} mails to trash for user: {}", ids.size(), userId);
        for (UUID id : ids) {
            mailRepository.findByIdAndUserId(id, userId).ifPresent(mail -> {
                mail.setTrash(true);
                mail.setImportant(false);
                mail.setStarred(false);
                mail.setArchived(false);
                mail.setSpam(false);
                mail.setUpdatedAt(Instant.now());
                mail.markNotNew();
                mailRepository.save(mail);
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MailCounts getCounts(UUID userId) {
        log.info("Getting mail counts for user: {}", userId);
        return new MailCounts(
                mailRepository.countInbox(userId),
                mailRepository.countStarred(userId),
                mailRepository.countImportant(userId),
                mailRepository.countSent(userId),
                mailRepository.countTrash(userId),
                mailRepository.countSpam(userId),
                mailRepository.countArchived(userId)
        );
    }

    private MailModel toModel(Mail mail) {
        return MailModel.builder()
                .id(mail.getId())
                .senderName(mail.getSenderName())
                .senderEmail(mail.getSenderEmail())
                .senderImage(mail.getSenderImage())
                .to(mail.getToName())
                .toName(mail.getToName())
                .email(mail.getToEmail())
                .title(mail.getTitle())
                .message(mail.getMessage())
                .image(mail.getSenderImage())
                .date(mail.getDate())
                .important(mail.getImportant())
                .starred(mail.getStarred())
                .trash(mail.getTrash())
                .spam(mail.getSpam())
                .archived(mail.getArchived())
                .sent(mail.getSent())
                .read(mail.getReadStatus())
                .build();
    }
}
