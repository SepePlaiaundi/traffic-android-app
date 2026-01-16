package com.example.trafficandroidapp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TrafficApiService {
    @GET("traffic/v1.0/cameras")
    Call<CameraResponse> getCameras(@Query("_page") int page);
}