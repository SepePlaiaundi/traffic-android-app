package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
    private List<Camera> allCamerasCache = new ArrayList<>();


    // Cache local SOLO para filtrar
    private final Set<String> bookmarkedCameraIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        bookmarkRepository = new BookmarkRepository(this);
        cameraRepository = new CameraRepository(this);

        setupRecyclerView();
        observeData();
        setupBottomMenu("bookmark");
    }

    /* ============================
       OBSERVACIÓN REACTIVA
       ============================ */


    private void observeData() {
        cameraRepository.getCamerasLiveData().observe(this, cameras -> {
            if (cameras != null) {
                this.allCamerasCache = cameras;
                refreshList();
            }
        });

        bookmarkRepository.observeBookmarks().observe(this, bookmarks -> {
            bookmarkedCameraIds.clear();
            if (bookmarks != null) {
                for (Bookmark b : bookmarks) {
                    bookmarkedCameraIds.add(b.getCameraId());
                }
            }

            refreshList();
        });
    }

    private void refreshList() {
        List<Camera> filtered = new ArrayList<>();
        for (Camera c : allCamerasCache) {
            if (bookmarkedCameraIds.contains(c.getId())) {
                filtered.add(c);
            }
        }
        adapter.setItems(filtered);
    }
    /* ============================
       RECYCLER
       ============================ */

    private void setupRecyclerView() {

        RecyclerView recyclerView =
                findViewById(R.id.recyclerBookmarks);

        adapter = new BookmarkAdapter();
        adapter.configure(bookmarkRepository, () -> {});
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /* ============================
       BOTTOM MENU
       ============================ */

    private void setupBottomMenu(String activeTab) {

        View btnExplore = findViewById(R.id.menuExplore);
        View btnBookmark = findViewById(R.id.menuBookmark);
        View btnProfile = findViewById(R.id.menuProfile);

        if ("bookmark".equals(activeTab)) {
            setMenuSelected(btnBookmark, true);
        }

        btnExplore.setOnClickListener(v -> {
            startActivity(new Intent(this, MapsActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void setMenuSelected(View container, boolean selected) {
        container.setSelected(selected);
        if (container instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg =
                    (android.view.ViewGroup) container;
            for (int i = 0; i < vg.getChildCount(); i++) {
                vg.getChildAt(i).setSelected(selected);
            }
        }
    }
}
