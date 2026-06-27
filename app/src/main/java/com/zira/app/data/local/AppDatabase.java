package com.zira.app.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.zira.app.data.local.dao.ExplanationDao;
import com.zira.app.data.local.entity.ExplanationEntity;

/**
 * The Zira Room database.
 *
 * <p>Day 6–8 introduces {@link ExplanationEntity} for offline explanation history.
 * {@code FlashcardEntity} and {@code QuizResultEntity} are added (with a version bump) on
 * Days 9–11.
 */
@Database(
        entities = {ExplanationEntity.class},
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ExplanationDao explanationDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "zira-db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
