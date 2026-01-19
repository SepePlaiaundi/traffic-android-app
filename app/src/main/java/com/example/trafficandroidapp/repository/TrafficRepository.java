package com.example.trafficandroidapp.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trafficandroidapp.api.RetrofitClient;
import com.example.trafficandroidapp.api.TrafficApiService;
import com.example.trafficandroidapp.db.AppDatabase;
import com.example.trafficandroidapp.db.CameraDao;
import com.example.trafficandroidapp.models.Camera;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrafficRepository {

    private final TrafficApiService api;
    private final CameraDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private static final long REFRESH_INTERVAL_MS = 5 * 60 * 1000; // 5 minutos

    private final MutableLiveData<List<Camera>> camerasLiveData = new MutableLiveData<>();

    public TrafficRepository(Context context) {
        api = RetrofitClient.getService();
        dao = AppDatabase.getInstance(context).cameraDao();

        // Cargar datos iniciales desde SQLite
        loadFromDatabase();

        // Iniciar refresco periódico
        startPeriodicRefresh();
    }

    // Exponer LiveData para que la Activity observe
    public LiveData<List<Camera>> getCamerasLiveData() {
        return camerasLiveData;
    }

    // Cargar BD inicialmente
    private void loadFromDatabase() {
        executor.execute(() -> {
            List<Camera> local = dao.getAll();
            if (local != null && !local.isEmpty()) {
                camerasLiveData.postValue(local);
            } else {
                // Si está vacío, traer de API
                fetchFromApi();
            }
        });
    }

    // Traer datos de la API y actualizar SQLite y LiveData
    private void fetchFromApi() {
        api.getCameras().enqueue(new Callback<List<Camera>>() {
            @Override
            public void onResponse(Call<List<Camera>> call, Response<List<Camera>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Camera> remote = response.body();
                    executor.execute(() -> {
                        dao.insertAll(remote); // actualizar BD
                        camerasLiveData.postValue(remote); // notificar UI
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Camera>> call, Throwable t) {
                // opcional: manejar error
            }
        });
    }

    // Refresco periódico usando Handler
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
