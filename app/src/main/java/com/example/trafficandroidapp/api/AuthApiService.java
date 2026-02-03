package com.example.trafficandroidapp.api;

import com.example.trafficandroidapp.models.auth.LoginRequest;
import com.example.trafficandroidapp.models.auth.LoginResponse;
import com.example.trafficandroidapp.models.auth.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("users/register")
    Call<Void> register(@Body RegisterRequest request);
}
