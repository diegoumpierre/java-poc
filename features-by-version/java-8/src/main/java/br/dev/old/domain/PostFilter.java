package br.dev.old.domain;

import br.dev.domain.Post;

@FunctionalInterface
public interface PostFilter {
    boolean filter(Post post);

    default PostFilter and(PostFilter other) {
        return post -> this.filter(post) && other.filter(post);
    }
}
