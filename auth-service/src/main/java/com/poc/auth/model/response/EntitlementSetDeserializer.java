package com.poc.auth.model.response;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.ArrayNode;

import java.util.HashSet;
import java.util.Set;

/**
 * Deserializes entitlements from user-service response.
 * Handles both formats:
 * - Array of strings: ["HELPDESK_MODULE", "FINANCE_MODULE"]
 * - Array of objects: [{"featureCode": "HELPDESK_MODULE", "enabled": true, ...}]
 */
public class EntitlementSetDeserializer extends StdDeserializer<Set<String>> {

    public EntitlementSetDeserializer() {
        super(Set.class);
    }

    @Override
    public Set<String> deserialize(JsonParser p, DeserializationContext ctxt) {
        Set<String> result = new HashSet<>();
        JsonNode node = (JsonNode) p.readValueAsTree();

        if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            for (int i = 0; i < array.size(); i++) {
                JsonNode element = array.get(i);
                if (element.isTextual()) {
                    result.add(element.asText());
                } else if (element.isObject()) {
                    JsonNode featureCode = element.get("featureCode");
                    if (featureCode != null && featureCode.isTextual()) {
                        result.add(featureCode.asText());
                    }
                }
            }
        }

        return result;
    }
}
