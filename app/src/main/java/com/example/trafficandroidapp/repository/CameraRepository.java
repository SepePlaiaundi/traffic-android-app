package com.example.trafficandroidapp.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trafficandroidapp.api.RetrofitClient;
import com.example.trafficandroidapp.api.CameraApiService;
import com.example.trafficandroidapp.db.AppDatabase;
import com.example.trafficandroidapp.db.dao.CameraDao;
import com.example.trafficandroidapp.models.Camera;
import com.example.trafficandroidapp.security.SessionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraRepository {

    private final CameraApiService api;
    private final CameraDao dao;
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();
    private final Handler handler =
            new Handler(Looper.getMainLooper());
    private final SessionManager sessionManager;

    private static final long REFRESH_INTERVAL_MS =
            5 * 60 * 1000;

    public CameraRepository(Context context) {
        api = RetrofitClient.getInstance()
                .create(CameraApiService.class);
        dao = AppDatabase.getInstance(context).cameraDao();
        sessionManager = new SessionManager(context);

        fetchFromApi();
        startPeriodicRefresh();
    }

    public LiveData<List<Camera>> getCamerasLiveData() {
        return dao.observeAll();
    }

    private void fetchFromApi() {

        String token = sessionManager.getToken();
        if (token == null) return;

        api.getCameras("Bearer " + token)
                .enqueue(new Callback<List<Camera>>() {

                    @Override
                    public void onResponse(
                            Call<List<Camera>> call,
                            Response<List<Camera>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            executor.execute(() ->
                                    dao.insertAll(response.body())
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Camera>> call,
                            Throwable t) {
                    }
                });
    }

    private void startPeriodicRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchFromApi();
                handler.postDelayed(
                        this, REFRESH_INTERVAL_MS);
            }
        }, REFRESH_INTERVAL_MS);
    }
}

