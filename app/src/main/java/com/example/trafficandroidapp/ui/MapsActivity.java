package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.models.Camera;
import com.example.trafficandroidapp.models.Incidence;
import com.example.trafficandroidapp.repository.BookmarkRepository;
import com.example.trafficandroidapp.repository.CameraRepository;
import com.example.trafficandroidapp.repository.IncidenceRepository;
import com.example.trafficandroidapp.ui.bookmark.BookmarkActivity;
import com.google.android.material.chip.ChipGroup;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends BaseBottomMenuActivity {

    private MapView map;
    private View cameraPanel, incidencePanel, scrim;

    private TextView txtCameraName, txtCameraRoad, txtCameraKm;
    private ImageButton btnBookmark;
    private TextView txtIncidenceType, txtIncidenceRoad, txtIncidenceCause;

    private Camera selectedCamera;
    private Incidence selectedIncidence;

    private final List<Marker> allCameraMarkers = new ArrayList<>();
    private final List<Marker> allIncidenceMarkers = new ArrayList<>();

    private CameraRepository cameraRepository;
    private IncidenceRepository incidenceRepository;
    private BookmarkRepository bookmarkRepository;

    private static final double MIN_ZOOM_FOR_MARKERS = 10.0;
    private boolean isBookmarked = false;
    private boolean showCameras = true;
    private boolean showIncidences = true;

    private View addIncidencePanel; // El include en el XML
    private AutoCompleteTextView comboTipo;
    private EditText etDescripcion;
    private GeoPoint pendingPoint;
    private Marker tempMarker;
    private AutoCompleteTextView comboNivel;
    private EditText etCausa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_maps);

        cameraRepository = new CameraRepository(this);
        incidenceRepository = new IncidenceRepository(this);
        bookmarkRepository = new BookmarkRepository(this);

        setupViews();
        initMap();
        observeData();
        setupBottomMenu("explore");
    }

    private void setupViews() {
        map = findViewById(R.id.map);
        scrim = findViewById(R.id.scrim);
        cameraPanel = findViewById(R.id.cameraInfo);
        incidencePanel = findViewById(R.id.incidenceInfo);

        txtCameraName = findViewById(R.id.txtCameraName);
        txtCameraRoad = findViewById(R.id.txtCameraRoad);
        txtCameraKm = findViewById(R.id.txtCameraKm);
        btnBookmark = findViewById(R.id.btnBookmark);

        txtIncidenceType = findViewById(R.id.txtIncidenceType);
        txtIncidenceRoad = findViewById(R.id.txtIncidenceRoad);
        txtIncidenceCause = findViewById(R.id.txtIncidenceCause);
        comboNivel = findViewById(R.id.comboNivel);
        etCausa = findViewById(R.id.etCausa);

        String[] niveles = { "Amarillo", "Blanco" };
        ArrayAdapter<String> nivelAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, niveles);
        comboNivel.setAdapter(nivelAdapter);


        findViewById(R.id.btnDetails).setOnClickListener(v -> openDetails());
        findViewById(R.id.btnClose).setOnClickListener(v -> closePanels());
        findViewById(R.id.btnIncidenceDetails).setOnClickListener(v -> openDetails());
        findViewById(R.id.btnCloseIncidence).setOnClickListener(v -> closePanels());
        scrim.setOnClickListener(v -> closePanels());

        ChipGroup filterGroup = findViewById(R.id.chipGroupFilters);
        filterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            showCameras = (checkedId == R.id.chipAll || checkedId == R.id.chipCameras);
            showIncidences = (checkedId == R.id.chipAll || checkedId == R.id.chipIncidences);
            applyFilters();
            closePanels();
        });

        // 1. En setupViews (asegúrate de añadir el include en tu activity_maps.xml)
        addIncidencePanel = findViewById(R.id.addIncidenceInfo); // ID del include
        comboTipo = findViewById(R.id.comboTipo);
        etDescripcion = findViewById(R.id.etDescripcion);

        // Configurar el combo de tipos
        String[] tipos = { "Accidente", "Obras", "Meteorológica", "Seguridad vial", "Otros" };
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, tipos);
        comboTipo.setAdapter(adapter);

        findViewById(R.id.btnCancelAdd).setOnClickListener(v -> cancelAddIncidence());
        findViewById(R.id.btnConfirmAdd).setOnClickListener(v -> confirmAddIncidence());

        // 2. En initMap añadir el Overlay de eventos
        org.osmdroid.views.overlay.MapEventsOverlay eventsOverlay = new org.osmdroid.views.overlay.MapEventsOverlay(
                new org.osmdroid.events.MapEventsReceiver() {
                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p) {
                        closePanels(); // Si toca en otro lado, cerramos todo
                        return true;
                    }

                    @Override
                    public boolean longPressHelper(GeoPoint p) {
                        showAddIncidenceFlow(p);
                        return true;
                    }
                });
        map.getOverlays().add(0, eventsOverlay); // Añadir al fondo
    }

    private void initMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(11.0);
        map.getController().setCenter(new GeoPoint(43.2627, -2.9252));

        map.addMapListener(new MapListener() {
            @Override
            public boolean onZoom(ZoomEvent event) {
                applyFilters();
                return false;
            }

            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }
        });
    }

    private void observeData() {
        cameraRepository.getCamerasLiveData().observe(this, cameras -> {
            map.getOverlays().removeAll(allCameraMarkers);
            allCameraMarkers.clear();
            if (cameras != null) {
                for (Camera cam : cameras) {
                    GeoPoint gp = cam.getGeoPoint();
                    if (gp == null)
                        continue;
                    // Tamaño 30dp para cámaras (un poco más discreto)
                    Marker m = createMarker(gp, R.drawable.ic_camera, 30);
                    m.setRelatedObject(cam);
                    m.setOnMarkerClickListener((marker, v) -> {
                        showCameraInfo((Camera) marker.getRelatedObject(), marker.getPosition());
                        return true;
                    });
                    allCameraMarkers.add(m);
                    map.getOverlays().add(m);
                }
            }
            applyFilters();
        });

        incidenceRepository.getIncidencesLiveData().observe(this, incidences -> {
            map.getOverlays().removeAll(allIncidenceMarkers);
            allIncidenceMarkers.clear();
            if (incidences != null) {
                for (Incidence inc : incidences) {
                    GeoPoint gp = inc.getGeoPoint();
                    if (gp == null)
                        continue;
                    // Tamaño 38dp para incidencias (más prioritario visualmente)
                    Marker m = createMarker(gp, getIconResourceForType(inc.tipo), 38);
                    m.setRelatedObject(inc);
                    m.setOnMarkerClickListener((marker, v) -> {
                        showIncidenceInfo((Incidence) marker.getRelatedObject(), marker.getPosition());
                        return true;
                    });
                    allIncidenceMarkers.add(m);
                    map.getOverlays().add(m);
                }
            }
            applyFilters();
        });
    }

    /**
     * Crea un marcador escalado dinámicamente según la densidad de la pantalla (DP
     * a PX).
     */
    private Marker createMarker(GeoPoint point, int iconRes, int sizeDp) {
        Marker m = new Marker(map);
        m.setPosition(point);
        // Anchor en el centro inferior para que la punta del icono señale la coordenada
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        Drawable drawable = ContextCompat.getDrawable(this, iconRes);
        if (drawable != null) {
            // Conversión dinámica de DP a Píxeles reales del dispositivo
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int sizePx = (int) (sizeDp * metrics.density);

            // Mantener relación de aspecto original del icono
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            float ratio = (float) width / height;

            int finalWidth, finalHeight;
            if (width > height) {
                finalWidth = sizePx;
                finalHeight = (int) (sizePx / ratio);
            } else {
                finalHeight = sizePx;
                finalWidth = (int) (sizePx * ratio);
            }

            Bitmap bitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, finalWidth, finalHeight);
            drawable.draw(canvas);

            m.setIcon(new BitmapDrawable(getResources(), bitmap));
        }
        return m;
    }

    private void applyFilters() {
        double zoom = map.getZoomLevelDouble();
        boolean zoomOk = zoom >= MIN_ZOOM_FOR_MARKERS;

        for (Marker m : allCameraMarkers) {
            m.setVisible(showCameras);
            m.setEnabled(showCameras && zoomOk);
        }
        for (Marker m : allIncidenceMarkers) {
            m.setVisible(showIncidences);
            m.setEnabled(showIncidences && zoomOk);
        }
        map.invalidate();
    }

    private void showCameraInfo(Camera cam, GeoPoint position) {
        if (cam == null)
            return;
        selectedCamera = cam;
        selectedIncidence = null;

        txtCameraName.setText(cam.name != null ? cam.name : "-");
        txtCameraRoad.setText(cam.getDisplayRoad());
        txtCameraKm.setText(cam.kilometer != null ? getString(R.string.km_format, cam.kilometer) : "-");

        bookmarkRepository.observeIsBookmarked(cam.id).observe(this, count -> {
            isBookmarked = count != null && count > 0;
            updateBookmarkIcon();
        });

        btnBookmark.setOnClickListener(v -> {
            if (isBookmarked)
                bookmarkRepository.removeBookmark(cam.id, null);
            else
                bookmarkRepository.addBookmark(cam.id);
        });

        togglePanels(true);
        map.getController().animateTo(position);
    }

    private void showIncidenceInfo(Incidence inc, GeoPoint position) {
        if (inc == null)
            return;
        selectedIncidence = inc;
        selectedCamera = null;

        txtIncidenceType.setText(inc.tipo);
        txtIncidenceRoad.setText(inc.carretera + " (" + inc.provincia + ")");
        txtIncidenceCause.setText(inc.causa != null ? inc.causa : inc.descripcion);

        togglePanels(false);
        map.getController().animateTo(position);
    }

    private void togglePanels(boolean isCamera) {
        scrim.setVisibility(View.VISIBLE);
        cameraPanel.setVisibility(isCamera ? View.VISIBLE : View.GONE);
        incidencePanel.setVisibility(isCamera ? View.GONE : View.VISIBLE);
    }

    private void closePanels() {
        scrim.setVisibility(View.GONE);
        cameraPanel.setVisibility(View.GONE);
        incidencePanel.setVisibility(View.GONE);
        selectedCamera = null;
        selectedIncidence = null;
    }

    private void updateBookmarkIcon() {
        btnBookmark.setImageResource(isBookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark);
    }

    private void openDetails() {
        if (selectedCamera != null) {
            Intent intent = new Intent(this, CameraDetailsActivity.class);
            intent.putExtra("id", selectedCamera.id);
            intent.putExtra("name", selectedCamera.name);
            intent.putExtra("road", selectedCamera.getDisplayRoad());
            intent.putExtra("km", selectedCamera.kilometer);
            intent.putExtra("lat", selectedCamera.latitude);
            intent.putExtra("lon", selectedCamera.longitude);
            intent.putExtra("image", selectedCamera.urlImage);
            startActivity(intent);
        } else if (selectedIncidence != null) {
            Intent intent = new Intent(this, IncidenceDetailsActivity.class);
            intent.putExtra("incidence", selectedIncidence);
            startActivity(intent);
        }
    }

    private int getIconResourceForType(String tipo) {
        if (tipo == null)
            return R.drawable.ic_otras_incidencias;
        switch (tipo.trim()) {
            case "Seguridad vial":
                return R.drawable.ic_seguridad_vial;
            case "Obras":
                return R.drawable.ic_obras;
            case "Accidente":
                return R.drawable.ic_accidente;
            case "Meteorológica":
                return R.drawable.ic_meteorologica;
            case "Puertos de montaña":
                return R.drawable.ic_puertos_de_montania;
            case "Vialidad invernal tramos":
                return R.drawable.ic_vialidad_invernal_tramos;
            default:
                return R.drawable.ic_otras_incidencias;
        }
    }

    private void showAddIncidenceFlow(GeoPoint p) {
        closePanels(); // Cerramos otros paneles abiertos
        pendingPoint = p;

        // 1. Poner marcador visual temporal
        if (tempMarker != null)
            map.getOverlays().remove(tempMarker);
        tempMarker = createMarker(p, R.drawable.ic_otras_incidencias, 45);
        tempMarker.setAlpha(0.7f); // Un poco transparente para indicar "borrador"
        map.getOverlays().add(tempMarker);

        // 2. Mostrar panel y centrar cámara
        scrim.setVisibility(View.VISIBLE);
        addIncidencePanel.setVisibility(View.VISIBLE);
        map.getController().animateTo(p);
    }

    private void confirmAddIncidence() {
        String tipo = comboTipo.getText().toString();
        String nivel = comboNivel.getText().toString();
        String causa = etCausa.getText().toString();
        String desc = etDescripcion.getText().toString();

        if (tipo.isEmpty() || nivel.isEmpty()) {
            Toast.makeText(this, R.string.select_type_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Incidence inc = new Incidence();
        inc.setLatitud(pendingPoint.getLatitude());
        inc.setLongitud(pendingPoint.getLongitude());
        inc.setTipo(tipo);
        inc.setNivel(nivel);
        inc.setCausa(causa);
        inc.setDescripcion(desc);
        inc.setCarretera(getString(R.string.manual_location));
        inc.setProvincia(getString(R.string.user_province));

        incidenceRepository.addIncidence(inc);

        Toast.makeText(this, R.string.reporting_incidence, Toast.LENGTH_SHORT).show();
        cancelAddIncidence();
    }


    private void cancelAddIncidence() {
        if (tempMarker != null)
            map.getOverlays().remove(tempMarker);
        tempMarker = null;
        pendingPoint = null;
        etDescripcion.setText("");
        comboTipo.setText("");
        addIncidencePanel.setVisibility(View.GONE);
        scrim.setVisibility(View.GONE);
        map.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}