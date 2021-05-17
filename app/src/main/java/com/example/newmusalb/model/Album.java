package com.example.newmusalb.model;

public class Album {

    private final String image;
    private final String title;

    public Album(String title, String image) {
        this.image = image;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }
}
