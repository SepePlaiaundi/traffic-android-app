package com.example.trafficandroidapp.repository;

import android.content.Context;

import com.example.trafficandroidapp.api.AuthApiService;
import com.example.trafficandroidapp.api.RetrofitClient;
import com.example.trafficandroidapp.models.auth.LoginRequest;
import com.example.trafficandroidapp.models.auth.LoginResponse;
import com.example.trafficandroidapp.security.SessionManager;

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

        LoginRequest request = new LoginRequest(email, password);

        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response) {

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

    public interface AuthCallback {
        void onSuccess();
        void onError(String message);
    }
}
