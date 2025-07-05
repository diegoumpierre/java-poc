package br.dev.interfaces;

import br.dev.domain.Post;
import br.dev.domain.User;

public class PostInterfaceImpl implements PostInterface, UserInterface {


    @Override
    public User createUser(String name, String email, String passwordHash) {
        return null;
    }


    @Override
    public Post addPostToUser(Long userId, Long postId) {
        return UserInterface.super.addPostToUser(userId, postId);
    }
}
