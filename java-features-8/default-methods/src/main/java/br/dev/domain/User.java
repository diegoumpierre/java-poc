package br.dev.domain;

import java.util.List;

public class User {
    private Long id;
    private String name;
    private String email;
    private String passwordHash;

    private List<Post> posts;

}
