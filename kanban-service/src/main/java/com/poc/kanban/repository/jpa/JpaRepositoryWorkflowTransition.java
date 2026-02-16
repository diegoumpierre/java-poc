package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.WorkflowTransition;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRepositoryWorkflowTransition extends CrudRepository<WorkflowTransition, UUID> {

    @Query("SELECT * FROM KANB_WORKFLOW_TRANSITIONS WHERE BOARD_TYPE_CODE = :boardTypeCode")
    List<WorkflowTransition> findByBoardTypeCode(@Param("boardTypeCode") String boardTypeCode);

    @Query("SELECT * FROM KANB_WORKFLOW_TRANSITIONS WHERE BOARD_TYPE_CODE = :boardTypeCode AND FROM_STEP_ID = :fromStepId")
    List<WorkflowTransition> findByBoardTypeCodeAndFromStepId(@Param("boardTypeCode") String boardTypeCode,
                                                              @Param("fromStepId") UUID fromStepId);

    @Query("SELECT * FROM KANB_WORKFLOW_TRANSITIONS WHERE BOARD_TYPE_CODE = :boardTypeCode AND FROM_STEP_ID = :fromStepId AND TO_STEP_ID = :toStepId")
    Optional<WorkflowTransition> findTransition(@Param("boardTypeCode") String boardTypeCode,
                                                 @Param("fromStepId") UUID fromStepId,
                                                 @Param("toStepId") UUID toStepId);
}
