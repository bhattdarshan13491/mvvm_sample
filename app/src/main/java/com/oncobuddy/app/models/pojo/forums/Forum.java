package com.oncobuddy.app.models.pojo.forums;

public class Forum {

    private int id;
    private String name;

    public Forum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Forum() {
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
