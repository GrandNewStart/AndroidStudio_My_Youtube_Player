package com.jinwoo.my_youtube_player;

import android.graphics.drawable.Drawable;

public class Video {
    private int ID;
    private String thumbnail;
    private String title;
    private String uploader;
    private String date;
    private String videoID;
    private boolean checked = false;

    public Video(String thumbnail, String title, String uploader, String date, String videoID) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.uploader = uploader;
        this.date = date;
        this.videoID = videoID;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVideoID() { return videoID; }

    public void setVideoID(String videoID) { this.videoID = videoID; }

    public boolean isChecked() { return checked; }

    public void setChecked(boolean check) { checked = check; }

    public int getID() { return ID; }

    public void setID(int ID) { this.ID = ID; }
}
