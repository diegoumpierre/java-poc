package com.poc.lar.repository;

import com.poc.lar.domain.Reward;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RewardRepository extends CrudRepository<Reward, UUID> {

    @Query("SELECT * FROM LAR_REWARDS WHERE TENANT_ID = :tenantId AND ACTIVE = 1 ORDER BY POINTS_COST")
    List<Reward> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_REWARDS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<Reward> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);
}
