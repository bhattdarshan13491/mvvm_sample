package com.oncobuddy.app.models.pojo.forums.trending_videos;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VideoCategories {

    private int id;

    private String name;

    private ArrayList<TrendingVideoDetails> trendingVideoDetailsList;


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

    public ArrayList<TrendingVideoDetails> getTrendingVideoDetailsList() {
        return trendingVideoDetailsList;
    }

    public void setTrendingVideoDetailsList(ArrayList<TrendingVideoDetails> trendingVideoDetailsList) {
        this.trendingVideoDetailsList = trendingVideoDetailsList;
    }
}
