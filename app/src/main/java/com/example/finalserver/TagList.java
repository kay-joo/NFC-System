package com.example.finalserver;

//data 클래스
public class TagList {
    private String placeName;
    private String current_time;
    private String exists;

    public TagList(String placeName, String current_time, String exists) {
        this.placeName = placeName;
        this.current_time = current_time;
        this.exists = exists;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public String getExists() {
        return exists;
    }
}
