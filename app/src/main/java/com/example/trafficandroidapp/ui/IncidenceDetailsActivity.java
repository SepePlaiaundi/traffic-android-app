package com.example.trafficandroidapp.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.models.Incidence;

public class IncidenceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidence_details);

        Incidence incidence = (Incidence) getIntent().getSerializableExtra("incidence");
        if (incidence == null) { finish(); return; }

        setupViews(incidence);
    }

    private void setupViews(Incidence inc) {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView txtType = findViewById(R.id.txtType);
        TextView txtLevel = findViewById(R.id.txtLevel);
        TextView txtDescription = findViewById(R.id.txtDescription);
        TextView txtRoad = findViewById(R.id.txtRoad);
        TextView txtProvince = findViewById(R.id.txtProvince);
        TextView txtCause = findViewById(R.id.txtCause);
        TextView txtCity = findViewById(R.id.txtCity);
        ImageView imgIcon = findViewById(R.id.imgIncidenceIcon);

        txtType.setText(inc.tipo);
        txtLevel.setText("Nivel: " + (inc.nivel != null ? inc.nivel : "Desconocido"));
        txtDescription.setText(inc.descripcion);
        txtRoad.setText(inc.carretera);
        txtProvince.setText(inc.provincia);
        txtCause.setText(inc.causa);
        txtCity.setText(inc.ciudad);

        // Usamos el mismo método de selección de iconos que tienes en MapsActivity
        imgIcon.setImageResource(getIconResourceForType(inc.tipo));
    }

    private int getIconResourceForType(String tipo) {
        if (tipo == null) return R.drawable.ic_otras_incidencias;
        switch (tipo.trim()) {
            case "Seguridad vial": return R.drawable.ic_seguridad_vial;
            case "Obras": return R.drawable.ic_obras;
            case "Accidente": return R.drawable.ic_accidente;
            case "Meteorológica": return R.drawable.ic_meteorologica;
            case "Puertos de montaña": return R.drawable.ic_puertos_de_montania;
            case "Vialidad invernal tramos": return R.drawable.ic_vialidad_invernal_tramos;
            default: return R.drawable.ic_otras_incidencias;
        }
    }
}