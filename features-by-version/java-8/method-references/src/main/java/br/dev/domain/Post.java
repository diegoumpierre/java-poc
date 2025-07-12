package br.dev.domain;

public class Post {
    private final String title;

    public Post(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static void printStatic(Post post) {
        System.out.println("Static: " + post.getTitle());
    }

    public void printInstance() {
        System.out.println("Instance: " + this.title);
    }
}
