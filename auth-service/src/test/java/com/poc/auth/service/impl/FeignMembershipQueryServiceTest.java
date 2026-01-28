package com.poc.auth.service.impl;

import com.poc.auth.BaseUnitTest;
import com.poc.auth.client.TenantClient;
import com.poc.auth.client.dto.MembershipDto;
import com.poc.auth.model.response.MembershipResponse;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("FeignMembershipQueryService - Resilience")
class FeignMembershipQueryServiceTest extends BaseUnitTest {

    @Mock
    private TenantClient tenantClient;

    @InjectMocks
    private FeignMembershipQueryService service;

    @Test
    @DisplayName("findByUserId returns empty list when tenant-service is unavailable")
    void findByUserId_WhenTenantServiceDown_ReturnsEmptyList() {
        when(tenantClient.getMembershipsByUserId(any(UUID.class), anyString()))
                .thenThrow(mock(FeignException.ServiceUnavailable.class));

        List<MembershipResponse> result = service.findByUserId(TEST_USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUserId returns empty list on connection timeout")
    void findByUserId_WhenConnectionTimeout_ReturnsEmptyList() {
        when(tenantClient.getMembershipsByUserId(any(UUID.class), anyString()))
                .thenThrow(new RuntimeException("Connection timed out"));

        List<MembershipResponse> result = service.findByUserId(TEST_USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUserId returns memberships when tenant-service is available")
    void findByUserId_WhenServiceAvailable_ReturnsMemberships() {
        MembershipDto dto = new MembershipDto();
        dto.setId(randomId());
        dto.setUserId(TEST_USER_ID);
        dto.setTenantId(TEST_TENANT_ID);
        dto.setStatus("ACTIVE");

        when(tenantClient.getMembershipsByUserId(any(UUID.class), anyString()))
                .thenReturn(List.of(dto));

        List<MembershipResponse> result = service.findByUserId(TEST_USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(TEST_USER_ID);
    }

    @Test
    @DisplayName("findActiveByUserId returns empty list when tenant-service is unavailable")
    void findActiveByUserId_WhenTenantServiceDown_ReturnsEmptyList() {
        when(tenantClient.getMembershipsByUserId(any(UUID.class), anyString()))
                .thenThrow(mock(FeignException.ServiceUnavailable.class));

        List<MembershipResponse> result = service.findActiveByUserId(TEST_USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findActiveByUserId filters only active memberships")
    void findActiveByUserId_WhenServiceAvailable_FiltersActive() {
        MembershipDto active = new MembershipDto();
        active.setId(randomId());
        active.setUserId(TEST_USER_ID);
        active.setTenantId(TEST_TENANT_ID);
        active.setStatus("ACTIVE");

        MembershipDto inactive = new MembershipDto();
        inactive.setId(randomId());
        inactive.setUserId(TEST_USER_ID);
        inactive.setTenantId(randomId());
        inactive.setStatus("SUSPENDED");

        when(tenantClient.getMembershipsByUserId(any(UUID.class), anyString()))
                .thenReturn(List.of(active, inactive));

        List<MembershipResponse> result = service.findActiveByUserId(TEST_USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTenantId()).isEqualTo(TEST_TENANT_ID);
    }

    @Test
    @DisplayName("findById returns null when tenant-service is unavailable")
    void findById_WhenTenantServiceDown_ReturnsNull() {
        UUID membershipId = randomId();
        when(tenantClient.getMembershipById(any(UUID.class), anyString()))
                .thenThrow(mock(FeignException.ServiceUnavailable.class));

        MembershipResponse result = service.findById(membershipId);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("findById returns membership when tenant-service is available")
    void findById_WhenServiceAvailable_ReturnsMembership() {
        UUID membershipId = randomId();
        MembershipDto dto = new MembershipDto();
        dto.setId(membershipId);
        dto.setUserId(TEST_USER_ID);
        dto.setTenantId(TEST_TENANT_ID);
        dto.setStatus("ACTIVE");

        when(tenantClient.getMembershipById(any(UUID.class), anyString()))
                .thenReturn(dto);

        MembershipResponse result = service.findById(membershipId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(membershipId);
    }
}
