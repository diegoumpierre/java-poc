package com.poc.lar.repository;

import com.poc.lar.domain.FamilyMember;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends CrudRepository<FamilyMember, UUID> {

    @Query("SELECT * FROM LAR_FAMILY_MEMBERS WHERE TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<FamilyMember> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_FAMILY_MEMBERS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<FamilyMember> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_FAMILY_MEMBERS WHERE USER_ID = :userId AND TENANT_ID = :tenantId")
    Optional<FamilyMember> findByUserIdAndTenantId(@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_FAMILY_MEMBERS WHERE TENANT_ID = :tenantId AND ROLE_TYPE = :roleType ORDER BY CREATED_AT DESC")
    List<FamilyMember> findByRoleType(@Param("tenantId") UUID tenantId, @Param("roleType") String roleType);
}
