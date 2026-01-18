package com.example.trafficandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.api.RetrofitClient;
import com.example.trafficandroidapp.api.TrafficApiService;
import com.example.trafficandroidapp.models.Camera;
import com.example.trafficandroidapp.db.AppDatabase;
import com.example.trafficandroidapp.db.CameraEntity;

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
    private TextView txtCameraName, txtCameraRoad, txtCameraKm;
    private CameraEntity selectedCamera;
    private List<Marker> allCameraMarkers = new ArrayList<>();
    private AppDatabase db;

    private static final String TAG = "TrafficApp";
    private static final double MIN_ZOOM_FOR_MARKERS = 10.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_maps);

        map = findViewById(R.id.map);
        cameraInfo = findViewById(R.id.cameraInfo);
        scrim = findViewById(R.id.scrim);

        txtCameraName = findViewById(R.id.txtCameraName);
        txtCameraRoad = findViewById(R.id.txtCameraRoad);
        txtCameraKm = findViewById(R.id.txtCameraKm);

        db = AppDatabase.getInstance(this);

        findViewById(R.id.btnDetails).setOnClickListener(v -> {
            if (selectedCamera == null) return;

            Intent intent = new Intent(MapsActivity.this, CameraDetailsActivity.class);
            intent.putExtra("name", selectedCamera.cameraName);
            intent.putExtra("road", selectedCamera.road);
            intent.putExtra("km", selectedCamera.kilometer);
            intent.putExtra("lat", selectedCamera.latitude);
            intent.putExtra("lon", selectedCamera.longitude);
            intent.putExtra("image", selectedCamera.urlImage);

            startActivity(intent);
        });

        findViewById(R.id.btnClose).setOnClickListener(v -> {
            closeCameraInfo();
        });

        scrim.setOnClickListener(v -> closeCameraInfo());

        findViewById(R.id.btnClose).setOnClickListener(v -> {
            cameraInfo.setVisibility(View.GONE);
            closeCameraInfo();
        });

        initMap();

        // 1. Cargar caché (Room)
        loadFromLocalDatabase();

        // 2. Cargar red (API) - Una sola llamada
        loadDataFromApi();
    }

    private void initMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(43.2627, -2.9252);
        map.getController().setZoom(11.0);
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

    // --- CARGA DE DATOS ---

    private void loadFromLocalDatabase() {
        new Thread(() -> {
            List<CameraEntity> entities = db.cameraDao().getAll();
            if (entities != null && !entities.isEmpty()) {
                runOnUiThread(() -> {
                    Log.d(TAG, "Cargando desde caché: " + entities.size());
                    if (allCameraMarkers.isEmpty()) {
                        addEntitiesToMap(entities);
                    }
                });
            }
        }).start();
    }

    private void loadDataFromApi() {
        TrafficApiService service = RetrofitClient.getService();
        service.getCameras().enqueue(new Callback<List<Camera>>() {
            @Override
            public void onResponse(Call<List<Camera>> call, Response<List<Camera>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Camera> camerasFromApi = response.body();

                    saveToRoom(camerasFromApi);

                    clearMapMarkers();
                    addCamerasToMap(camerasFromApi);
                }
            }

            @Override
            public void onFailure(Call<List<Camera>> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Error de red, usando caché", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToRoom(List<Camera> cameras) {
        new Thread(() -> {
            List<CameraEntity> entities = new ArrayList<>();
            for (Camera c : cameras) {
                entities.add(new CameraEntity(
                        c.id, c.nombre, c.latitud, c.longitud, c.carretera, c.kilometro, c.urlImage
                ));
            }
            db.cameraDao().insertAll(entities);
            Log.d(TAG, "Caché actualizada en SQLite.");
        }).start();
    }

    private void clearMapMarkers() {
        for (Marker m : allCameraMarkers) {
            map.getOverlays().remove(m);
        }
        allCameraMarkers.clear();
    }

    private void addCamerasToMap(List<Camera> cameras) {
        double currentZoom = map.getZoomLevelDouble();

        for (Camera cam : cameras) {
            // Usamos 'direccion' si 'carretera' viene null, para tener algo que mostrar
            String displayRoad = (cam.carretera != null) ? cam.carretera : cam.direccion;
            createMarker(cam.latitud, cam.longitud, cam.nombre, displayRoad, cam.kilometro, cam.urlImage, currentZoom);
        }
        map.invalidate();
    }

    private void addEntitiesToMap(List<CameraEntity> entities) {
        double currentZoom = map.getZoomLevelDouble();
        for (CameraEntity e : entities) {
            createMarker(e.latitude, e.longitude, e.cameraName, e.road, e.kilometer, e.urlImage, currentZoom);
        }
        map.invalidate();
    }

    private void createMarker(String latStr, String lonStr, String name, String road, String km, String urlImage, double zoom) {
        try {
            if (latStr == null || lonStr == null) return;

            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);

            Marker m = new Marker(map);
            m.setPosition(new GeoPoint(lat, lon));
            m.setIcon(getDrawable(R.drawable.ic_camera));

            m.setTitle(name);

            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            m.setEnabled(zoom >= MIN_ZOOM_FOR_MARKERS);

            m.setOnMarkerClickListener((marker, mapView) -> {
                txtCameraName.setText((name != null ? name : "-"));
                txtCameraRoad.setText((road != null ? road : "-"));
                txtCameraKm.setText((km != null ? km : "-"));

                selectedCamera = new CameraEntity(
                        "tmp",
                        name,
                        String.valueOf(lat),
                        String.valueOf(lon),
                        road,
                        km,
                        urlImage
                );

                scrim.setVisibility(View.VISIBLE);
                cameraInfo.setVisibility(View.VISIBLE);
                map.getController().animateTo(marker.getPosition());

                return true;
            });

            allCameraMarkers.add(m);
            map.getOverlays().add(m);
        } catch (Exception e) {
            Log.e(TAG, "Error creating marker: " + e.getMessage());
        }
    }

    private void closeCameraInfo() {
        scrim.setVisibility(View.GONE);
        cameraInfo.setVisibility(View.GONE);
    }

    private void updateMarkersVisibility(double zoomLevel) {
        boolean visible = zoomLevel >= MIN_ZOOM_FOR_MARKERS;
        for (Marker m : allCameraMarkers) {
            if (m.isEnabled() != visible) {
                m.setEnabled(visible);
            }
        }
        map.invalidate();
    }

    @Override
    protected void onResume() { super.onResume(); map.onResume(); }
    @Override
    protected void onPause() { super.onPause(); map.onPause(); }
}
