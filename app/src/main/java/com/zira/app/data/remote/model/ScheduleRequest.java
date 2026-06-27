package com.zira.app.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Request body for {@code POST /api/schedule}. */
public class ScheduleRequest {

    @SerializedName("exams")
    private final List<Exam> exams;

    @SerializedName("dailyMins")
    private final int dailyMins;

    @SerializedName("userId")
    private final String userId;

    public ScheduleRequest(List<Exam> exams, int dailyMins, String userId) {
        this.exams = exams;
        this.dailyMins = dailyMins;
        this.userId = userId;
    }

    public List<Exam> getExams() {
        return exams;
    }

    public int getDailyMins() {
        return dailyMins;
    }

    public String getUserId() {
        return userId;
    }

    /** A subject and its exam date. */
    public static class Exam {

        @SerializedName("subject")
        private final String subject;

        @SerializedName("date")
        private final String date;

        public Exam(String subject, String date) {
            this.subject = subject;
            this.date = date;
        }

        public String getSubject() {
            return subject;
        }

        public String getDate() {
            return date;
        }
    }
}
