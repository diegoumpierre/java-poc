package br.dev.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void testPostCreation() {
        Post post = new Post();
        post.setTitle("Test Title");
        post.setContent("This is a test content.");

        User author = new User();
        author.setName("John Doe");
        author.setEmail("john@doe.com");

        post.setAuthor(author);

        assertEquals("Test Title", post.getTitle());
        assertEquals("This is a test content.", post.getContent());
        assertNotNull(post.getAuthor());

    }
}