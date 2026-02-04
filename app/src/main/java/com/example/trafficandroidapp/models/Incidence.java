package com.example.trafficandroidapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;

@Entity(tableName = "incidences")
public class Incidence implements Serializable {

    @PrimaryKey(autoGenerate = true) // Recomendado añadir autoGenerate para Room local
    @SerializedName("id")
    public Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCarretera() {
        return carretera;
    }

    public void setCarretera(String carretera) {
        this.carretera = carretera;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}