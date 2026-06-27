package com.zira.app.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Response body for {@code POST /api/flashcards}. */
public class FlashcardResponse {

    @SerializedName("cards")
    private List<Card> cards;

    public List<Card> getCards() {
        return cards;
    }

    /** A single generated flashcard (front/back/hint). */
    public static class Card {

        @SerializedName("front")
        private String front;

        @SerializedName("back")
        private String back;

        @SerializedName("hint")
        private String hint;

        public String getFront() {
            return front;
        }

        public String getBack() {
            return back;
        }

        public String getHint() {
            return hint;
        }
    }
}
