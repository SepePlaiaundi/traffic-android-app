package com.example.trafficandroidapp.models;

public class AddBookmarkRequest {

    private double cameraId;

    public AddBookmarkRequest(double cameraId) {
        this.cameraId = cameraId;
    }

    public double getCameraId() {
        return cameraId;
    }
}