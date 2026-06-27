package com.zira.app.data.remote.model;

import com.google.gson.annotations.SerializedName;

/** Request body for {@code POST /api/flashcards}. */
public class FlashcardRequest {

    @SerializedName("topic")
    private final String topic;

    @SerializedName("count")
    private final int count;

    @SerializedName("userId")
    private final String userId;

    public FlashcardRequest(String topic, int count, String userId) {
        this.topic = topic;
        this.count = count;
        this.userId = userId;
    }

    public String getTopic() {
        return topic;
    }

    public int getCount() {
        return count;
    }

    public String getUserId() {
        return userId;
    }
}
