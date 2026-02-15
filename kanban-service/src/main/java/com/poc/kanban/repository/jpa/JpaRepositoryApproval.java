package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.Approval;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaRepositoryApproval extends CrudRepository<Approval, UUID> {

    @Query("SELECT * FROM KANB_APPROVALS WHERE TENANT_ID = :tenantId AND STATUS = :status ORDER BY REQUESTED_AT DESC")
    List<Approval> findByTenantIdAndStatus(@Param("tenantId") UUID tenantId, @Param("status") String status);

    @Query("SELECT * FROM KANB_APPROVALS WHERE CARD_ID = :cardId AND STATUS = :status")
    List<Approval> findByCardIdAndStatus(@Param("cardId") UUID cardId, @Param("status") String status);

    @Query("SELECT * FROM KANB_APPROVALS WHERE CARD_ID = :cardId ORDER BY CREATED_AT DESC")
    List<Approval> findByCardId(@Param("cardId") UUID cardId);

    @Query("SELECT * FROM KANB_APPROVALS WHERE BOARD_ID = :boardId ORDER BY CREATED_AT DESC")
    List<Approval> findByBoardId(@Param("boardId") UUID boardId);

    @Query("SELECT COUNT(*) FROM KANB_APPROVALS WHERE TENANT_ID = :tenantId AND STATUS = :status")
    long countByTenantIdAndStatus(@Param("tenantId") UUID tenantId, @Param("status") String status);

    @Query("SELECT * FROM KANB_APPROVALS WHERE REQUESTED_BY = :userId ORDER BY CREATED_AT DESC")
    List<Approval> findByRequestedBy(@Param("userId") UUID userId);
}
