package com.example.trafficandroidapp.api;

import com.example.trafficandroidapp.models.Incidence;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface IncidenceApiService {

    @GET("incidencia") // Coincide con @RequestMapping("/incidencia") del Controller
    Call<List<Incidence>> getIncidences(
            @Header("Authorization") String authHeader
    );

    @POST("incidencia")
    Call<Void> saveIncidence(
            @Header("Authorization") String authHeader,
            @Body Incidence incidence
    );
}