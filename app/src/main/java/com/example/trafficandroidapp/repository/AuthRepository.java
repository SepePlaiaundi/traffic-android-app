package com.example.trafficandroidapp.repository;

import android.content.Context;
import com.example.trafficandroidapp.api.AuthApiService;
import com.example.trafficandroidapp.api.RetrofitClient;
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
                    sessionManager.saveToken(response.body().getToken());
                    callback.onSuccess();
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