package com.poc.lar.repository;

import com.poc.lar.domain.ChoreLog;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChoreLogRepository extends CrudRepository<ChoreLog, UUID> {

    @Query("SELECT * FROM LAR_CHORE_LOG WHERE CHORE_ID = :choreId ORDER BY COMPLETED_AT DESC")
    List<ChoreLog> findByChoreId(@Param("choreId") UUID choreId);

    @Query("SELECT * FROM LAR_CHORE_LOG WHERE MEMBER_ID = :memberId ORDER BY COMPLETED_AT DESC")
    List<ChoreLog> findByMemberId(@Param("memberId") UUID memberId);

    @Query("SELECT COALESCE(SUM(POINTS_EARNED), 0) FROM LAR_CHORE_LOG WHERE MEMBER_ID = :memberId")
    Integer sumPointsByMemberId(@Param("memberId") UUID memberId);
}
