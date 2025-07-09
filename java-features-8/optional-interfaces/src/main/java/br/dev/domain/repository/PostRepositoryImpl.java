package br.dev.domain.repository;

import br.dev.domain.Post;

import java.util.Optional;

public class PostRepositoryImpl implements PostRepository {
    @Override
    public Optional<Post> findById(int id) {
        if (id == 1) {
            return Optional.of(new Post("Hello from Java 8"));
        }
        return Optional.empty(); // Instead of null
    }
}
