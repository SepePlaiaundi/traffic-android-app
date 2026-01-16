package com.example.trafficandroidapp.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CameraEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CameraDao cameraDao();
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "traffic_db").build();
        }
        return instance;
    }
}