package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.KanbanCardHistory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRepositoryCardHistory extends CrudRepository<KanbanCardHistory, UUID> {

    @Query("SELECT * FROM KANB_CARD_HISTORY WHERE CARD_ID = :cardId ORDER BY CREATED_AT DESC")
    List<KanbanCardHistory> findByCardIdOrderByCreatedAtDesc(@Param("cardId") UUID cardId);

    @Query("SELECT * FROM KANB_CARD_HISTORY WHERE BOARD_ID = :boardId ORDER BY CREATED_AT DESC")
    List<KanbanCardHistory> findByBoardIdOrderByCreatedAtDesc(@Param("boardId") UUID boardId);

    @Query("SELECT * FROM KANB_CARD_HISTORY WHERE CARD_ID = :cardId ORDER BY CREATED_AT DESC LIMIT 1")
    Optional<KanbanCardHistory> findLastByCardId(@Param("cardId") UUID cardId);

    @Query("SELECT COUNT(*) FROM KANB_CARD_HISTORY WHERE CARD_ID = :cardId")
    long countByCardId(@Param("cardId") UUID cardId);

    @Query("SELECT * FROM KANB_CARD_HISTORY ORDER BY CREATED_AT DESC LIMIT :limit")
    List<KanbanCardHistory> findRecentHistory(@Param("limit") int limit);

    @Query("DELETE FROM KANB_CARD_HISTORY WHERE CARD_ID = :cardId")
    void deleteByCardId(@Param("cardId") UUID cardId);
}
