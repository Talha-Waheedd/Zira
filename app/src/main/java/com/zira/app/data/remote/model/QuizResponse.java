package com.zira.app.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Response body for {@code POST /api/quiz}. */
public class QuizResponse {

    @SerializedName("questions")
    private List<QuizQuestion> questions;

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    /** A single multiple-choice question returned by the backend. */
    public static class QuizQuestion {

        @SerializedName("question")
        private String question;

        @SerializedName("options")
        private List<String> options;

        @SerializedName("correctIndex")
        private int correctIndex;

        @SerializedName("explanation")
        private String explanation;

        public String getQuestion() {
            return question;
        }

        public List<String> getOptions() {
            return options;
        }

        public int getCorrectIndex() {
            return correctIndex;
        }

        public String getExplanation() {
            return explanation;
        }
    }
}
