package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.trafficandroidapp.R;

public class CameraDetailsActivity extends AppCompatActivity {

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
    }
}
