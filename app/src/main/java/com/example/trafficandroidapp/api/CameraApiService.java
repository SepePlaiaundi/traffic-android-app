package com.example.trafficandroidapp.api;

import com.example.trafficandroidapp.models.Camera;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface CameraApiService {
    @GET("camara/mobile")
    Call<List<Camera>> getCameras(
            @Header("Authorization") String authHeader
    );

}