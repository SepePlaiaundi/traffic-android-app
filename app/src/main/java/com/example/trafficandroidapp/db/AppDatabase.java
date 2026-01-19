package com.example.trafficandroidapp.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.trafficandroidapp.models.Camera;

@Database(entities = {Camera.class}, version = 3) // <-- Subir a 2
public abstract class AppDatabase extends RoomDatabase {
    public abstract CameraDao cameraDao();
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "traffic_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}