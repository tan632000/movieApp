package com.example.movie_streaming.model;

import java.io.Serializable;

public class Favorite implements Serializable {
    private String name, img, type, video, userUid;
    private long id;

    public Favorite() {
    }

    public Favorite(long id, String name, String img, String type, String video, String userUid) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.type = type;
        this.video = video;
        this.userUid = userUid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
