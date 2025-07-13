package br.dev.old.domain.impl;

import br.dev.domain.User;
import br.dev.old.domain.UserInterface;

public class UserInterfaceImpl implements UserInterface {

    @Override
    public User createUser(String name, String email, String passwordHash) {
       // return new User(name, email, passwordHash);
        return null;
    }
}
