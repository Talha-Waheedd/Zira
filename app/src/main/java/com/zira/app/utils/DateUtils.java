package com.zira.app.utils;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/** Date/time helpers for streaks, spaced repetition, and timestamp formatting. */
public final class DateUtils {

    public static final long ONE_DAY_MILLIS = TimeUnit.DAYS.toMillis(1);

    private DateUtils() {
    }

    /** @return epoch millis for the start (00:00:00.000) of today, device local time. */
    public static long startOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /** @return epoch millis for the end (23:59:59.999) of today, device local time. */
    public static long endOfToday() {
        return startOfToday() + ONE_DAY_MILLIS - 1;
    }

    /** @return epoch millis {@code days} days from the start of today (use negative for past). */
    public static long startOfTodayPlusDays(int days) {
        return startOfToday() + (long) days * ONE_DAY_MILLIS;
    }

    /** @return {@code true} if the two epoch-millis timestamps fall on the same calendar day. */
    public static boolean isSameDay(long millisA, long millisB) {
        Calendar a = Calendar.getInstance();
        a.setTimeInMillis(millisA);
        Calendar b = Calendar.getInstance();
        b.setTimeInMillis(millisB);
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    /** @return {@code true} if {@code millis} is on the calendar day immediately before today. */
    public static boolean isYesterday(long millis) {
        return isSameDay(millis, System.currentTimeMillis() - ONE_DAY_MILLIS);
    }

    /** Formats a timestamp as a short, locale-aware label, e.g. "14 Mar, 09:32". */
    @NonNull
    public static String formatTimestamp(long millis) {
        return new java.text.SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
                .format(new java.util.Date(millis));
    }

    /** Formats a timestamp as an ISO date (yyyy-MM-dd), suitable for the backend schedule API. */
    @NonNull
    public static String formatIsoDate(long millis) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(new java.util.Date(millis));
    }

    /** @return a time-of-day greeting prefix, e.g. "Good morning". */
    @NonNull
    public static String getTimeOfDayGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            return "Good morning";
        } else if (hour < 17) {
            return "Good afternoon";
        }
        return "Good evening";
    }
}
