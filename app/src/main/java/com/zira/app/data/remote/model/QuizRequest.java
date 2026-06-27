package com.zira.app.data.remote.model;

import com.google.gson.annotations.SerializedName;

/** Request body for {@code POST /api/quiz}. */
public class QuizRequest {

    @SerializedName("subject")
    private final String subject;

    @SerializedName("difficulty")
    private final String difficulty;

    @SerializedName("count")
    private final int count;

    @SerializedName("userId")
    private final String userId;

    public QuizRequest(String subject, String difficulty, int count, String userId) {
        this.subject = subject;
        this.difficulty = difficulty;
        this.count = count;
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getCount() {
        return count;
    }

    public String getUserId() {
        return userId;
    }
}
