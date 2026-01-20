package com.example.trafficandroidapp.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.models.Camera;

public class BookmarkViewHolder extends RecyclerView.ViewHolder {

    private final TextView txtName;
    private final TextView txtRoad;
    private final TextView txtKm;
    private final ImageView imgCamera;

    public BookmarkViewHolder(View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtCameraName);
        txtRoad = itemView.findViewById(R.id.txtRoad);
        txtKm = itemView.findViewById(R.id.txtKm);
        imgCamera = itemView.findViewById(R.id.imgCamera);
    }

    public void bind(Camera camera) {
        txtName.setText(camera.getName());
        txtRoad.setText(camera.getRoad());
        txtKm.setText(camera.getKilometer());

        // Imagen: más adelante (Glide / Coil)
        // imgCamera.setImage...
    }
}
