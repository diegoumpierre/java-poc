package br.dev.domain;

import java.time.LocalDateTime;

public class Post {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime publishedAt;

    private User author;
}
