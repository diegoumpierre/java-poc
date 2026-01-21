package com.poc.auth.security;

import com.poc.auth.BaseUnitTest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Tests for UserIdHeaderFilter.
 *
 * Validates that X-User-Id and X-Tenant-Id headers are injected
 * from JWT-authenticated CustomUserDetails.
 */
class UserIdHeaderFilterTest extends BaseUnitTest {

    @InjectMocks
    private UserIdHeaderFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    void shouldInjectBothUserIdAndTenantIdHeaders() throws Exception {
        // Given - authenticated user with tenant context
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID tenantId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        CustomUserDetails userDetails = new CustomUserDetails(userId, "test@test.com", "hash", "Test", true);
        userDetails.setTenantContext(tenantId, randomId());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            // When
            filter.doFilterInternal(request, response, filterChain);

            // Then - verify filter chain is called with wrapped request
            ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
            verify(filterChain).doFilter(requestCaptor.capture(), org.mockito.ArgumentMatchers.eq(response));

            HttpServletRequest wrappedRequest = requestCaptor.getValue();
            assertThat(wrappedRequest.getHeader("X-User-Id")).isEqualTo(userId.toString());
            assertThat(wrappedRequest.getHeader("X-Tenant-Id")).isEqualTo(tenantId.toString());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void shouldInjectOnlyUserIdWhenNoTenant() throws Exception {
        // Given - authenticated user WITHOUT tenant context
        UUID userId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        CustomUserDetails userDetails = new CustomUserDetails(userId, "test@test.com", "hash", "Test", true);
        // No setTenantContext() call - tenantId remains null

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            // When
            filter.doFilterInternal(request, response, filterChain);

            // Then
            ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
            verify(filterChain).doFilter(requestCaptor.capture(), org.mockito.ArgumentMatchers.eq(response));

            HttpServletRequest wrappedRequest = requestCaptor.getValue();
            assertThat(wrappedRequest.getHeader("X-User-Id")).isEqualTo(userId.toString());
            assertThat(wrappedRequest.getHeader("X-Tenant-Id")).isNull();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void shouldPassThroughWhenNotAuthenticated() throws Exception {
        // Given - no authentication
        SecurityContextHolder.clearContext();

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then - original request passed through (not wrapped)
        verify(filterChain).doFilter(request, response);
    }
}
