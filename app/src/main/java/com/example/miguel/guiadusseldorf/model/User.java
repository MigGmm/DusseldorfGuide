package com.example.miguel.guiadusseldorf.model;

import java.io.Serializable;

/**
 * Model for work with users.
 */
public class User implements Serializable {

    private String name;
    private String mail;
    private String password;
    private String type;
    private int avatar;

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getType() {
        return type;
    }

    public int getAvatar() { return avatar; }

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAvatar(int avatar) { this.avatar = avatar; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
