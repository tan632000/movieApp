package com.example.movie_streaming.model;

public class Category {
    private String id;
    private String type;

    public Category() {
    }

    public Category(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
