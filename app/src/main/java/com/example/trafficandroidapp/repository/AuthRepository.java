package com.example.trafficandroidapp.repository;

import android.content.Context;
import com.example.trafficandroidapp.api.AuthApiService;
import com.example.trafficandroidapp.api.RetrofitClient;
import com.example.trafficandroidapp.models.UserResponse;
import com.example.trafficandroidapp.models.auth.LoginRequest;
import com.example.trafficandroidapp.models.auth.LoginResponse;
import com.example.trafficandroidapp.models.auth.RegisterRequest; // Importar
import com.example.trafficandroidapp.security.SessionManager;
import com.example.trafficandroidapp.utils.HashUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApiService api;
    private final SessionManager sessionManager;

    public AuthRepository(Context context) {
        api = RetrofitClient.getInstance().create(AuthApiService.class);
        sessionManager = new SessionManager(context);
    }

    public void login(String email, String password, AuthCallback callback) {
        String hashedPassword = HashUtils.toSha256(password);
        LoginRequest request = new LoginRequest(email, hashedPassword);

        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    // Llamada encadenada para obtener el perfil inmediatamente
                    fetchProfileAndFinish(token, callback);
                } else {
                    callback.onError("Credenciales incorrectas");
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Error de conexión");
            }
        });
    }

    private void fetchProfileAndFinish(String token, AuthCallback callback) {
        api.getProfile("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // AHORA PASAMOS 4 PARÁMETROS: token, nombre, email y AVATAR
                    sessionManager.saveSession(
                            token,
                            response.body().getNombreCompleto(),
                            response.body().getEmail(),
                            response.body().getAvatar() // <-- Añade esto
                    );
                    callback.onSuccess();
                } else {
                    callback.onError("Error al obtener datos del usuario");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Error de perfil: " + t.getMessage());
            }
        });
    }

    // --- MÉTODO NUEVO DE REGISTRO ---
    public void register(String nombre, String email, String password, AuthCallback callback) {
        String hashedPassword = HashUtils.toSha256(password);

        RegisterRequest request = new RegisterRequest(nombre, email, hashedPassword);

        api.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    // Intentamos leer el mensaje de error del cuerpo (si existe)
                    String errorMsg = "Error en el registro";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public interface AuthCallback {
        void onSuccess();
        void onError(String message);
    }
}