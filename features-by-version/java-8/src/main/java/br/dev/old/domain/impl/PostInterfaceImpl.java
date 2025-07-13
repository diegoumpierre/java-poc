package java.br.dev.old.domain.impl;

import java.br.dev.domain.User;
import java.br.dev.old.domain.PostInterface;

public class PostInterfaceImpl implements PostInterface {

    @Override
    public User getAuthor() {
        User user = new User();
        user.setEmail("diego@umpierre.com");
        return user;
    }
}
