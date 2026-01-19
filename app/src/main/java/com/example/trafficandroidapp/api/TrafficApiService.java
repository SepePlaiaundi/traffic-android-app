package com.example.trafficandroidapp.api;

import com.example.trafficandroidapp.models.Camera;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TrafficApiService {
    @GET("camara")
    Call<List<Camera>> getCameras();
}