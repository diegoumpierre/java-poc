package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.KanbanAttachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRepositoryKanbanAttachment extends CrudRepository<KanbanAttachment, UUID> {

    List<KanbanAttachment> findByCardId(UUID cardId);

    Optional<KanbanAttachment> findByIdAndCardId(UUID id, UUID cardId);

    void deleteByCardId(UUID cardId);
}
