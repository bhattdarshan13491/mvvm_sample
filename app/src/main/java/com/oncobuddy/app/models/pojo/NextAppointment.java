package com.oncobuddy.app.models.pojo;

public class NextAppointment {

    private int id;
    private String name;

    public NextAppointment(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public NextAppointment() {
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
