package com.example.trafficandroidapp.repository;

import android.content.Context;

import com.example.trafficandroidapp.api.BookmarkApiService;
import com.example.trafficandroidapp.api.RetrofitClient;
import com.example.trafficandroidapp.models.AddBookmarkRequest;
import com.example.trafficandroidapp.models.Bookmark;
import com.example.trafficandroidapp.security.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkRepository {

    private final BookmarkApiService api;
    private final SessionManager sessionManager;

    public BookmarkRepository(Context context) {
        api = RetrofitClient.getInstance().create(BookmarkApiService.class);
        sessionManager = new SessionManager(context);
    }

    public void getBookmarks(BookmarkCallback callback) {

        String token = sessionManager.getToken();

        if (token == null) {
            callback.onError("No autenticado");
            return;
        }

        api.getBookmarks("Bearer " + token)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<List<Bookmark>> call,
                                           Response<List<Bookmark>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Error al cargar bookmarks");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                        callback.onError("Error de conexión");
                    }
                });
    }

    public void addBookmark(long cameraId) {
        String token = sessionManager.getToken();
        if (token == null) return;

        api.addBookmark(
                "Bearer " + token,
                new AddBookmarkRequest(cameraId)
        ).enqueue(new Callback<>() {
            @Override public void onResponse(Call<Void> c, Response<Void> r) {}
            @Override public void onFailure(Call<Void> c, Throwable t) {}
        });
    }

    public void removeBookmark(long cameraId, Runnable onSuccess) {
        String token = sessionManager.getToken();
        if (token == null) return;

        api.removeBookmark(
                "Bearer " + token,
                cameraId
        ).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> c, Response<Void> r) {
                if (r.isSuccessful() && onSuccess != null) {
                    onSuccess.run();
                }
            }

            @Override
            public void onFailure(Call<Void> c, Throwable t) {
            }
        });
    }


    public void isBookmarked(long cameraId, IsBookmarkedCallback callback) {

        getBookmarks(new BookmarkCallback() {
            @Override
            public void onSuccess(List<Bookmark> bookmarks) {
                for (Bookmark b : bookmarks) {
                    if (b.getCameraId() == cameraId) {
                        callback.onResult(true);
                        return;
                    }
                }
                callback.onResult(false);
            }

            @Override
            public void onError(String message) {
                callback.onResult(false);
            }
        });
    }

    public interface IsBookmarkedCallback {
        void onResult(boolean isBookmarked);
    }


    public interface BookmarkCallback {
        void onSuccess(List<Bookmark> bookmarks);
        void onError(String message);
    }
}
