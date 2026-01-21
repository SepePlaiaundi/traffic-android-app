package com.example.trafficandroidapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookmarks")
public class Bookmark {

    @PrimaryKey
    @NonNull
    private String cameraId;

    // Constructor requerido por Room
    public Bookmark(@NonNull String cameraId) {
        this.cameraId = cameraId;
    }

    @NonNull
    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(@NonNull String cameraId) {
        this.cameraId = cameraId;
    }
}
