package com.example.trafficandroidapp.models;

public class UserResponse {
    private String email;
    private String nombreCompleto; // Matches backend field name
    private String role;
    private String avatar;

    // Getters
    public String getEmail() { return email; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getRole() { return role; }
    public String getAvatar() { return avatar; }
}