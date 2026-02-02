package com.example.trafficandroidapp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.trafficandroidapp.models.Incidence;

import java.util.List;

@Dao
public interface IncidenceDao {

    @Query("SELECT * FROM incidences")
    LiveData<List<Incidence>> observeAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Incidence> incidences);

    @Query("DELETE FROM incidences")
    void deleteAll();
}