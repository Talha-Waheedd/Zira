package com.zira.app.data.local.entity;

import androidx.room.ColumnInfo;

/**
 * Aggregated deck stats for the deck-list screen (one row per subject).
 */
public class DeckSummary {

    @ColumnInfo(name = "subject")
    public String subject;

    @ColumnInfo(name = "totalCount")
    public int totalCount;

    @ColumnInfo(name = "dueCount")
    public int dueCount;
}
