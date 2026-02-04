package com.example.trafficandroidapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.trafficandroidapp.db.dao.CameraDao;
import com.example.trafficandroidapp.db.dao.BookmarkDao;
import com.example.trafficandroidapp.db.dao.IncidenceDao; // IMPORTANTE
import com.example.trafficandroidapp.models.Camera;
import com.example.trafficandroidapp.models.Bookmark;
import com.example.trafficandroidapp.models.Incidence; // IMPORTANTE

@Database(
        entities = {
                Camera.class,
                Bookmark.class,
                Incidence.class // AÑADIDO
        },
        version = 12,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CameraDao cameraDao();
    public abstract BookmarkDao bookmarkDao();
    public abstract IncidenceDao incidenceDao(); // AÑADIDO

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "traffic_db"
                    )
                    .fallbackToDestructiveMigration() // Borrará DB si cambia versión
                    .build();
        }
        return instance;
    }
}