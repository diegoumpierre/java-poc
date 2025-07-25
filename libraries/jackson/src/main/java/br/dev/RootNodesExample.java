package br.dev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RootNodesExample {


    public static String customJson(long id, String name) {

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        root.put("id", id);
        root.put("name", name);

        ArrayNode roles = root.putArray("roles");
        roles.add("admin").add("editor");

        ObjectNode meta = root.putObject("meta");
        meta.put("active", true);
        meta.put("loginCount", 42);

        try {
            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Could not serialize JSON", e);
        }


    }

    public static void main(String[] args) {
        String json = customJson(1, "Diego");
        System.out.println(json);
    }
}
