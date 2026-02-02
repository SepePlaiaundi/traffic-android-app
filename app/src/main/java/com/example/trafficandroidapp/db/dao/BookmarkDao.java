package com.example.trafficandroidapp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.trafficandroidapp.models.Bookmark;

import java.util.List;

@Dao
public interface BookmarkDao {

    @Query("SELECT * FROM bookmarks")
    LiveData<List<Bookmark>> observeAll();

    // 🔥 CLAVE: observar si existe un bookmark concreto
    @Query("SELECT COUNT(*) FROM bookmarks WHERE cameraId = :cameraId")
    LiveData<Integer> observeIsBookmarked(int cameraId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Bookmark bookmark);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Bookmark> bookmarks);

    @Query("DELETE FROM bookmarks WHERE cameraId = :cameraId")
    void deleteByCameraId(int cameraId);

    @Query("DELETE FROM bookmarks")
    void deleteAll();
}
