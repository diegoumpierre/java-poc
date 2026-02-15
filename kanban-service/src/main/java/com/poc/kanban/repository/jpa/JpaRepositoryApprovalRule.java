package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.ApprovalRule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaRepositoryApprovalRule extends CrudRepository<ApprovalRule, UUID> {

    List<ApprovalRule> findByTransitionId(UUID transitionId);
}
