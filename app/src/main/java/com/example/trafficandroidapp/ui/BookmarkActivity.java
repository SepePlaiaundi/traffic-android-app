package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.models.Bookmark;
import com.example.trafficandroidapp.models.Camera;
import com.example.trafficandroidapp.repository.BookmarkRepository;
import com.example.trafficandroidapp.repository.CameraRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookmarkActivity extends AppCompatActivity {

    private BookmarkRepository bookmarkRepository;
    private CameraRepository cameraRepository;
    private BookmarkAdapter adapter;

    // IDs de cámaras marcadas
    private final Set<Long> bookmarkedCameraIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        bookmarkRepository = new BookmarkRepository(this);
        cameraRepository = new CameraRepository(this);

        setupRecyclerView();
        loadBookmarks();
        setupBottomMenu("bookmark");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookmarks();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerBookmarks);

        adapter = new BookmarkAdapter();
        adapter.configure(
                bookmarkedCameraIds,
                bookmarkRepository,
                this::loadBookmarks
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Paso 1: cargar bookmarks (solo IDs)
     */
    private void loadBookmarks() {
        bookmarkRepository.getBookmarks(new BookmarkRepository.BookmarkCallback() {

            @Override
            public void onSuccess(List<Bookmark> bookmarks) {

                bookmarkedCameraIds.clear();

                for (Bookmark bookmark : bookmarks) {
                    bookmarkedCameraIds.add(bookmark.getCameraId());
                }

                observeCameras();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(
                        BookmarkActivity.this,
                        message,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    /**
     * Paso 2: observar cámaras y filtrar solo las bookmarkeadas
     */
    private void observeCameras() {

        cameraRepository.getCamerasLiveData()
                .observe(this, cameras -> {

                    if (cameras == null || cameras.isEmpty()) {
                        adapter.setItems(new ArrayList<>());
                        return;
                    }

                    List<Camera> bookmarkedCameras = new ArrayList<>();

                    for (Camera camera : cameras) {
                        try {
                            Long cameraId = Long.valueOf(camera.getId());
                            if (bookmarkedCameraIds.contains(cameraId)) {
                                bookmarkedCameras.add(camera);
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    adapter.setItems(bookmarkedCameras);
                });
    }

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