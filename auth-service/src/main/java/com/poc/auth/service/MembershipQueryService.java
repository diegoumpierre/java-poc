package com.poc.auth.service;

import com.poc.auth.model.response.MembershipResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface for querying membership data.
 * Can be implemented locally or via Feign to organization-service.
 */
public interface MembershipQueryService {

    List<MembershipResponse> findByUserId(UUID userId);

    List<MembershipResponse> findActiveByUserId(UUID userId);

    MembershipResponse findById(UUID id);
}
