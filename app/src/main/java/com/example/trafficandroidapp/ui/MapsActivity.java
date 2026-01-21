package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.models.Camera;
import com.example.trafficandroidapp.repository.BookmarkRepository;
import com.example.trafficandroidapp.repository.CameraRepository;

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

public class MapsActivity extends AppCompatActivity {

    private MapView map;
    private View cameraInfo, scrim;
    private TextView txtCameraName, txtCameraRoad, txtCameraKm;

    // UI State
    private Camera selectedCamera;
    private final List<Marker> allCameraMarkers = new ArrayList<>();

    private CameraRepository repository;
    private static final double MIN_ZOOM_FOR_MARKERS = 10.0;

    private ImageButton btnBookmark;
    private BookmarkRepository bookmarkRepository;
    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_maps);
        setupBottomMenu("explore");

        // Inicializar Vistas
        setupViews();

        // Inicializar Mapa
        initMap();

        // Cargar Datos via Repositorio
        repository = new CameraRepository(this);
        loadCameras();
    }

    private void setupViews() {
        map = findViewById(R.id.map);
        cameraInfo = findViewById(R.id.cameraInfo);
        scrim = findViewById(R.id.scrim);
        txtCameraName = findViewById(R.id.txtCameraName);
        txtCameraRoad = findViewById(R.id.txtCameraRoad);
        txtCameraKm = findViewById(R.id.txtCameraKm);
        btnBookmark = findViewById(R.id.btnBookmark);
        bookmarkRepository = new BookmarkRepository(this);

        findViewById(R.id.btnDetails).setOnClickListener(v -> openDetails());
        View.OnClickListener closeAction = v -> closeCameraInfo();
        findViewById(R.id.btnClose).setOnClickListener(closeAction);
        scrim.setOnClickListener(closeAction);
    }

    private void initMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        // Centro en Bilbao aprox
        map.getController().setZoom(11.0);
        map.getController().setCenter(new GeoPoint(43.2627, -2.9252));

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

    private void loadCameras() {
        repository.getCamerasLiveData().observe(this, cameras -> {
            clearMarkers();
            addCamerasToMap(cameras);
        });
    }

    private void clearMarkers() {
        map.getOverlays().removeAll(allCameraMarkers);
        allCameraMarkers.clear();
        map.invalidate();
    }

    private void addCamerasToMap(List<Camera> cameras) {
        double currentZoom = map.getZoomLevelDouble();

        for (Camera cam : cameras) {
            GeoPoint point = cam.getGeoPoint();
            if (point == null) continue; // Saltar si las coordenadas son inválidas

            Marker m = new Marker(map);
            m.setPosition(point);
            m.setIcon(getDrawable(R.drawable.ic_camera));
            m.setTitle(cam.name);
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            // Optimización: Solo habilitar si el zoom es suficiente
            m.setEnabled(currentZoom >= MIN_ZOOM_FOR_MARKERS);

            // Guardar referencia al objeto cámara dentro del marcador si es necesario
            m.setRelatedObject(cam);

            m.setOnMarkerClickListener((marker, mapView) -> {
                showCameraInfo((Camera) marker.getRelatedObject(), marker.getPosition());
                return true;
            });

            allCameraMarkers.add(m);
            map.getOverlays().add(m);
        }
        map.invalidate();
    }

    private void showCameraInfo(Camera cam, GeoPoint position) {
        if (cam == null) return;

        selectedCamera = cam;

        txtCameraName.setText(cam.name != null ? cam.name : "-");
        txtCameraRoad.setText(cam.getDisplayRoad());
        txtCameraKm.setText(cam.kilometer != null ? cam.kilometer : "-");

        long cameraId = Long.parseLong(cam.id);

        bookmarkRepository.isBookmarked(cameraId, bookmarked -> {
            isBookmarked = bookmarked;
            updateBookmarkIcon();
        });

        btnBookmark.setOnClickListener(v -> {
            if (isBookmarked) {
                bookmarkRepository.removeBookmark(cameraId, () -> {
                    isBookmarked = false;
                    updateBookmarkIcon();
                });
            } else {
                bookmarkRepository.addBookmark(cameraId);
                isBookmarked = true;
                updateBookmarkIcon();
            }
        });

        scrim.setVisibility(View.VISIBLE);
        cameraInfo.setVisibility(View.VISIBLE);
        map.getController().animateTo(position);
    }

    private void updateBookmarkIcon() {
        btnBookmark.setImageResource(
                isBookmarked
                        ? R.drawable.ic_bookmark_filled
                        : R.drawable.ic_bookmark
        );
    }

    private void openDetails() {
        if (selectedCamera == null) return;

        Intent intent = new Intent(MapsActivity.this, CameraDetailsActivity.class);
        // Ahora pasamos datos más limpios
        intent.putExtra("name", selectedCamera.name);
        intent.putExtra("road", selectedCamera.getDisplayRoad());
        intent.putExtra("km", selectedCamera.kilometer);
        intent.putExtra("lat", selectedCamera.latitude);
        intent.putExtra("lon", selectedCamera.longitude);
        intent.putExtra("image", selectedCamera.urlImage);
        intent.putExtra("id", selectedCamera.id);
        startActivity(intent);
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

    private void setupBottomMenu(String activeTab) {
        // 1. Referencias a los contenedores (LinearLayouts)
        View btnExplore = findViewById(R.id.menuExplore);
        View btnBookmark = findViewById(R.id.menuBookmark);
        View btnProfile = findViewById(R.id.menuProfile);

        // 2. Referencias a los hijos para cambiar estado (Iconos y Textos)
        // Esto es necesario para que el color cambie usando el selector

        // ESTADO VISUAL: Marcar el activo
        if (activeTab.equals("explore")) {
            setMenuSelected(btnExplore, true);
        } else if (activeTab.equals("bookmark")) {
            setMenuSelected(btnBookmark, true);
        } else if (activeTab.equals("profile")) {
            setMenuSelected(btnProfile, true);
        }

        // 3. LISTENERS DE NAVEGACIÓN

        // Botón Explorar
        btnExplore.setOnClickListener(v -> {
            if (!activeTab.equals("explore")) {
                // Como ya estamos en MapsActivity, si vienes de otra, navegas aquí
                startActivity(new Intent(this, MapsActivity.class));
                overridePendingTransition(0, 0); // Quitar animación
                finish(); // Opcional: cierra la anterior para no acumular
            }
        });

        // Botón Marcador
        btnBookmark.setOnClickListener(v -> {
            if (!activeTab.equals("bookmark")) {
                // Reemplaza BookmarkActivity.class con tu clase real
                Intent intent = new Intent(this, BookmarkActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        // Botón Perfil
        btnProfile.setOnClickListener(v -> {
            if (!activeTab.equals("profile")) {
                // Reemplaza ProfileActivity.class con tu clase real
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    // Helper para activar el estado "selected" en el padre y sus hijos
    private void setMenuSelected(View container, boolean selected) {
        container.setSelected(selected);
        // Forzamos el estado seleccionado en los hijos (Imagen y Texto)
        // para que el selector XML funcione
        if (container instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) container;
            for (int i = 0; i < vg.getChildCount(); i++) {
                vg.getChildAt(i).setSelected(selected);
            }
        }
    }
}