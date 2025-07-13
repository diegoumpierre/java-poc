package java.br.dev.old.domain.repository;

import java.br.dev.domain.Post;

import java.util.Optional;

public interface PostRepository {

    Optional<Post> findById(int id);

}
