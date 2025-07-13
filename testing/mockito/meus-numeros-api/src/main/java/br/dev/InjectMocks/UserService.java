package br.dev.InjectMocks;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public String getUsernameById(int id) {
        return repository.findUsernameById(id);
    }

}
