package br.dev.interfaces;

import br.dev.domain.Post;
import br.dev.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserInterface {
    User createUser(String name, String email, String passwordHash);

    default User getUserById(Long id) {
        User user = getUserById(id);
        if (user == null) {
            throw new RuntimeException("User with ID " + id + " not found");
        }
        return user;
    }

    default Post addPostToUser(Long userId, Long postId) {
        User user = getUserById(userId);
        Post post = new Post(postId, "Sample Post Title", "Sample post content", LocalDateTime.now() ,user);
        List<Post> posts = user.getPosts();
        posts.add(post);
        user.setPosts(posts);
        return post;
    }

}
