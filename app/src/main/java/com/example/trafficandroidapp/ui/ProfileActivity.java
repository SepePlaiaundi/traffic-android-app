package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.models.UserResponse;
import com.example.trafficandroidapp.repository.UserRepository;
import com.example.trafficandroidapp.security.SessionManager;
import com.example.trafficandroidapp.ui.bookmark.BookmarkActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText etEmail, etFullName, etPassword;
    private android.widget.ImageView imgAvatar; // Faltaba declarar
    private Button btnSave, btnLogout;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private View loadingOverlay;
    private Uri selectedImageUri;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Usamos Glide para la previsualización local también
                    Glide.with(this).load(selectedImageUri).into(imgAvatar);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        userRepository = new UserRepository(this);
        sessionManager = new SessionManager(this);

        loadData();

        btnSave.setOnClickListener(v -> handleUpdate());
        btnLogout.setOnClickListener(v -> logout());
        imgAvatar.setOnClickListener(v -> openGallery());

        setupBottomMenu("profile");
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etFullName = findViewById(R.id.etFullName);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);
        imgAvatar = findViewById(R.id.imgAvatar); // Inicializar
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void setLoading(boolean isLoading) {
        loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!isLoading);
        btnLogout.setEnabled(!isLoading);
        etFullName.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
        imgAvatar.setClickable(!isLoading);
    }

    private void loadData() {
        etEmail.setText(sessionManager.getUserEmail());
        etFullName.setText(sessionManager.getUserName());

        String avatarUrl = sessionManager.getUserAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.icon) // Imagen por defecto mientras carga
                    .error(R.drawable.icon)       // Imagen si falla
                    .into(imgAvatar);
        }
    }

    private void logout() {
        sessionManager.clear();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, LoginActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // startActivity(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleUpdate() {
        String nombre = etFullName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nombre.isEmpty()) {
            etFullName.setError("El nombre es obligatorio");
            return;
        }

        setLoading(true); // Bloquear interfaz

        if (selectedImageUri != null) {
            userRepository.uploadImage(selectedImageUri, this, new UserRepository.ProfileCallback<String>() {
                @Override
                public void onSuccess(String urlCloudinary) {
                    sendUpdateToServer(nombre, password, urlCloudinary);
                }
                @Override
                public void onError(String message) {
                    setLoading(false); // Desbloquear si hay error
                    Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            sendUpdateToServer(nombre, password, sessionManager.getUserAvatar());
        }
    }

    private void sendUpdateToServer(String nombre, String password, String avatarUrl) {
        userRepository.updateProfile(nombre, password, avatarUrl, new UserRepository.ProfileCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                sessionManager.updateUserName(nombre);
                sessionManager.updateAvatar(avatarUrl);
                setLoading(false); // Desbloquear
                Toast.makeText(ProfileActivity.this, "¡Perfil actualizado!", Toast.LENGTH_SHORT).show();
                etPassword.setText("");
                selectedImageUri = null;
            }

            @Override
            public void onError(String message) {
                setLoading(false); // Desbloquear si hay error
                Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- MÉTODOS DE NAVEGACIÓN (Tus existentes) ---
    private void setupBottomMenu(String activeTab) {
        View btnExplore = findViewById(R.id.menuExplore);
        View btnBookmark = findViewById(R.id.menuBookmark);
        View btnProfile = findViewById(R.id.menuProfile);

        if (activeTab.equals("profile")) setMenuSelected(btnProfile, true);

        btnExplore.setOnClickListener(v -> {
            startActivity(new Intent(this, MapsActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnBookmark.setOnClickListener(v -> {
            startActivity(new Intent(this, BookmarkActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void setMenuSelected(View container, boolean selected) {
        container.setSelected(selected);
        if (container instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) container;
            for (int i = 0; i < vg.getChildCount(); i++) {
                vg.getChildAt(i).setSelected(selected);
            }
        }
    }
}