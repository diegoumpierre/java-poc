package com.poc.lar.repository;

import com.poc.lar.domain.ChecklistResponseItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChecklistResponseItemRepository extends CrudRepository<ChecklistResponseItem, UUID> {

    @Query("SELECT * FROM LAR_CHECKLIST_RESPONSE_ITEMS WHERE RESPONSE_ID = :responseId ORDER BY CREATED_AT")
    List<ChecklistResponseItem> findByResponseId(@Param("responseId") UUID responseId);
}
