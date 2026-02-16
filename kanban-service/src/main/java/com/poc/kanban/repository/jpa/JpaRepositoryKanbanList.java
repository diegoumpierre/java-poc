package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.KanbanList;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRepositoryKanbanList extends CrudRepository<KanbanList, UUID> {

    @Query("SELECT * FROM KANB_LISTS WHERE board_id = :boardId ORDER BY position ASC")
    List<KanbanList> findByBoardIdOrderByPositionAsc(@Param("boardId") UUID boardId);

    @Query("SELECT COALESCE(MAX(position), -1) FROM KANB_LISTS WHERE board_id = :boardId")
    Integer findMaxPositionByBoardId(@Param("boardId") UUID boardId);
}
