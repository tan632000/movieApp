package com.example.movie_streaming.model;

import java.io.Serializable;

public class BannerMovie implements Serializable {
    private long id;
    private String name;
    private String img;
    private String video;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BannerMovie() {
    }

    public BannerMovie(int id, String movieName, String imgUrl, String fileUrl, String type, String desc) {
        this.id = id;
        this.name = movieName;
        this.img = imgUrl;
        this.video = fileUrl;
        this.type = type;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}