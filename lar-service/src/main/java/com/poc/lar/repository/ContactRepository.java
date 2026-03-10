package com.poc.lar.repository;

import com.poc.lar.domain.Contact;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends CrudRepository<Contact, UUID> {

    @Query("SELECT * FROM LAR_CONTACTS WHERE TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<Contact> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_CONTACTS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<Contact> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_CONTACTS WHERE MEMBER_ID = :memberId AND TENANT_ID = :tenantId ORDER BY NAME")
    List<Contact> findByMemberId(@Param("memberId") UUID memberId, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_CONTACTS WHERE TENANT_ID = :tenantId AND TRUSTED = 1 ORDER BY NAME")
    List<Contact> findTrusted(@Param("tenantId") UUID tenantId);
}
