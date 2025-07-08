package br.dev.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@john.com");
        user.setPasswordHash("hashed_password");
        user.setPosts(new ArrayList<>());

        assertEquals("Alice", user.getName());
        assertEquals("alice@john.com", user.getEmail());
        assertEquals("hashed_password", user.getPasswordHash());
        assertNotNull(user.getPosts());
    }


    @Test
    void testUserWithConstructor() {
        User user = new User("Bob", "bob@john.com", "hashed_password_123");

        assertEquals("Bob", user.getName());
        assertEquals("bob@john.com", user.getEmail());
        assertEquals("hashed_password_123", user.getPasswordHash());
    }

}