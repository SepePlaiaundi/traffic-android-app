package com.example.trafficandroidapp.api;

import com.example.trafficandroidapp.models.auth.LoginRequest;
import com.example.trafficandroidapp.models.auth.LoginResponse;
import com.example.trafficandroidapp.models.auth.RegisterRequest;
import com.example.trafficandroidapp.models.UserProfileUpdateRequest;
import com.example.trafficandroidapp.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AuthApiService {

    @POST("users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("users/register")
    Call<Void> register(@Body RegisterRequest request);

    // New endpoints
    @GET("users/me")
    Call<UserResponse> getProfile(@Header("Authorization") String token);

    @PUT("users/profile/update")
    Call<Void> updateProfile(@Header("Authorization") String token, @Body UserProfileUpdateRequest request);
}