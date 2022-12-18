package com.example.movie_streaming.model;

import java.io.Serializable;

public class Movie implements Serializable {
    private String id;
    private String name;
    private String img;
    private String video;
    private String type;

    public Movie() {
    }

    public Movie(String id, String imgUrl, String name, String type, String fileUrl) {
        this.id = id;
        this.name = name;
        this.img = imgUrl;
        this.video = fileUrl;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", video='" + video + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
