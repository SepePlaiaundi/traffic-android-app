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

import java.util.Locale;

public class CameraDetailsActivity extends AppCompatActivity {

    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_details);

        // Referencias UI
        TextView txtName = findViewById(R.id.txtName);
        TextView txtRoad = findViewById(R.id.txtRoad);
        TextView txtKm = findViewById(R.id.txtKm);
        TextView txtLat = findViewById(R.id.txtLat);
        TextView txtLon = findViewById(R.id.txtLon);
        ImageView imgCamera = findViewById(R.id.imgCamera);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        Intent i = getIntent();

        // 1. Obtener ID (ahora es int). Usamos -1 como valor por defecto si falla.
        int cameraId = i.getIntExtra("id", -1);

        // 2. Textos básicos
        txtName.setText(i.getStringExtra("name"));
        txtRoad.setText(i.getStringExtra("road"));
        txtKm.setText(i.getStringExtra("km"));

        // 3. Coordenadas (ahora vienen como double)
        // Usamos String.format para controlar los decimales (ej. 5 decimales)
        double lat = i.getDoubleExtra("lat", 0.0);
        double lon = i.getDoubleExtra("lon", 0.0);
        txtLat.setText(String.format(Locale.getDefault(), "Lat: %.5f", lat));
        txtLon.setText(String.format(Locale.getDefault(), "Lon: %.5f", lon));

        // 4. Cargar Imagen con Glide
        String imageUrl = i.getStringExtra("image");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_camera) // Opcional: icono mientras carga
                    .error(R.drawable.ic_otras_incidencias)    // Opcional: si falla la carga
                    .into(imgCamera);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        /* ============================
           LÓGICA DE FAVORITOS (REPOSITORIO)
           ============================ */
        BookmarkRepository repo = new BookmarkRepository(this);

        if (cameraId != -1) {
            // Observar si está en favoritos
            repo.observeIsBookmarked(cameraId).observe(this, count -> {
                isBookmarked = count != null && count > 0;
                updateButton(btnSave);
            });

            // Acción de guardar/quitar
            btnSave.setOnClickListener(v -> {
                if (isBookmarked) {
                    // repo.removeBookmark ahora recibe int
                    repo.removeBookmark(cameraId, null);
                } else {
                    repo.addBookmark(cameraId);
                }
            });
        }
    }

    private void updateButton(MaterialButton btnSave) {
        btnSave.setText(isBookmarked ? "Quitar de favoritos" : "Guardar la cámara");
        // Opcional: cambiar icono del botón
        btnSave.setIconResource(isBookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark);
    }
}