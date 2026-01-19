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
    @SerializedName("id")
    public String id;

    @SerializedName("nombre")
    public String name;

    @SerializedName("carretera") // A veces viene null, usaremos la lógica en el getter
    public String road;

    @SerializedName("direccion")
    public String address;

    @SerializedName("kilometro")
    public String kilometer;

    @SerializedName("latitud")
    public String latitude;

    @SerializedName("longitud")
    public String longitude;

    @SerializedName("urlImage")
    public String urlImage;

    // Constructor vacío necesario para Room/Gson
    public Camera() {}

    // Lógica encapsulada: Obtener carretera o dirección
    public String getDisplayRoad() {
        return (road != null && !road.isEmpty()) ? road : address;
    }

    // Lógica encapsulada: Convertir lat/lon seguro a GeoPoint
    public GeoPoint getGeoPoint() {
        try {
            double lat = Double.parseDouble(latitude);
            double lon = Double.parseDouble(longitude);
            return new GeoPoint(lat, lon);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}