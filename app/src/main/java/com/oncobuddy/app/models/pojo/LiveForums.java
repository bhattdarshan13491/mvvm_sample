package com.oncobuddy.app.models.pojo;

public class LiveForums {

    private int id;
    private String name;

    public LiveForums(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public LiveForums() {
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
