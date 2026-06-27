package com.zira.app.data.remote.model;

import com.google.gson.annotations.SerializedName;

/** Request body for {@code POST /api/explain}. */
public class ExplanationRequest {

    @SerializedName("question")
    private final String question;

    @SerializedName("userId")
    private final String userId;

    @SerializedName("subject")
    private final String subject;

    public ExplanationRequest(String question, String userId, String subject) {
        this.question = question;
        this.userId = userId;
        this.subject = subject;
    }

    public String getQuestion() {
        return question;
    }

    public String getUserId() {
        return userId;
    }

    public String getSubject() {
        return subject;
    }
}
