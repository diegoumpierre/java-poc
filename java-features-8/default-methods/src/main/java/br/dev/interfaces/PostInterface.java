package br.dev.interfaces;

import br.dev.domain.Post;
import br.dev.domain.User;

public interface PostInterface {

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

}
