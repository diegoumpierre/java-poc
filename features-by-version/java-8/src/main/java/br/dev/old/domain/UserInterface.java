package java.br.dev.old.domain;


import java.br.dev.domain.Post;
import java.br.dev.domain.User;

public interface UserInterface {

    User createUser(String name, String email, String passwordHash);

    default Post addPost(Long userId, String title, String content) {
        System.out.println("Adding post for user ID " + userId + " CLASS: " + this.getClass().getSimpleName());
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(new User());
        return post;
    }

}
