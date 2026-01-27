package com.poc.auth.service;

import com.poc.auth.model.response.TenantResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface for querying tenant data.
 * Can be implemented locally or via Feign to organization-service.
 */
public interface TenantQueryService {

    List<TenantResponse> findAll();

    TenantResponse findById(UUID id);

    TenantResponse findBySlug(String slug);

    List<TenantResponse> search(String query);

    List<TenantResponse> findByParentId(UUID parentId);
}
