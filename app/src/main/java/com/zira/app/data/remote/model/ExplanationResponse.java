package com.zira.app.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Response body for {@code POST /api/explain}. */
public class ExplanationResponse {

    @SerializedName("explanation")
    private String explanation;

    @SerializedName("followUpQuestions")
    private List<String> followUpQuestions;

    @SerializedName("keyConcepts")
    private List<String> keyConcepts;

    public String getExplanation() {
        return explanation;
    }

    public List<String> getFollowUpQuestions() {
        return followUpQuestions;
    }

    public List<String> getKeyConcepts() {
        return keyConcepts;
    }
}
