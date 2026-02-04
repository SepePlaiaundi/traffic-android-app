package com.example.trafficandroidapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;

@Entity(tableName = "incidences")
public class Incidence implements Serializable {

    @PrimaryKey // Ahora 'id' es la clave primaria real
    @SerializedName("id")
    public int id;

    // Puedes borrar idModel si el backend no lo envía en el JSON
    // @SerializedName("idModel")
    // public int idModel;

    public String provincia;
    public String causa;
    public String ciudad;
    public String carretera;
    public String direccion;

    public double latitud;
    public double longitud;

    public String nivel;
    public String tipo;
    public String descripcion;

    public GeoPoint getGeoPoint() {
        if (latitud == 0 && longitud == 0) return null;
        return new GeoPoint(latitud, longitud);
    }

    public String getTitle() {
        return (tipo != null && !tipo.isEmpty()) ? tipo : "Incidencia";
    }
}