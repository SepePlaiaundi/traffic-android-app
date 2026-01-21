package com.example.trafficandroidapp.repository;

import android.content.Context;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.trafficandroidapp.api.BookmarkApiService;
import com.example.trafficandroidapp.api.RetrofitClient;
import com.example.trafficandroidapp.db.AppDatabase;
import com.example.trafficandroidapp.db.dao.BookmarkDao;
import com.example.trafficandroidapp.models.AddBookmarkRequest;
import com.example.trafficandroidapp.models.Bookmark;
import com.example.trafficandroidapp.security.SessionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.internal.http2.Http2Reader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkRepository {

    private final BookmarkApiService api;
    private final BookmarkDao dao;
    private final SessionManager sessionManager;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public BookmarkRepository(Context context) {
        api = RetrofitClient.getInstance()
                .create(BookmarkApiService.class);

        dao = AppDatabase
                .getInstance(context)
                .bookmarkDao();

        sessionManager = new SessionManager(context);

        // Sincronizar al arrancar
        syncFromApi();
    }

    /* ============================
       LECTURA (SIEMPRE DESDE ROOM)
       ============================ */

    public LiveData<List<Bookmark>> observeBookmarks() {
        return dao.observeAll();
    }

    public LiveData<Integer> observeIsBookmarked(String cameraId) {
        return dao.observeIsBookmarked(cameraId);
    }


    /* ============================
       ESCRITURA (ROOM + API)
       ============================ */

    public void addBookmark(String cameraId) {

        executor.execute(() ->
                dao.insert(new Bookmark(cameraId))
        );

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

    public void removeBookmark(String cameraId,
                               Runnable onSuccess) {

        executor.execute(() -> {
            dao.deleteByCameraId(cameraId);
            if (onSuccess != null) onSuccess.run();
        });

        String token = sessionManager.getToken();
        if (token == null) return;

        api.removeBookmark(
                "Bearer " + token,
                cameraId
        ).enqueue(new Callback<>() {
            @Override public void onResponse(Call<Void> c, Response<Void> r) {}
            @Override public void onFailure(Call<Void> c, Throwable t) {}
        });
    }

    /* ============================
       SINCRONIZACIÓN API → ROOM
       ============================ */

    private void syncFromApi() {

        String token = sessionManager.getToken();
        if (token == null) return;

        api.getBookmarks("Bearer " + token)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(
                            Call<List<Bookmark>> call,
                            Response<List<Bookmark>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            executor.execute(() -> {
                                dao.deleteAll();
                                dao.insertAll(response.body());
                            });
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<Bookmark>> call,
                            Throwable t) {
                    }
                });
    }

    /* ============================
       CALLBACKS
       ============================ */

    public interface IsBookmarkedCallback {
        void onResult(boolean isBookmarked);
    }
}
