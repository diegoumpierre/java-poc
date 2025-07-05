package br.dev.interfaces;

import br.dev.domain.Post;

public interface PostInterface {

    default Post getPostById(Long id) {
        System.out.println("Fetching post with ID " + id);
        return null; // Placeholder return value
    }

   default Post addPostToUser(Long userId, Long postId) {
       Post post = getPostById(postId);
         if (post == null) {
              throw new RuntimeException("Post with ID " + postId + " not found");
         }
         return post;
    }

}
