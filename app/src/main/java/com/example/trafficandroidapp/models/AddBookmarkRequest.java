package com.example.trafficandroidapp.models;

public class AddBookmarkRequest {

    private String cameraId;

    public AddBookmarkRequest(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getCameraId() {
        return cameraId;
    }
}