package com.zira.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zira.app.data.local.entity.DeckSummary;
import com.zira.app.data.local.entity.FlashcardEntity;

import java.util.List;

@Dao
public interface FlashcardDao {

    @Insert
    void insertAll(List<FlashcardEntity> cards);

    @Query("SELECT * FROM flashcards WHERE subject = :subject ORDER BY createdAt DESC")
    LiveData<List<FlashcardEntity>> getBySubject(String subject);

    @Query("SELECT * FROM flashcards WHERE nextReviewDate <= :endOfToday ORDER BY nextReviewDate ASC")
    LiveData<List<FlashcardEntity>> getDueToday(long endOfToday);

    @Query("SELECT * FROM flashcards WHERE subject = :subject AND nextReviewDate <= :endOfToday "
            + "ORDER BY nextReviewDate ASC")
    LiveData<List<FlashcardEntity>> getDueBySubject(String subject, long endOfToday);

    @Query("SELECT subject AS subject, COUNT(*) AS totalCount, "
            + "SUM(CASE WHEN nextReviewDate <= :endOfToday THEN 1 ELSE 0 END) AS dueCount "
            + "FROM flashcards GROUP BY subject ORDER BY subject ASC")
    LiveData<List<DeckSummary>> getDeckSummaries(long endOfToday);

    @Query("SELECT COUNT(*) FROM flashcards WHERE nextReviewDate <= :endOfToday")
    LiveData<Integer> getTotalDueCount(long endOfToday);

    @Query("UPDATE flashcards SET nextReviewDate = :nextReviewDate, difficulty = :difficulty "
            + "WHERE id = :id")
    void updateReviewDate(int id, long nextReviewDate, int difficulty);

    @Update
    void update(FlashcardEntity card);
}
