package br.dev;

public class Post {

    private String title;
    private String author;

    public Post(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
}
