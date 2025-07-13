package br.dev.domain.repository;

import br.dev.domain.Post;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PostRepositoryImplTest {


    @Test
    public void testFindById() {
        PostRepository postRepository = new PostRepositoryImpl();
        Optional<Post> postOpt = postRepository.findById(1);
        postOpt.ifPresent(post -> System.out.println(post.getTitle()));

        // Test with a valid ID
        assertTrue(postRepository.findById(1).isPresent());
        assertEquals("Hello from Java 8", postRepository.findById(1).get().getTitle());

        // Test with an invalid ID
        assertFalse(postRepository.findById(2).isPresent());
    }

}