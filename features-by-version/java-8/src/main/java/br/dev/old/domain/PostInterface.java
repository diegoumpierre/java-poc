package java.br.dev.old.domain;

import java.br.dev.domain.Post;
import java.br.dev.domain.User;

public interface PostInterface {

    //this way for which implementing the interface,
    // I can use the method getAuthor() to get the author of the post
    // and for each implementation, will be a different way to get the author
    User getAuthor();

    default Post getPostById(Long id) {
        System.out.println("Fetching post with ID " + id);
        return null;
    }

    default Post addPost(Long userId, String title, String content) {
        System.out.println("Adding post for user ID " + userId + " CLASS: " + this.getClass().getSimpleName());
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(new User());
        return post;
    }

    default Post updatePost(Long id, String title, String content) {
        System.out.println("Updating post with ID " + id);
        Post post = new Post();
        post.setAuthor(getAuthor());
        if (post != null) {
            post.setTitle(title);
            post.setContent(content);
        }
        return post;
    }

}
