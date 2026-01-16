package com.example.trafficandroidapp.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CameraDao {
    @Query("SELECT * FROM cameras")
    List<CameraEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CameraEntity> cameras);
}