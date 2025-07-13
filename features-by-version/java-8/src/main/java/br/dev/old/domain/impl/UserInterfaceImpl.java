package java.br.dev.old.domain.impl;

import java.br.dev.domain.User;
import java.br.dev.old.domain.UserInterface;

public class UserInterfaceImpl implements UserInterface {

    @Override
    public User createUser(String name, String email, String passwordHash) {
        return new User(name, email, passwordHash);
    }
}
