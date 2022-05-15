package com.oncobuddy.app.models.pojo;

public class LiveVideo {

    private int id;
    private String name;

    public LiveVideo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public LiveVideo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
