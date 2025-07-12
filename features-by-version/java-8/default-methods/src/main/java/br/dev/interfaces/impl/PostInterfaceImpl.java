package br.dev.interfaces.impl;

import br.dev.domain.User;
import br.dev.interfaces.PostInterface;

public class PostInterfaceImpl implements PostInterface {

    @Override
    public User getAuthor() {
        User user = new User();
        user.setEmail("diego@umpierre.com");
        return user;
    }
}
