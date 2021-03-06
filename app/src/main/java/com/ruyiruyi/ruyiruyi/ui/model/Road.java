package com.ruyiruyi.ruyiruyi.ui.model;

public class Road {
    public int id;
    public String content;
    public String title;
    public String image;

    public Road(int id, String content, String title, String image) {
        this.id = id;
        this.content = content;
        this.title = title;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}