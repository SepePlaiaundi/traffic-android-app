package com.example.trafficandroidapp.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.trafficandroidapp.api.IncidenceApiService;
import com.example.trafficandroidapp.api.RetrofitClient;
import com.example.trafficandroidapp.db.AppDatabase;
import com.example.trafficandroidapp.db.dao.IncidenceDao;
import com.example.trafficandroidapp.models.Incidence;
import com.example.trafficandroidapp.security.SessionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidenceRepository {

    private final IncidenceApiService api;
    private final IncidenceDao dao;
    private final SessionManager sessionManager;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private static final long REFRESH_INTERVAL_MS = 5 * 60 * 1000; // 5 min

    public IncidenceRepository(Context context) {
        api = RetrofitClient.getInstance().create(IncidenceApiService.class);
        dao = AppDatabase.getInstance(context).incidenceDao();
        sessionManager = new SessionManager(context);

        fetchFromApi();
        startPeriodicRefresh();
    }

    public LiveData<List<Incidence>> getIncidencesLiveData() {
        return dao.observeAll();
    }

    private void fetchFromApi() {
        String token = sessionManager.getToken();
        // Si la API es pública (Cross Origin *) quizás no necesites token,
        // pero lo dejo por coherencia con CameraRepository
        if (token == null) token = ""; // O return si es obligatorio

        api.getIncidences("Bearer " + token).enqueue(new Callback<List<Incidence>>() {
            @Override
            public void onResponse(Call<List<Incidence>> call, Response<List<Incidence>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> dao.insertAll(response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<Incidence>> call, Throwable t) {
                // Manejo de error o log
            }
        });
    }

    private void startPeriodicRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchFromApi();
                handler.postDelayed(this, REFRESH_INTERVAL_MS);
            }
        }, REFRESH_INTERVAL_MS);
    }
}