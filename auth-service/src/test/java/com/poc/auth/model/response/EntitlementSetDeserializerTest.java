package com.poc.auth.model.response;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for EntitlementSetDeserializer.
 *
 * Validates that entitlements can be deserialized from both formats:
 * - Array of strings (frontend format)
 * - Array of objects with featureCode (user-service format)
 */
class EntitlementSetDeserializerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldDeserializeEntitlementsFromObjectArray() throws Exception {
        // Given - user-service returns EntitlementInfo objects
        String json = """
            {
                "entitlements": [
                    {"id": "aaa", "featureCode": "HELPDESK_MODULE", "enabled": true, "usageLimit": null},
                    {"id": "bbb", "featureCode": "FINANCE_MODULE", "enabled": true, "usageLimit": 100},
                    {"id": "ccc", "featureCode": "helpdesk_agents", "enabled": true, "usageLimit": 5}
                ]
            }
            """;

        // When
        AccessContextResponse response = mapper.readValue(json, AccessContextResponse.class);

        // Then
        assertThat(response.getEntitlements())
                .containsExactlyInAnyOrder("HELPDESK_MODULE", "FINANCE_MODULE", "helpdesk_agents");
    }

    @Test
    void shouldDeserializeEntitlementsFromStringArray() throws Exception {
        // Given - already in string format
        String json = """
            {
                "entitlements": ["HELPDESK_MODULE", "FINANCE_MODULE", "BPF_MODULE"]
            }
            """;

        // When
        AccessContextResponse response = mapper.readValue(json, AccessContextResponse.class);

        // Then
        assertThat(response.getEntitlements())
                .containsExactlyInAnyOrder("HELPDESK_MODULE", "FINANCE_MODULE", "BPF_MODULE");
    }

    @Test
    void shouldHandleEmptyEntitlements() throws Exception {
        // Given
        String json = """
            {
                "entitlements": []
            }
            """;

        // When
        AccessContextResponse response = mapper.readValue(json, AccessContextResponse.class);

        // Then
        assertThat(response.getEntitlements()).isEmpty();
    }

    @Test
    void shouldHandleMixedArray() throws Exception {
        // Given - mix of strings and objects (edge case)
        String json = """
            {
                "entitlements": [
                    "CORE_MODULE",
                    {"featureCode": "HELPDESK_MODULE", "enabled": true}
                ]
            }
            """;

        // When
        AccessContextResponse response = mapper.readValue(json, AccessContextResponse.class);

        // Then
        assertThat(response.getEntitlements())
                .containsExactlyInAnyOrder("CORE_MODULE", "HELPDESK_MODULE");
    }

    @Test
    void shouldSkipObjectsWithoutFeatureCode() throws Exception {
        // Given - object without featureCode field
        String json = """
            {
                "entitlements": [
                    {"id": "aaa", "enabled": true},
                    {"featureCode": "HELPDESK_MODULE", "enabled": true}
                ]
            }
            """;

        // When
        AccessContextResponse response = mapper.readValue(json, AccessContextResponse.class);

        // Then
        assertThat(response.getEntitlements())
                .containsExactly("HELPDESK_MODULE");
    }

    @Test
    void shouldDeserializeNullEntitlementsAsNull() throws Exception {
        // Given - no entitlements field
        String json = """
            {
                "userId": "11111111-1111-1111-1111-111111111111"
            }
            """;

        // When
        AccessContextResponse response = mapper.readValue(json, AccessContextResponse.class);

        // Then
        assertThat(response.getEntitlements()).isNull();
    }
}
