package java.br.dev.old.domain;

import java.br.dev.domain.Post;

@FunctionalInterface
public interface PostFilter {
    boolean filter(Post post);

    default PostFilter and(PostFilter other) {
        return post -> this.filter(post) && other.filter(post);
    }
}
