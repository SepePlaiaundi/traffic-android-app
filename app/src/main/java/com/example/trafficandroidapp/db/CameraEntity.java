package com.example.trafficandroidapp.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cameras")
public class CameraEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String cameraId;

    @ColumnInfo(name = "nombre")
    public String cameraName;

    @ColumnInfo(name = "latitud")
    public String latitude;

    @ColumnInfo(name = "longitud")
    public String longitude;

    @ColumnInfo(name = "carretera")
    public String road;

    @ColumnInfo(name = "kilometro")
    public String kilometer;

    @ColumnInfo(name = "url_image")
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