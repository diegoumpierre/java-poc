package com.poc.chat.service;

import com.poc.chat.domain.ChatAttachment;
import com.poc.chat.domain.ChatUser;
import com.poc.chat.dto.chat.ChatAttachmentDTO;
import com.poc.chat.repository.ChatAttachmentRepository;
import com.poc.chat.repository.ChatChannelMemberRepository;
import com.poc.chat.repository.ChatUserRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {

    private final ChatAttachmentRepository attachmentRepository;
    private final ChatChannelMemberRepository channelMemberRepository;
    private final ChatUserRepository userRepository;

    @Value("${app.attachments.storage-dir:#{systemProperties['java.io.tmpdir'] + '/chat-attachments'}}")
    private String storageDir;

    @Value("${app.attachments.base-url:}")
    private String baseUrl;

    @Value("${app.attachments.max-size-mb:50}")
    private int maxSizeMb;

    @Transactional
    public ChatAttachment saveAttachment(Long messageId, MultipartFile file) throws IOException {
        if (file.getSize() > (long) maxSizeMb * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds maximum of " + maxSizeMb + "MB");
        }

        // Generate unique storage key
        String storageKey = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // Store file locally (in production, this would be MinIO/S3)
        Path storagePath = Path.of(storageDir);
        Files.createDirectories(storagePath);
        Path filePath = storagePath.resolve(storageKey);

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        ChatAttachment attachment = ChatAttachment.builder()
                .messageId(messageId)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .storageKey(storageKey)
                .createdAt(Instant.now())
                .build();

        ChatAttachment saved = attachmentRepository.save(attachment);
        log.info("Attachment saved: id={}, fileName={}, size={}", saved.getId(), saved.getFileName(), saved.getFileSize());
        return saved;
    }

    @Transactional(readOnly = true)
    public ChatAttachmentDTO getAttachment(Long attachmentId) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        ChatAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found: " + attachmentId));

        return ChatAttachmentDTO.fromEntity(attachment, baseUrl);
    }

    @Transactional(readOnly = true)
    public Path getAttachmentFile(Long attachmentId) {
        ChatAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found: " + attachmentId));

        return Path.of(storageDir, attachment.getStorageKey());
    }

    @Transactional(readOnly = true)
    public String getAttachmentMimeType(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .map(ChatAttachment::getMimeType)
                .orElse("application/octet-stream");
    }

    @Transactional(readOnly = true)
    public List<ChatAttachmentDTO> getChannelAttachments(Long channelId, int offset, int limit) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null || !channelMemberRepository.isMember(channelId, currentUser.getId())) {
            return Collections.emptyList();
        }

        return attachmentRepository.findByChannelId(channelId, offset, limit).stream()
                .map(a -> ChatAttachmentDTO.fromEntity(a, baseUrl))
                .toList();
    }

    public Map<Long, List<ChatAttachmentDTO>> getAttachmentSummaries(List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) return Collections.emptyMap();

        List<ChatAttachment> allAttachments = attachmentRepository.findByMessageIdIn(messageIds);

        return allAttachments.stream()
                .map(a -> ChatAttachmentDTO.fromEntity(a, baseUrl))
                .collect(Collectors.groupingBy(dto -> {
                    return allAttachments.stream()
                            .filter(a -> a.getId().equals(dto.getId()))
                            .findFirst()
                            .map(ChatAttachment::getMessageId)
                            .orElse(0L);
                }));
    }

    private ChatUser getCurrentUser() {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID externalUserId = TenantContext.getCurrentUser();
        return userRepository.findByExternalUserIdAndTenantId(externalUserId, tenantId).orElse(null);
    }
}
