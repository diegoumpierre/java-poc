package br.dev.interfaces;

import br.dev.domain.User;

public class UserInterfaceImpl implements UserInterface {

    @Override
    public User createUser(String name, String email, String passwordHash) {
        return new User(name, email, passwordHash);
    }



}
