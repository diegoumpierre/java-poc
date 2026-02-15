package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.KanbanComment;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaRepositoryKanbanComment extends CrudRepository<KanbanComment, Long> {

    @Query("SELECT * FROM KANB_COMMENTS WHERE card_id = :cardId ORDER BY created_at ASC")
    List<KanbanComment> findByCardIdOrderByCreatedAtAsc(@Param("cardId") Long cardId);
}
