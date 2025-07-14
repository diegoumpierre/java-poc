package br.dev.domain;

public class Post {
    private String title;
    private String content;
    private boolean published;

    public Post(String title, String content, boolean published) {
        this.title = title;
        this.content = content;
        this.published = published;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPublished(){return this.published;}
}
