package com.example.trafficandroidapp.models;

import com.google.gson.annotations.SerializedName;

public class Camera {
    // Usamos @SerializedName para mapear el JSON exacto a variables Java
    @SerializedName("id")
    public String id;

    @SerializedName("direccion")
    public String direccion;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("kilometro")
    public String kilometro;

    @SerializedName("latitud")
    public String latitud;

    @SerializedName("longitud")
    public String longitud;

    @SerializedName("carretera")
    public String carretera;

    @SerializedName("urlImage")
    public String urlImage;
}