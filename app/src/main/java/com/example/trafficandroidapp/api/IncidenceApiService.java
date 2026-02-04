package com.example.trafficandroidapp.api;

import com.example.trafficandroidapp.models.Incidence;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface IncidenceApiService {

    @GET("incidencia") // Coincide con @RequestMapping("/incidencia") del Controller
    Call<List<Incidence>> getIncidences(
            @Header("Authorization") String authHeader
    );
}