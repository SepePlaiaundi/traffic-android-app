package com.example.trafficandroidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trafficandroidapp.R;
import com.example.trafficandroidapp.repository.AuthRepository;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    private AuthRepository authRepository;

    // Referencias a las vistas definidas en tu XML
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilitar borde a borde (EdgeToEdge)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // 1. Inicializar repositorio
        authRepository = new AuthRepository(this);

        // 2. Vincular vistas con los IDs de tu nuevo XML
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword); // Nuevo campo
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        // 3. Configurar listeners
        btnRegister.setOnClickListener(v -> attemptRegister());

        txtLogin.setOnClickListener(v -> {
            // Volver al login
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            // Flags para limpiar el stack y que no se pueda volver atrás con el botón 'Back'
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void attemptRegister() {
        // Resetear errores previos
        etName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);

        // Obtener valores
        String nombre = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        boolean cancel = false;
        EditText focusView = null;

        // --- VALIDACIONES ---

        // Validar Contraseñas coincidentes
        if (!TextUtils.isEmpty(password) && !password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            focusView = etConfirmPassword;
            cancel = true;
        }

        // Validar Contraseña válida (ej: mínimo 4 caracteres)
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            cancel = true;
        } else if (password.length() < 4) {
            etPassword.setError("La contraseña es muy corta");
            focusView = etPassword;
            cancel = true;
        }

        // Validar Email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email no válido");
            focusView = etEmail;
            cancel = true;
        }

        // Validar Nombre
        if (TextUtils.isEmpty(nombre)) {
            etName.setError(getString(R.string.error_field_required));
            focusView = etName;
            cancel = true;
        }

        if (cancel) {
            // Hubo un error, enfocar el campo problemático
            focusView.requestFocus();
        } else {
            // --- TODO CORRECTO: ENVIAR AL SERVIDOR ---
            performRegister(nombre, email, password);
        }
    }

    private void performRegister(String nombre, String email, String password) {
        // Deshabilitar botón para evitar doble click
        btnRegister.setEnabled(false);
        btnRegister.setText("Registrando...");

        authRepository.register(nombre, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                // Restaurar botón (aunque cambiemos de actividad, es buena práctica)
                btnRegister.setEnabled(true);
                btnRegister.setText(R.string.register);

                Toast.makeText(RegisterActivity.this, "¡Registro exitoso! Por favor inicia sesión.", Toast.LENGTH_LONG).show();

                // Navegar al Login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(String message) {
                // Restaurar botón
                btnRegister.setEnabled(true);
                btnRegister.setText(R.string.register);

                // Mostrar error
                Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}