package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.repository.BookmarkRepository;
import com.google.android.material.button.MaterialButton;

public class CameraDetailsActivity extends AppCompatActivity {

    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_details);

        TextView txtName = findViewById(R.id.txtName);
        TextView txtRoad = findViewById(R.id.txtRoad);
        TextView txtKm = findViewById(R.id.txtKm);
        TextView txtLat = findViewById(R.id.txtLat);
        TextView txtLon = findViewById(R.id.txtLon);
        ImageView imgCamera = findViewById(R.id.imgCamera);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        Intent i = getIntent();

        txtName.setText(i.getStringExtra("name"));
        txtRoad.setText(i.getStringExtra("road"));
        txtKm.setText(i.getStringExtra("km"));
        txtLat.setText(i.getStringExtra("lat"));
        txtLon.setText(i.getStringExtra("lon"));

        String imageUrl = i.getStringExtra("image");
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(imgCamera);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        BookmarkRepository repo = new BookmarkRepository(this);
        long cameraId = Long.parseLong(i.getStringExtra("id"));

        repo.isBookmarked(cameraId, bookmarked -> {
            isBookmarked = bookmarked;
            updateButton(btnSave);
        });

        btnSave.setOnClickListener(v -> {
            if (isBookmarked) {
                repo.removeBookmark(cameraId, () -> {
                    isBookmarked = false;
                    updateButton(btnSave);
                });
            } else {
                repo.addBookmark(cameraId);
                isBookmarked = true;
                updateButton(btnSave);
            }
        });

    }

    private void updateButton(MaterialButton btnSave) {
        if (isBookmarked) {
            btnSave.setText("Quitar de favoritos");
        } else {
            btnSave.setText("Guardar la cámara");
        }
    }
}

