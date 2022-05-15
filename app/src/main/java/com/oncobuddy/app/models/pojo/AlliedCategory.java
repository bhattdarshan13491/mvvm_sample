package com.oncobuddy.app.models.pojo;

import java.util.ArrayList;

public class AlliedCategory {

    private int id;
    private String categoryName;
    private ArrayList<YoutubeVideo> doctorsList;

    public AlliedCategory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<YoutubeVideo> getDoctorsList() {
        return doctorsList;
    }

    public void setDoctorsList(ArrayList<YoutubeVideo> doctorsList) {
        this.doctorsList = doctorsList;
    }
}
