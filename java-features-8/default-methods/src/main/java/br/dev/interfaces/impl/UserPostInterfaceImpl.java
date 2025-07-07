package br.dev.interfaces.impl;

import br.dev.domain.Post;
import br.dev.domain.User;
import br.dev.interfaces.PostInterface;
import br.dev.interfaces.UserInterface;

public class UserPostInterfaceImpl implements PostInterface, UserInterface {
    @Override
    public User createUser(String name, String email, String passwordHash) {
        return new User(name, email, passwordHash);
    }


    @Override
    public Post addPost(Long userId, String title, String content) {
        return PostInterface.super.addPost(userId, title, content);
    }
}
