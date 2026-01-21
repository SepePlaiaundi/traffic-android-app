package com.example.trafficandroidapp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.trafficandroidapp.models.Camera;

import java.util.List;

@Dao
public interface CameraDao {

    @Query("SELECT * FROM cameras")
    LiveData<List<Camera>> observeAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Camera> cameras);

    @Query("DELETE FROM cameras")
    void deleteAll();
}
