package com.poc.lar.repository;

import com.poc.lar.domain.ChecklistItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChecklistItemRepository extends CrudRepository<ChecklistItem, UUID> {

    @Query("SELECT * FROM LAR_CHECKLIST_ITEMS WHERE TEMPLATE_ID = :templateId ORDER BY ORDER_INDEX")
    List<ChecklistItem> findByTemplateId(@Param("templateId") UUID templateId);
}
