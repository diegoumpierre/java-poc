package com.poc.lar.repository;

import com.poc.lar.domain.ChecklistResponse;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChecklistResponseRepository extends CrudRepository<ChecklistResponse, UUID> {

    @Query("SELECT * FROM LAR_CHECKLIST_RESPONSES WHERE OUTING_ID = :outingId AND TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<ChecklistResponse> findByOutingId(@Param("outingId") UUID outingId, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_CHECKLIST_RESPONSES WHERE MEMBER_ID = :memberId AND TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<ChecklistResponse> findByMemberId(@Param("memberId") UUID memberId, @Param("tenantId") UUID tenantId);
}
