package java.br.dev.old.domain.impl;

import java.br.dev.domain.Post;
import java.br.dev.domain.User;
import java.br.dev.old.domain.PostInterface;
import java.br.dev.old.domain.UserInterface;

public class UserPostInterfaceImpl implements PostInterface, UserInterface {
    @Override
    public User createUser(String name, String email, String passwordHash) {
        return new User(name, email, passwordHash);
    }


    @Override
    public Post addPost(Long userId, String title, String content) {
        return PostInterface.super.addPost(userId, title, content);
    }

    @Override
    public User getAuthor() {
        User user = new User();
        user.setEmail("other@umpierre.com.br");
        return user;
    }
}
