import br.dev.domain.Post;
import br.dev.domain.User;
import java.util.List;

void main() {
    var posts = List.of(
        new Post("First Post", "Content 1", true),
        new Post("Second Post", "Content 2", false)
    );
    var user = new User("John Doe", "john@example.com", posts);
    System.out.println("User: " + user.getName() + " (" + user.getEmail() + ")");
    for (Post post : user.getPosts()) {
        String status = post.isPublished() ? "Published" : "Draft";
        System.out.println("  - " + status + " Post: '" + post.getTitle() + "'");
    }
}

