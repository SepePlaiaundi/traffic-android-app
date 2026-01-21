package com.example.trafficandroidapp.models;

public class AddBookmarkRequest {

    private long cameraId;

    public AddBookmarkRequest(long cameraId) {
        this.cameraId = cameraId;
    }

    public long getCameraId() {
        return cameraId;
    }
}