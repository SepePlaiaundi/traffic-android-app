package com.example.trafficandroidapp.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cameras")
public class CameraEntity {
    @PrimaryKey
    @NonNull
    public String cameraId;
    public String cameraName;
    public String latitude;
    public String longitude;
    public String road;
    public String kilometer;
    public String urlImage;

    public CameraEntity(@NonNull String cameraId, String cameraName, String latitude,
                        String longitude, String road, String kilometer, String urlImage) {
        this.cameraId = cameraId;
        this.cameraName = cameraName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.road = road;
        this.kilometer = kilometer;
        this.urlImage = urlImage;
    }
}