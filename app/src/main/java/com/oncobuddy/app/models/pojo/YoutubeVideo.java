package com.oncobuddy.app.models.pojo;

public class YoutubeVideo {

    private int id;
    private String title;
    private String videoUrl;

    public YoutubeVideo(int id, String title, String videoUrl) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
    }

    public YoutubeVideo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
