package com.example.trafficandroidapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookmarks")
public class Bookmark {

    @PrimaryKey
    @NonNull
    private int cameraId;

    // Constructor requerido por Room
    public Bookmark(@NonNull int cameraId) {
        this.cameraId = cameraId;
    }

    @NonNull
    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(@NonNull int cameraId) {
        this.cameraId = cameraId;
    }
}
