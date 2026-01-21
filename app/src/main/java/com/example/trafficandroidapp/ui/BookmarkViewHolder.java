package com.example.trafficandroidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.models.Camera;
import com.example.trafficandroidapp.repository.BookmarkRepository;

import java.util.Set;

public class BookmarkViewHolder extends RecyclerView.ViewHolder {

    private final TextView txtTitle;
    private final TextView txtSubtitle;
    private final ImageView imgPreview;
    private final ImageView imgBookmark;

    public BookmarkViewHolder(View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.txtTitle);
        txtSubtitle = itemView.findViewById(R.id.txtSubtitle);
        imgPreview = itemView.findViewById(R.id.imgPreview);
        imgBookmark = itemView.findViewById(R.id.imgBookmark);
    }

    public void bind(
            Camera camera,
            BookmarkRepository repository,
            Runnable onUnbookmarked
    ) {

        txtTitle.setText(camera.getName());
        txtSubtitle.setText(
                camera.getRoad() + " · " + camera.getKilometer()
        );

        Glide.with(itemView)
                .load(camera.getUrlImage())
                .placeholder(R.drawable.ic_camera_placeholder)
                .error(R.drawable.ic_camera_placeholder)
                .centerCrop()
                .into(imgPreview);

        // En esta pantalla SIEMPRE es bookmark
        imgBookmark.setImageResource(R.drawable.ic_bookmark_filled);

        itemView.setOnClickListener(v -> {
            Context context = itemView.getContext();
            Intent intent = new Intent(context, CameraDetailsActivity.class);

            intent.putExtra("id", camera.getId());
            intent.putExtra("name", camera.getName());
            intent.putExtra("road", camera.getDisplayRoad());
            intent.putExtra("km", camera.getKilometer());
            intent.putExtra("lat", camera.getLatitude());
            intent.putExtra("lon", camera.getLongitude());
            intent.putExtra("image", camera.getUrlImage());

            context.startActivity(intent);
        });

        imgBookmark.setOnClickListener(v -> {
            repository.removeBookmark(camera.getId(), onUnbookmarked);
        });
    }
}
