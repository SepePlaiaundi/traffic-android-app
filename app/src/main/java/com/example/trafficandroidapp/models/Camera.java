package com.example.trafficandroidapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import org.osmdroid.util.GeoPoint;

@Entity(tableName = "cameras")
public class Camera {

    @PrimaryKey
    @NonNull
    @SerializedName("id") // El 'id' de la cámara del JSON
    public int id;

    @SerializedName("nombre")
    public String name;

    @SerializedName("carretera")
    public String road;

    @SerializedName("direccion")
    public String address;

    @SerializedName("kilometro")
    public String kilometer;

    @SerializedName("latitud")
    public double latitude; // Cambiado a double

    @SerializedName("longitud")
    public double longitude; // Cambiado a double

    @SerializedName("urlImage")
    public String urlImage;

    public Camera() {}

    public String getDisplayRoad() {
        return (road != null && !road.isEmpty()) ? road : address;
    }

    public GeoPoint getGeoPoint() {
        // Al ser double, ya no necesitas Double.parseDouble()
        if (latitude == 0 && longitude == 0) return null;
        return new GeoPoint(latitude, longitude);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKilometer() {
        return kilometer;
    }

    public void setKilometer(String kilometer) {
        this.kilometer = kilometer;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}