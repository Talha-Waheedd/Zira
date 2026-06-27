package com.zira.app.utils;

public final class Constants {

    private Constants() {
    }

    public static final String PREFS_NAME = "zira_prefs";
    public static final String KEY_ONBOARDING_COMPLETE = "onboarding_complete";

    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_EXPLANATIONS = "explanations";
    public static final String COLLECTION_QUIZ_SESSIONS = "quiz_sessions";
    public static final String COLLECTION_STUDY_SESSIONS = "study_sessions";

    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_SUBJECTS = "subjects";
    public static final String FIELD_DAILY_GOAL_MINS = "dailyGoalMins";
    public static final String FIELD_STREAK_COUNT = "streakCount";
    public static final String FIELD_LAST_STUDY_DATE = "lastStudyDate";
    public static final String FIELD_TOTAL_XP = "totalXP";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_ONBOARDING_COMPLETE = "onboardingComplete";

    public static final long SPLASH_DELAY_MS = 2000L;

    public static final int DEFAULT_DAILY_GOAL_MINS = 30;
    public static final int MIN_DAILY_GOAL_MINS = 15;
    public static final int MAX_DAILY_GOAL_MINS = 120;
}
