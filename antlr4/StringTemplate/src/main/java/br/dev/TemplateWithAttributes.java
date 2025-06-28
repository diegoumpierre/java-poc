package br.dev;

import org.stringtemplate.v4.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TemplateWithAttributes {
    public static void main(String[] args) throws IOException {

        STGroup group = getStGroup();

        // Create attributes
        List<Map<String, String>> attributes = new ArrayList<>();
        attributes.add(Map.of("type", "String", "name", "firstName"));
        attributes.add(Map.of("type", "int", "name", "age"));
        attributes.add(Map.of("type", "boolean", "name", "active"));

        // Fill template
        ST template = group.getInstanceOf("classFile");
        template.add("packageName", "com.example.generated");
        template.add("className", "Person");
        template.add("attributes", attributes);

        // Render result
        String result = template.render();

        // Save to .java file
        Files.writeString(Paths.get("Person.java"), result);
        System.out.println("File generated: Person.java");
    }

    private static STGroup getStGroup() {
        URL resource = TemplateWithAttributes.class.getClassLoader().getResource("templates/classTemplate.stg");
        if (resource == null) {
            throw new RuntimeException("Could not find classTemplate.stg in resources!");
        }

        // Important: convert URL to file path string
        String templatePath = Paths.get(resource.getPath()).toString();

        // Load .stg template
        STGroup group = new STGroupFile(templatePath);
        return group;
    }
}