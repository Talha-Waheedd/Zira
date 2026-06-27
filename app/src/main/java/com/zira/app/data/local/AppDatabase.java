package com.zira.app.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.zira.app.data.local.dao.ExplanationDao;
import com.zira.app.data.local.dao.FlashcardDao;
import com.zira.app.data.local.entity.ExplanationEntity;
import com.zira.app.data.local.entity.FlashcardEntity;

@Database(
        entities = {ExplanationEntity.class, FlashcardEntity.class},
        version = 2,
        exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ExplanationDao explanationDao();

    public abstract FlashcardDao flashcardDao();

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
