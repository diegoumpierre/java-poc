package br.dev.domain.repository;

import br.dev.domain.Post;

import java.util.Optional;

public interface PostRepository {

    Optional<Post> findById(int id);

}
