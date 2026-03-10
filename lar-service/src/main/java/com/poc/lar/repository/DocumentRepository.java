package com.poc.lar.repository;

import com.poc.lar.domain.Document;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends CrudRepository<Document, UUID> {

    @Query("SELECT * FROM LAR_DOCUMENTS WHERE TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<Document> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_DOCUMENTS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<Document> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_DOCUMENTS WHERE TENANT_ID = :tenantId AND MEMBER_ID = :memberId ORDER BY CREATED_AT DESC")
    List<Document> findByMemberId(@Param("tenantId") UUID tenantId, @Param("memberId") UUID memberId);

    @Query("SELECT * FROM LAR_DOCUMENTS WHERE TENANT_ID = :tenantId AND EXPIRY_DATE IS NOT NULL AND EXPIRY_DATE <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) ORDER BY EXPIRY_DATE")
    List<Document> findExpiring(@Param("tenantId") UUID tenantId);
}
