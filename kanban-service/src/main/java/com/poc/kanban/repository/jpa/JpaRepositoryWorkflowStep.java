package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.WorkflowStep;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRepositoryWorkflowStep extends CrudRepository<WorkflowStep, UUID> {

    @Query("SELECT * FROM KANB_WORKFLOW_STEPS WHERE BOARD_TYPE_CODE = :boardTypeCode")
    List<WorkflowStep> findByBoardTypeCode(@Param("boardTypeCode") String boardTypeCode);

    @Query("SELECT * FROM KANB_WORKFLOW_STEPS WHERE BOARD_TYPE_CODE = :boardTypeCode ORDER BY POSITION ASC")
    List<WorkflowStep> findByBoardTypeCodeOrderByPositionAsc(@Param("boardTypeCode") String boardTypeCode);

    @Query("SELECT * FROM KANB_WORKFLOW_STEPS WHERE BOARD_TYPE_CODE = :boardTypeCode AND IS_INITIAL = 1 LIMIT 1")
    Optional<WorkflowStep> findInitialStep(@Param("boardTypeCode") String boardTypeCode);

    @Query("SELECT * FROM KANB_WORKFLOW_STEPS WHERE BOARD_TYPE_CODE = :boardTypeCode AND STEP_CODE = :stepCode")
    Optional<WorkflowStep> findByBoardTypeCodeAndStepCode(@Param("boardTypeCode") String boardTypeCode,
                                                           @Param("stepCode") String stepCode);
}
