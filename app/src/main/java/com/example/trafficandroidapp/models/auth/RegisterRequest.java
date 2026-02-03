package com.example.trafficandroidapp.models.auth;

public class RegisterRequest {
    private String nombreCompleto;
    private String email;
    private String password;
    private String rol;

    public RegisterRequest(String nombreCompleto, String email, String password) {
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.password = password;
        this.rol = "Usuario";
    }

}