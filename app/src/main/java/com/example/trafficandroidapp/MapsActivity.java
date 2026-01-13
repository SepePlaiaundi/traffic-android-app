package com.example.trafficandroidapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.events.MapListener;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapsActivity extends AppCompatActivity {

    private MapView map;
    private Marker cameraMarker;
    View cameraInfo;
    View scrim;
    private static final double MIN_ZOOM_FOR_MARKER = 15.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_maps);

        map = findViewById(R.id.map);
        cameraInfo = findViewById(R.id.cameraInfo);
        scrim = findViewById(R.id.scrim);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(43.3128, -1.9750);
        map.getController().setZoom(17.0);
        map.getController().setCenter(startPoint);

        cameraMarker = new Marker(map);
        cameraMarker.setPosition(new GeoPoint(43.3132, -1.9741));
        cameraMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        cameraMarker.setIcon(getDrawable(R.drawable.ic_camera));
        cameraMarker.setInfoWindow(null);

        cameraMarker.setOnMarkerClickListener((marker, mapView) -> {
            scrim.setVisibility(View.VISIBLE);
            cameraInfo.setVisibility(View.VISIBLE);
            return true;
        });

        map.getOverlays().add(cameraMarker);

        map.addMapListener(new MapListener() {
            @Override
            public boolean onZoom(ZoomEvent event) {
                double zoom = event.getZoomLevel();
                cameraMarker.setEnabled(zoom >= MIN_ZOOM_FOR_MARKER);
                map.invalidate();
                return false;
            }

            @Override
            public boolean onScroll(org.osmdroid.events.ScrollEvent event) {
                return false;
            }
        });

        findViewById(R.id.btnClose).setOnClickListener(v -> {
                    scrim.setVisibility(View.GONE);
                    cameraInfo.setVisibility(View.GONE);
                }
        );
    }
}
