package com.poc.kanban.service.impl;

import com.poc.kanban.domain.KanbanAttachment;
import com.poc.kanban.model.KanbanAttachmentModel;
import com.poc.kanban.metrics.KanbanMetrics;
import com.poc.kanban.storage.client.StorageClient;
import com.poc.kanban.storage.model.FileUploadResponse;
import com.poc.kanban.repository.jpa.JpaRepositoryKanbanAttachment;
import com.poc.kanban.service.KanbanAttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class KanbanAttachmentServiceImpl implements KanbanAttachmentService {

    private final JpaRepositoryKanbanAttachment attachmentRepository;
    private final KanbanMetrics kanbanMetrics;

    @Autowired(required = false)
    private StorageClient storageClient;

    @Value("${app.upload.dir:uploads/kanban}")
    private String uploadDir;

    @Value("${app.storage.enabled:true}")
    private boolean storageEnabled;

    @Value("${app.storage.bucket:kanban-files}")
    private String storageBucket;

    public KanbanAttachmentServiceImpl(JpaRepositoryKanbanAttachment attachmentRepository,
                                       KanbanMetrics kanbanMetrics) {
        this.attachmentRepository = attachmentRepository;
        this.kanbanMetrics = kanbanMetrics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KanbanAttachmentModel> findByCardId(UUID cardId) {
        return attachmentRepository.findByCardId(cardId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public KanbanAttachmentModel upload(UUID cardId, MultipartFile file) {
        String filePath;
        String originalFilename = file.getOriginalFilename();

        // Use storage-service if enabled, otherwise use filesystem
        if (storageEnabled && storageClient != null) {
            try {
                log.info("Uploading attachment to storage-service for card: {}", cardId);

                // Generate unique object name for storage
                String objectName = "kanban/cards/" + cardId.toString() + "/" +
                        UUID.randomUUID() + "_" + (originalFilename != null ? originalFilename : "file");

                // Upload to storage-service
                FileUploadResponse response = storageClient.upload(
                        file,
                        storageBucket,
                        objectName,
                        null,
                        "kanban"
                );

                if (!response.isSuccess()) {
                    throw new RuntimeException("Failed to upload to storage-service: " + response.getMessage());
                }

                // Store the object path (not full URL) for easier retrieval
                filePath = objectName;
                log.info("Attachment uploaded to storage-service successfully for card: {}", cardId);

            } catch (Exception e) {
                log.error("Failed to upload file to storage-service for card {}", cardId, e);
                throw new RuntimeException("Failed to upload file to storage-service", e);
            }
        } else {
            try {
                log.info("Uploading attachment to filesystem for card: {}", cardId);

                // Create upload directory if it doesn't exist
                Path uploadPath = Paths.get(uploadDir, cardId.toString());
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate unique filename
                String filename = UUID.randomUUID() + "_" + (originalFilename != null ? originalFilename : "file");
                Path filePathObj = uploadPath.resolve(filename);

                // Save file
                Files.copy(file.getInputStream(), filePathObj, StandardCopyOption.REPLACE_EXISTING);

                filePath = filePathObj.toString();
                log.info("Attachment uploaded to filesystem successfully for card: {}", cardId);

            } catch (IOException e) {
                log.error("Failed to upload file to filesystem for card {}", cardId, e);
                throw new RuntimeException("Failed to upload file", e);
            }
        }

        // Save metadata to database (ID and timestamps set by BeforeConvertCallback)
        KanbanAttachment attachment = KanbanAttachment.builder()
                .cardId(cardId)
                .fileName(originalFilename)
                .filePath(filePath)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .build();

        KanbanAttachment saved = attachmentRepository.save(attachment);
        kanbanMetrics.recordAttachmentUploaded();
        log.info("Uploaded attachment {} for card {}", saved.getId(), cardId);

        return toModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource download(UUID cardId, UUID attachmentId) {
        KanbanAttachment attachment = attachmentRepository.findByIdAndCardId(attachmentId, cardId)
                .orElseThrow(() -> new NoSuchElementException("Attachment not found"));

        String filePath = attachment.getFilePath();

        // Check if it's stored in storage-service (path doesn't start with "/" or drive letter)
        if (storageEnabled && storageClient != null && !filePath.startsWith("/") && !filePath.contains(":")) {
            try {
                log.info("Downloading attachment from storage-service for card: {}", cardId);

                // Download from storage-service using the stored object path
                byte[] fileBytes = storageClient.download(storageBucket, filePath);
                return new ByteArrayResource(fileBytes);

            } catch (Exception e) {
                log.error("Failed to download attachment from storage-service {} for card {}", attachmentId, cardId, e);
                throw new RuntimeException("Failed to download file from storage-service", e);
            }
        } else {
            try {
                log.info("Downloading attachment from filesystem for card: {}", cardId);

                Path filePathObj = Paths.get(filePath);
                Resource resource = new UrlResource(filePathObj.toUri());

                if (resource.exists() && resource.isReadable()) {
                    return resource;
                } else {
                    throw new RuntimeException("File not found or not readable");
                }
            } catch (MalformedURLException e) {
                log.error("Failed to download attachment from filesystem {} for card {}", attachmentId, cardId, e);
                throw new RuntimeException("Failed to download file", e);
            }
        }
    }

    @Override
    public void delete(UUID cardId, UUID attachmentId) {
        KanbanAttachment attachment = attachmentRepository.findByIdAndCardId(attachmentId, cardId)
                .orElseThrow(() -> new NoSuchElementException("Attachment not found"));

        String filePath = attachment.getFilePath();

        // Check if it's stored in storage-service (path doesn't start with "/" or drive letter)
        if (storageEnabled && storageClient != null && !filePath.startsWith("/") && !filePath.contains(":")) {
            try {
                log.info("Deleting attachment from storage-service for card: {}", cardId);

                // Delete from storage-service using the stored object path
                storageClient.delete(storageBucket, filePath);
                log.info("Deleted attachment from storage-service: {}", filePath);

            } catch (Exception e) {
                log.error("Failed to delete file from storage-service for attachment {}", attachmentId, e);
                throw new RuntimeException("Failed to delete file from storage-service", e);
            }
        } else {
            try {
                log.info("Deleting attachment from filesystem for card: {}", cardId);

                // Delete file from filesystem
                Path filePathObj = Paths.get(filePath);
                Files.deleteIfExists(filePathObj);
                log.info("Deleted attachment from filesystem: {}", filePath);
            } catch (IOException e) {
                log.error("Failed to delete file from filesystem for attachment {}", attachmentId, e);
                throw new RuntimeException("Failed to delete file", e);
            }
        }

        // Delete metadata from database
        attachmentRepository.delete(attachment);
        log.info("Deleted attachment metadata {} for card {}", attachmentId, cardId);
    }

    private KanbanAttachmentModel toModel(KanbanAttachment attachment) {
        return KanbanAttachmentModel.builder()
                .id(attachment.getId())
                .cardId(attachment.getCardId())
                .fileName(attachment.getFileName())
                .fileSize(attachment.getFileSize())
                .contentType(attachment.getContentType())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
