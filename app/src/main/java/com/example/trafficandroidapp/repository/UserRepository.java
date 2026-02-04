package com.example.trafficandroidapp.repository;

import android.content.Context;
import android.net.Uri;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.example.trafficandroidapp.api.AuthApiService;
import com.example.trafficandroidapp.api.RetrofitClient;
import com.example.trafficandroidapp.models.UserProfileUpdateRequest;
import com.example.trafficandroidapp.models.UserResponse;
import com.example.trafficandroidapp.security.SessionManager;
import com.example.trafficandroidapp.utils.HashUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final AuthApiService api;
    private final SessionManager sessionManager;

    public UserRepository(Context context) {
        api = RetrofitClient.getInstance().create(AuthApiService.class);
        sessionManager = new SessionManager(context);
    }

    public void getUserProfile(ProfileCallback<UserResponse> callback) {
        String token = sessionManager.getToken();
        if (token == null) {
            callback.onError("No hay sesión activa");
            return;
        }

        api.getProfile("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error al obtener perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Error de conexión");
            }
        });
    }

    public void updateProfile(String nombre, String password, String avatar, ProfileCallback<Void> callback) {
        String token = sessionManager.getToken();

        // Si el usuario escribió una contraseña, la hasheamos; si no, enviamos null
        String hashedPassword = (password != null && !password.isEmpty())
                ? HashUtils.toSha256(password)
                : null;

        UserProfileUpdateRequest request = new UserProfileUpdateRequest(nombre, hashedPassword, avatar);

        api.updateProfile("Bearer " + token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error al actualizar perfil");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Error de red");
            }
        });
    }

    public void uploadImage(Uri imageUri, Context context, ProfileCallback<String> callback) {
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dubiknu7g");
        MediaManager.init(context, config);

        MediaManager.get().upload(imageUri)
                .unsigned("ml_default") // Configura esto en Cloudinary (Settings -> Upload)
                .callback(new com.cloudinary.android.callback.UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        callback.onSuccess(resultData.get("secure_url").toString());
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        callback.onError("Error Cloudinary: " + error.getDescription());
                    }
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    public interface ProfileCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}