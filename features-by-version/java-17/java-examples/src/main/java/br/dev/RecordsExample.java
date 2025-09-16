package br.dev;


public class RecordsExample {


    // Define a record for a User DTO
    public record UserRecord(String name, String email) {
        // You can add methods if needed
        public String contactInfo() {
            return name + " <" + email + ">";
        }
    }

    // Define a record for a Post DTO
    public record PostRecord(String title, String content, boolean published) {}

    public static void main(String[] args) {

        // Create and use a UserRecord
        UserRecord user = new UserRecord("Alice", "alice@example.com");
        System.out.println("User: " + user.name() + ", email=" + user.email());

        // Create and use a PostRecord
        PostRecord post = new PostRecord("Hello World", "This is a post.", true);
        System.out.println("Post: title='" + post.title() + "', published=" + post.published());


        System.out.println("Call a method inside the record: " +user.contactInfo());

        // Records are immutable, so the following is not allowed:
        // user.name = "Bob"; // Compile error
    }
}

