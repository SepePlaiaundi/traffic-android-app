package com.example.trafficandroidapp.ui.bookmark;

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
import com.example.trafficandroidapp.ui.BaseBottomMenuActivity;
import com.example.trafficandroidapp.ui.MapsActivity;
import com.example.trafficandroidapp.ui.ProfileActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookmarkActivity extends BaseBottomMenuActivity {

    private BookmarkRepository bookmarkRepository;
    private CameraRepository cameraRepository;
    private BookmarkAdapter adapter;
    private List<Camera> allCamerasCache = new ArrayList<>();

    // Cache local SOLO para filtrar
    private final Set<Integer> bookmarkedCameraIds = new HashSet<>();

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

    /*
     * ============================
     * OBSERVACIÓN REACTIVA
     * ============================
     */

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
    /*
     * ============================
     * RECYCLER
     * ============================
     */

    private void setupRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.recyclerBookmarks);

        adapter = new BookmarkAdapter();
        adapter.configure(bookmarkRepository, () -> {
        });
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
