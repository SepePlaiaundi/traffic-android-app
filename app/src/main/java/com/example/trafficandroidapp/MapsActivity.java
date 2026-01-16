package com.example.trafficandroidapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trafficandroidapp.api.CameraResponse;
import com.example.trafficandroidapp.api.RetrofitClient;
import com.example.trafficandroidapp.api.TrafficApiService;
import com.example.trafficandroidapp.db.AppDatabase;
import com.example.trafficandroidapp.db.CameraEntity;
import com.example.trafficandroidapp.utils.CoordinateConverter;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity {

    private MapView map;
    private View cameraInfo, scrim;
    private List<Marker> allCameraMarkers = new ArrayList<>();
    private AppDatabase db;

    private static final String TAG = "TrafficApp";
    private static final double MIN_ZOOM_FOR_MARKERS = 13.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_maps);

        map = findViewById(R.id.map);
        cameraInfo = findViewById(R.id.cameraInfo);
        scrim = findViewById(R.id.scrim);
        db = AppDatabase.getInstance(this);

        initMap();

        // 1. Primero intentamos cargar lo que haya en SQLite (Caché)
        loadFromLocalDatabase();

        // 2. Independientemente de la caché, refrescamos con la API
        loadAllDataSequentially(1);

        findViewById(R.id.btnClose).setOnClickListener(v -> {
            scrim.setVisibility(View.GONE);
            cameraInfo.setVisibility(View.GONE);
        });
    }

    private void initMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(43.3128, -1.9750);
        map.getController().setZoom(14.0);
        map.getController().setCenter(startPoint);

        map.addMapListener(new MapListener() {
            @Override
            public boolean onZoom(ZoomEvent event) {
                updateMarkersVisibility(event.getZoomLevel());
                return false;
            }
            @Override
            public boolean onScroll(ScrollEvent event) { return false; }
        });
    }

    private void loadFromLocalDatabase() {
        new Thread(() -> {
            List<CameraEntity> entities = db.cameraDao().getAll();
            if (entities != null && !entities.isEmpty()) {
                runOnUiThread(() -> {
                    Log.d(TAG, "Cargando " + entities.size() + " cámaras desde caché Room.");
                    addEntitiesToMap(entities);
                });
            }
        }).start();
    }

    private void loadAllDataSequentially(int page) {
        TrafficApiService service = RetrofitClient.getService();
        service.getCameras(page).enqueue(new Callback<CameraResponse>() {
            @Override
            public void onResponse(Call<CameraResponse> call, Response<CameraResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CameraResponse data = response.body();

                    // Guardar página en caché y pintar solo si el mapa está vacío
                    savePageToRoom(data.cameras);

                    // Si es la primera vez que abrimos la app, pintamos directo
                    if (allCameraMarkers.isEmpty()) {
                        addCamerasToMap(data.cameras);
                    }

                    if (data.currentPage < data.totalPages) {
                        loadAllDataSequentially(data.currentPage + 1);
                    } else {
                        Log.d(TAG, "Sync completo con la API.");
                    }
                }
            }
            @Override
            public void onFailure(Call<CameraResponse> call, Throwable t) {
                Log.e(TAG, "Error red página " + page);
            }
        });
    }

    private void savePageToRoom(List<CameraResponse.Camera> cameras) {
        new Thread(() -> {
            List<CameraEntity> entities = new ArrayList<>();
            for (CameraResponse.Camera c : cameras) {
                entities.add(new CameraEntity(c.cameraId, c.cameraName, c.latitude, c.longitude, c.road, c.kilometer, c.urlImage));
            }
            db.cameraDao().insertAll(entities);
        }).start();
    }

    // Para datos que vienen de la API
    private void addCamerasToMap(List<CameraResponse.Camera> cameras) {
        double currentZoom = map.getZoomLevelDouble();
        for (CameraResponse.Camera cam : cameras) {
            createMarker(cam.latitude, cam.longitude, cam.cameraName, cam.road, cam.kilometer, currentZoom);
        }
        map.invalidate();
    }

    // Para datos que vienen de Room
    private void addEntitiesToMap(List<CameraEntity> entities) {
        double currentZoom = map.getZoomLevelDouble();
        for (CameraEntity e : entities) {
            createMarker(e.latitude, e.longitude, e.cameraName, e.road, e.kilometer, currentZoom);
        }
        map.invalidate();
    }

    private void createMarker(String lat, String lon, String name, String road, String km, double zoom) {
        try {
            double[] latLon = CoordinateConverter.utmToLatLon(Double.parseDouble(lon), Double.parseDouble(lat));
            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(latLon[0], latLon[1]));
            m.setIcon(getDrawable(R.drawable.ic_camera));
            m.setTitle(name);
            m.setSnippet(road + " km " + km);
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setEnabled(zoom >= MIN_ZOOM_FOR_MARKERS);
            m.setOnMarkerClickListener((marker, mapView) -> {
                scrim.setVisibility(View.VISIBLE);
                cameraInfo.setVisibility(View.VISIBLE);
                return true;
            });
            allCameraMarkers.add(m);
            map.getOverlays().add(m);
        } catch (Exception e) {
            Log.e(TAG, "Error en marcador");
        }
    }

    private void updateMarkersVisibility(double zoomLevel) {
        boolean visible = zoomLevel >= MIN_ZOOM_FOR_MARKERS;
        for (Marker m : allCameraMarkers) {
            m.setEnabled(visible);
        }
        map.invalidate();
    }

    @Override
    protected void onResume() { super.onResume(); map.onResume(); }
    @Override
    protected void onPause() { super.onPause(); map.onPause(); }
}