package com.poc.auth.service.impl;

import com.poc.auth.BaseUnitTest;
import com.poc.auth.client.TenantClient;
import com.poc.auth.client.dto.TenantDto;
import com.poc.auth.model.response.TenantResponse;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("FeignTenantQueryService - Resilience")
class FeignTenantQueryServiceTest extends BaseUnitTest {

    @Mock
    private TenantClient tenantClient;

    @InjectMocks
    private FeignTenantQueryService service;

    @Test
    @DisplayName("findById returns null when tenant-service is unavailable")
    void findById_WhenTenantServiceDown_ReturnsNull() {
        when(tenantClient.getTenantById(any(UUID.class), anyString()))
                .thenThrow(mock(FeignException.ServiceUnavailable.class));

        TenantResponse result = service.findById(TEST_TENANT_ID);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("findById returns null on connection timeout")
    void findById_WhenConnectionTimeout_ReturnsNull() {
        when(tenantClient.getTenantById(any(UUID.class), anyString()))
                .thenThrow(new RuntimeException("Read timed out"));

        TenantResponse result = service.findById(TEST_TENANT_ID);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("findById returns tenant when service is available")
    void findById_WhenServiceAvailable_ReturnsTenant() {
        TenantDto dto = new TenantDto();
        dto.setId(TEST_TENANT_ID);
        dto.setName("Test Tenant");
        dto.setSlug("test-tenant");
        dto.setTenantType("CLIENT");
        dto.setStatus("ACTIVE");

        when(tenantClient.getTenantById(any(UUID.class), anyString()))
                .thenReturn(dto);

        TenantResponse result = service.findById(TEST_TENANT_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_TENANT_ID);
        assertThat(result.getName()).isEqualTo("Test Tenant");
    }

    @Test
    @DisplayName("findBySlug returns null when tenant-service is unavailable")
    void findBySlug_WhenTenantServiceDown_ReturnsNull() {
        when(tenantClient.getTenantBySlug(anyString(), anyString()))
                .thenThrow(mock(FeignException.ServiceUnavailable.class));

        TenantResponse result = service.findBySlug("test-slug");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("findBySlug returns tenant when service is available")
    void findBySlug_WhenServiceAvailable_ReturnsTenant() {
        TenantDto dto = new TenantDto();
        dto.setId(TEST_TENANT_ID);
        dto.setName("Test Tenant");
        dto.setSlug("test-slug");
        dto.setTenantType("CLIENT");
        dto.setStatus("ACTIVE");

        when(tenantClient.getTenantBySlug(anyString(), anyString()))
                .thenReturn(dto);

        TenantResponse result = service.findBySlug("test-slug");

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("test-slug");
    }
}
