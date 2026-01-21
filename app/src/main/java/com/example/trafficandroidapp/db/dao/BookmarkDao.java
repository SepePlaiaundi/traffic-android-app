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

    @Query("SELECT * FROM bookmarks")
    List<Bookmark> getAllSync();

    @Query("SELECT COUNT(*) FROM bookmarks WHERE cameraId = :cameraId")
    int isBookmarked(long cameraId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Bookmark> bookmarks);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Bookmark bookmark);

    @Query("DELETE FROM bookmarks WHERE cameraId = :cameraId")
    void deleteByCameraId(long cameraId);

    @Query("DELETE FROM bookmarks")
    void deleteAll();
}
