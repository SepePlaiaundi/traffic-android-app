package com.example.trafficandroidapp.api;

import com.example.trafficandroidapp.models.Bookmark;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import java.util.List;

public interface BookmarkApiService {

    @GET("bookmarks")
    Call<List<Bookmark>> getBookmarks(
            @Header("Authorization") String authHeader
    );
}
