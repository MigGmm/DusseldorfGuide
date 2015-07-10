package com.example.miguel.guiadusseldorf.model;

/**
 * Model for work with comments.
 */
public class Comment {

    private String name;
    private String comment;
    private String date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
