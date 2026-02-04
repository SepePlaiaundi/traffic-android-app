package com.example.trafficandroidapp.models;

public class UserProfileUpdateRequest {
    private String nombreCompleto;
    private String password;
    private String avatar;

    public UserProfileUpdateRequest(String nombreCompleto, String password, String avatar) {
        this.nombreCompleto = nombreCompleto;
        this.password = password;
        this.avatar = avatar;
    }
}