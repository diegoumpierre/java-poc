package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.KanbanSubTask;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaRepositoryKanbanSubTask extends CrudRepository<KanbanSubTask, Long> {

    @Query("SELECT * FROM KANB_SUBTASKS WHERE card_id = :cardId ORDER BY position ASC")
    List<KanbanSubTask> findByCardIdOrderByPositionAsc(@Param("cardId") Long cardId);

    @Query("SELECT COALESCE(MAX(position), -1) FROM KANB_SUBTASKS WHERE card_id = :cardId")
    Integer findMaxPositionByCardId(@Param("cardId") Long cardId);
}
