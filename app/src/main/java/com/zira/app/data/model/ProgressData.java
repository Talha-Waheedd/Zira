package com.zira.app.data.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Aggregated analytics displayed on the Progress screen. */
public class ProgressData {

    /** Study minutes for each of the last 7 days (index 0 = oldest). */
    public float[] weeklyStudyMins = new float[7];

    /** Short day labels aligned with {@link #weeklyStudyMins}. */
    public String[] dayLabels = new String[7];

    /** Subject → mastery percentage (0–100) derived from quiz scores. */
    public Map<String, Float> subjectMastery = new LinkedHashMap<>();

    /** Whether the user studied on each of the last 28 days (index 0 = oldest). */
    public boolean[] streakDays = new boolean[28];

    /** Most frequently missed topics from quiz sessions. */
    public List<String> weakTopics = new ArrayList<>();

    public int currentStreak;
    public int totalXp;
}
