package com.zira.app.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

/**
 * Local cache of a Zira explanation for offline viewing.
 *
 * <p>Mirrors the {@code users/{uid}/explanations} Firestore documents but lives on-device so the
 * Home screen and Ask history work without a network connection.
 */
@Entity(tableName = "explanations")
public class ExplanationEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String userId;

    public String question;

    public String explanation;

    public String subject;

    public List<String> keyConcepts;

    public List<String> followUpQuestions;

    public long timestamp;
}
