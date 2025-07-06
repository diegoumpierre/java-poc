package br.dev.interfaces.impl;

import br.dev.domain.User;
import br.dev.interfaces.UserInterface;

public class UserInterfaceImpl implements UserInterface {

    @Override
    public User createUser(String name, String email, String passwordHash) {
        return new User(name, email, passwordHash);
    }



}
