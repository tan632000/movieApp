package com.example.movie_streaming.model;

import java.io.Serializable;

public class Favorite implements Serializable {
    private String id;
    private String movieId;
    private String userUid;

    public Favorite() {
    }

    public Favorite(String movieId, String userUid) {
        this.movieId = movieId;
        this.userUid = userUid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "movieId='" + movieId + '\'' +
                ", userId='" + userUid + '\'' +
                '}';
    }
}
