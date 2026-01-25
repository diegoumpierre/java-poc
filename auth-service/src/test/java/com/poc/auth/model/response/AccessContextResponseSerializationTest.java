package com.poc.auth.model.response;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for AccessContextResponse JSON serialization.
 *
 * Validates that:
 * - Boolean flags serialize with "is" prefix (isAdmin, not admin)
 * - Entitlements serialize as string array
 * - Full round-trip works correctly
 */
class AccessContextResponseSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldSerializeBooleanFlagsWithIsPrefix() throws Exception {
        // Given
        AccessContextResponse response = AccessContextResponse.builder()
                .userId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .email("admin@test.com")
                .name("Admin")
                .isAdmin(true)
                .isSuperAdmin(true)
                .isReseller(false)
                .isPlatformAdmin(true)
                .build();

        // When
        String json = mapper.writeValueAsString(response);

        // Then - must use "is" prefix, not bare name
        assertThat(json).contains("\"isAdmin\":true");
        assertThat(json).contains("\"isSuperAdmin\":true");
        assertThat(json).contains("\"isReseller\":false");
        assertThat(json).contains("\"isPlatformAdmin\":true");

        // Must NOT have bare boolean names (Jackson bug with primitive boolean)
        assertThat(json).doesNotContain("\"admin\":true");
        assertThat(json).doesNotContain("\"superAdmin\":true");
        assertThat(json).doesNotContain("\"platformAdmin\":true");
    }

    @Test
    void shouldSerializeEntitlementsAsStringArray() throws Exception {
        // Given
        AccessContextResponse response = AccessContextResponse.builder()
                .entitlements(Set.of("HELPDESK_MODULE", "FINANCE_MODULE"))
                .build();

        // When
        String json = mapper.writeValueAsString(response);

        // Then
        assertThat(json).contains("\"entitlements\":");
        assertThat(json).contains("HELPDESK_MODULE");
        assertThat(json).contains("FINANCE_MODULE");
        // Entitlements should be simple strings, not objects
        assertThat(json).doesNotContain("featureCode");
    }

    @Test
    void shouldDeserializeFullContextFromUserService() throws Exception {
        // Given - simulates what user-service returns (EntitlementInfo objects)
        String userServiceJson = """
            {
                "userId": "11111111-1111-1111-1111-111111111111",
                "email": "admin@test.com",
                "name": "Admin User",
                "hasTenant": true,
                "currentTenant": {
                    "id": "00000000-0000-0000-0000-000000000000",
                    "name": "Test Org",
                    "slug": "test-org",
                    "type": "PLATFORM",
                    "status": "ACTIVE"
                },
                "availableTenants": [
                    {
                        "id": "00000000-0000-0000-0000-000000000000",
                        "name": "Test Org",
                        "slug": "test-org",
                        "type": "PLATFORM",
                        "status": "ACTIVE"
                    }
                ],
                "roles": ["ADMIN", "USER"],
                "permissions": ["HELPDESK_VIEW", "BILLING_MANAGE"],
                "entitlements": [
                    {"id": "e1", "featureCode": "HELPDESK_MODULE", "featureName": "Help Desk", "enabled": true, "usageLimit": null, "currentUsage": null},
                    {"id": "e2", "featureCode": "helpdesk_agents", "featureName": "Helpdesk Agents", "enabled": true, "usageLimit": 5, "currentUsage": 2},
                    {"id": "e3", "featureCode": "CORE_MODULE", "featureName": "Core", "enabled": true, "usageLimit": null, "currentUsage": null}
                ],
                "isAdmin": true,
                "isSuperAdmin": false,
                "isReseller": false,
                "isPlatformAdmin": true
            }
            """;

        // When - auth-service deserializes the response
        AccessContextResponse response = mapper.readValue(userServiceJson, AccessContextResponse.class);

        // Then - entitlements extracted as feature code strings
        assertThat(response.getEntitlements())
                .containsExactlyInAnyOrder("HELPDESK_MODULE", "helpdesk_agents", "CORE_MODULE");

        // Boolean flags deserialized correctly
        assertThat(response.getIsAdmin()).isTrue();
        assertThat(response.getIsSuperAdmin()).isFalse();
        assertThat(response.getIsReseller()).isFalse();
        assertThat(response.getIsPlatformAdmin()).isTrue();

        // User info
        assertThat(response.getUserId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(response.getEmail()).isEqualTo("admin@test.com");
        assertThat(response.getHasTenant()).isTrue();

        // Tenant info
        assertThat(response.getCurrentTenant()).isNotNull();
        assertThat(response.getCurrentTenant().getName()).isEqualTo("Test Org");
        assertThat(response.getAvailableTenants()).hasSize(1);

        // Roles and permissions
        assertThat(response.getRoles()).containsExactlyInAnyOrder("ADMIN", "USER");
        assertThat(response.getPermissions()).containsExactlyInAnyOrder("HELPDESK_VIEW", "BILLING_MANAGE");

        // When re-serialized for frontend - entitlements should be string array
        String frontendJson = mapper.writeValueAsString(response);
        assertThat(frontendJson).contains("\"isAdmin\":true");
        assertThat(frontendJson).contains("\"isPlatformAdmin\":true");
        assertThat(frontendJson).doesNotContain("\"admin\":true");
        assertThat(frontendJson).doesNotContain("\"platformAdmin\":true");
    }

    @Test
    void shouldHandleNullBooleanFlags() throws Exception {
        // Given - user-service might not send all flags
        String json = """
            {
                "userId": "11111111-1111-1111-1111-111111111111",
                "entitlements": []
            }
            """;

        // When
        AccessContextResponse response = mapper.readValue(json, AccessContextResponse.class);

        // Then - null booleans should not throw
        assertThat(response.getIsAdmin()).isNull();
        assertThat(response.getIsPlatformAdmin()).isNull();
    }
}
