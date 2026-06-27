package com.zira.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * A single flashcard stored locally for offline review and spaced repetition (SM-2).
 */
@Entity(tableName = "flashcards")
public class FlashcardEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String front;

    public String back;

    public String hint;

    public String subject;

    /** Last review rating: 1 = hard, 2 = medium, 3 = easy. */
    public int difficulty;

    /** Epoch millis when this card becomes due for review. */
    public long nextReviewDate;

    public long createdAt;
}
