package com.poc.lar.repository;

import com.poc.lar.domain.RewardRedemption;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardRedemptionRepository extends CrudRepository<RewardRedemption, UUID> {

    @Query("SELECT * FROM LAR_REWARD_REDEMPTIONS WHERE MEMBER_ID = :memberId ORDER BY CREATED_AT DESC")
    List<RewardRedemption> findByMemberId(@Param("memberId") UUID memberId);

    @Query("SELECT r.* FROM LAR_REWARD_REDEMPTIONS r JOIN LAR_REWARDS w ON r.REWARD_ID = w.ID WHERE w.TENANT_ID = :tenantId AND r.STATUS = 'PENDING' ORDER BY r.CREATED_AT DESC")
    List<RewardRedemption> findPending(@Param("tenantId") UUID tenantId);

    @Query("SELECT COALESCE(SUM(POINTS_SPENT), 0) FROM LAR_REWARD_REDEMPTIONS WHERE MEMBER_ID = :memberId AND STATUS IN ('APPROVED', 'REDEEMED')")
    Integer sumPointsSpentByMemberId(@Param("memberId") UUID memberId);
}
