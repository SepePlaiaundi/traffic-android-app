package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.ui.bookmark.BookmarkActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomMenu("profile");
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