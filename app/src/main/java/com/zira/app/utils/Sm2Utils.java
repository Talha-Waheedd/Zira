package com.zira.app.utils;

/**
 * Simplified SM-2 spaced-repetition intervals used when the user rates a flashcard.
 *
 * <p>Easy = +7 days, Medium = +3 days, Hard = tomorrow (next calendar day).
 */
public final class Sm2Utils {

    public static final int RATING_HARD = 1;
    public static final int RATING_MEDIUM = 2;
    public static final int RATING_EASY = 3;

    private Sm2Utils() {
    }

    /**
     * @param rating one of {@link #RATING_HARD}, {@link #RATING_MEDIUM}, {@link #RATING_EASY}
     * @return epoch millis for the next review date
     */
    public static long nextReviewDateForRating(int rating) {
        switch (rating) {
            case RATING_EASY:
                return DateUtils.startOfTodayPlusDays(Constants.SM2_EASY_DAYS);
            case RATING_MEDIUM:
                return DateUtils.startOfTodayPlusDays(Constants.SM2_MEDIUM_DAYS);
            case RATING_HARD:
            default:
                return DateUtils.startOfTodayPlusDays(Constants.SM2_HARD_DAYS);
        }
    }
}
