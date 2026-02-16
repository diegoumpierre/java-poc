package com.poc.kanban.service;

import com.poc.kanban.model.KanbanAttachmentModel;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface KanbanAttachmentService {

    List<KanbanAttachmentModel> findByCardId(UUID cardId);

    KanbanAttachmentModel upload(UUID cardId, MultipartFile file);

    Resource download(UUID cardId, UUID attachmentId);

    void delete(UUID cardId, UUID attachmentId);
}
