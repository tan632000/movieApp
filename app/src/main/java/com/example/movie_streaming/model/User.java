package com.example.movie_streaming.model;


import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String username, password, name, avatar, uid;
    private List<Favorite> favorites;

    public User() {
    }

    public User(String username, String password, String name, List<Favorite> favorites, String img, String uid) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.favorites = favorites;
        this.avatar = img;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String img) {
        this.avatar = img;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
