package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.BoardTypeFeature;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaRepositoryBoardTypeFeature extends CrudRepository<BoardTypeFeature, UUID> {

    @Query("SELECT * FROM KANB_BOARD_TYPE_FEATURES WHERE BOARD_TYPE_CODE = :boardTypeCode")
    List<BoardTypeFeature> findByBoardTypeCode(@Param("boardTypeCode") String boardTypeCode);
}
