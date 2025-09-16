package br.dev;

import br.dev.domain.Post;
import br.dev.domain.User;
import java.util.List;
import java.util.stream.Collectors;

public class TextBlocksExample {
    public static void main(String[] args) {
        User user = new User(
                "Alice",
                "alice@example.com",
                List.of(
                        new Post("Hello World", "First post content", true),
                        new Post("Draft Notes", "Work in progress", false)
                )
        );

        // Example 1: Format JSON using a text block
        String json = """
                {
                  "name": "%s",
                  "email": "%s",
                  "posts": [
                %s
                  ]
                }
                """.formatted(
                user.getName(),
                user.getEmail(),
                user.getPosts().stream()
                        .map(p -> "    { \"title\": \"%s\", \"published\": %s }"
                                .formatted(escapeJson(p.getTitle()), p.isPublished()))
                        .collect(Collectors.joining(",\n"))
        );

        System.out.println("--- JSON Output ---");
        System.out.println(json);

        // Example 2: Format HTML using a text block
        String html = """
                <html>
                  <body>
                    <h1>User: %s</h1>
                    <ul>
                %s
                    </ul>
                  </body>
                </html>
                """.formatted(
                user.getName(),
                user.getPosts().stream()
                        .map(p -> "      <li>" + escapeHtml(p.getTitle()) + (p.isPublished() ? " (published)" : " (draft)") + "</li>")
                        .collect(Collectors.joining("\n"))
        );

        System.out.println("\n--- HTML Output ---");
        System.out.println(html);
    }

    // Minimal JSON string escaper for quotes (enough for this example).
    private static String escapeJson(String in) {
        return in.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Minimal HTML escaper for < and > (enough for this example).
    private static String escapeHtml(String in) {
        return in.replace("<", "&lt;").replace(">", "&gt;");
    }
}

