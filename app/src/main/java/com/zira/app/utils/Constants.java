package com.zira.app.utils;

public final class Constants {

    private Constants() {
    }

    /**
     * Base URL of the Zira FastAPI backend. MUST end with a trailing slash.
     * Replace with your deployed backend URL (e.g. "https://api.zira.app/").
     */
    public static final String BASE_URL = "https://your-backend-url.com/";

    public static final String PREFS_NAME = "zira_prefs";
    public static final String KEY_ONBOARDING_COMPLETE = "onboarding_complete";

    // Difficulty levels accepted by the backend
    public static final String DIFFICULTY_EASY = "easy";
    public static final String DIFFICULTY_MEDIUM = "medium";
    public static final String DIFFICULTY_HARD = "hard";

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

    // Explanation / session document fields
    public static final String FIELD_QUESTION = "question";
    public static final String FIELD_EXPLANATION = "explanation";
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_KEY_CONCEPTS = "keyConceptsList";
    public static final String FIELD_TIMESTAMP = "timestamp";

    public static final String SUBJECT_GENERAL = "General";

    public static final int RECENT_EXPLANATIONS_LIMIT = 10;

    // Flashcards
    public static final int FLASHCARD_DEFAULT_COUNT = 10;
    public static final int SM2_EASY_DAYS = 7;
    public static final int SM2_MEDIUM_DAYS = 3;
    public static final int SM2_HARD_DAYS = 1;

    public static final String EXTRA_REVIEW_SUBJECT = "extra_review_subject";

    /** Pakistani university subjects shown on the deck list. */
    public static final String[] DEFAULT_SUBJECTS = {
            "Mathematics",
            "Physics",
            "Computer Science",
            "Chemistry",
            "Biology",
            "Economics"
    };

    public static final long SPLASH_DELAY_MS = 2000L;

    public static final int DEFAULT_DAILY_GOAL_MINS = 30;
    public static final int MIN_DAILY_GOAL_MINS = 15;
    public static final int MAX_DAILY_GOAL_MINS = 120;

    // Quiz
    public static final int QUIZ_DEFAULT_COUNT = 5;

    public static final String EXTRA_QUIZ_SUBJECT = "extra_quiz_subject";
    public static final String EXTRA_QUIZ_SCORE = "extra_quiz_score";
    public static final String EXTRA_QUIZ_TOTAL = "extra_quiz_total";
    public static final String EXTRA_QUIZ_WRONG_TOPICS = "extra_quiz_wrong_topics";
    public static final String EXTRA_QUIZ_TIME_SECS = "extra_quiz_time_secs";

    public static final String FIELD_SCORE = "score";
    public static final String FIELD_TOTAL_QUESTIONS = "totalQuestions";
    public static final String FIELD_WRONG_TOPICS = "wrongTopics";
    public static final String FIELD_TIME_TAKEN_SECS = "timeTakenSecs";
    public static final String FIELD_ACTIVITY_TYPE = "activityType";

    public static final String ACTIVITY_QUIZ = "quiz";
    public static final String ACTIVITY_ASK = "ask";
    public static final String ACTIVITY_FLASHCARD = "flashcard";
}
