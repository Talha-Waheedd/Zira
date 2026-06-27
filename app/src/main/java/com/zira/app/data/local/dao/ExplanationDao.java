package com.zira.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.zira.app.data.local.entity.ExplanationEntity;

import java.util.List;

@Dao
public interface ExplanationDao {

    @Insert
    long insert(ExplanationEntity explanation);

    @Query("SELECT * FROM explanations WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    LiveData<List<ExplanationEntity>> getRecent(String userId, int limit);

    @Query("SELECT * FROM explanations WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<ExplanationEntity>> getAll(String userId);
}
