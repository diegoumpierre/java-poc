package com.poc.tenant.membership.repository;

import com.poc.tenant.membership.domain.MembershipRole;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipRoleRepository extends CrudRepository<MembershipRole, UUID> {

    @Query("SELECT * FROM TNT_ACC_MEMBERSHIP_ROLES WHERE MEMBERSHIP_ID = :membershipId")
    List<MembershipRole> findByMembershipId(@Param("membershipId") UUID membershipId);

    @Modifying
    @Query("DELETE FROM TNT_ACC_MEMBERSHIP_ROLES WHERE MEMBERSHIP_ID = :membershipId")
    void deleteByMembershipId(@Param("membershipId") UUID membershipId);

    @Modifying
    @Query("INSERT INTO TNT_ACC_MEMBERSHIP_ROLES (MEMBERSHIP_ID, ROLE_ID) VALUES (:membershipId, :roleId)")
    void insertMembershipRole(@Param("membershipId") UUID membershipId, @Param("roleId") UUID roleId);
}
