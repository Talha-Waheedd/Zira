package com.zira.app.data.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zira.app.data.local.AppDatabase;
import com.zira.app.data.local.entity.DeckSummary;
import com.zira.app.data.local.entity.ExplanationEntity;
import com.zira.app.data.local.entity.FlashcardEntity;
import com.zira.app.data.model.UserProfile;
import com.zira.app.data.remote.ApiService;
import com.zira.app.data.remote.RetrofitClient;
import com.zira.app.data.remote.model.ExplanationRequest;
import com.zira.app.data.remote.model.ExplanationResponse;
import com.zira.app.data.remote.model.FlashcardRequest;
import com.zira.app.data.remote.model.FlashcardResponse;
import com.zira.app.data.remote.model.QuizRequest;
import com.zira.app.data.remote.model.QuizResponse;
import com.zira.app.utils.Constants;
import com.zira.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The single source of truth for Zira's data.
 *
 * <p>The repository decides where data comes from (remote Retrofit API vs. local Room/Firestore)
 * and is the only layer that talks to those data sources. ViewModels call into the repository and
 * never touch Retrofit, Room, or Firebase directly.
 */
public class ZiraRepository {

    private final ApiService apiService;
    private final AppDatabase database;
    private final FirebaseFirestore firestore;
    private final ExecutorService ioExecutor;

    public ZiraRepository(Application application) {
        this.apiService = RetrofitClient.getApiService();
        this.database = AppDatabase.getInstance(application);
        this.firestore = FirebaseFirestore.getInstance();
        this.ioExecutor = Executors.newSingleThreadExecutor();
    }

    // ---------------------------------------------------------------------------------------------
    // Ask Zira
    // ---------------------------------------------------------------------------------------------

    /**
     * Requests a step-by-step explanation from the backend. On success the explanation is cached
     * locally (Room) and persisted to Firestore before the caller's callback is invoked, so the
     * data is available offline and across devices.
     */
    public void getExplanation(String question,
                               String subject,
                               String userId,
                               @NonNull Callback<ExplanationResponse> callback) {
        ExplanationRequest request = new ExplanationRequest(question, userId, subject);

        apiService.getExplanation(request).enqueue(new Callback<ExplanationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExplanationResponse> call,
                                   @NonNull Response<ExplanationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    persistExplanation(question, subject, userId, response.body());
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<ExplanationResponse> call, @NonNull Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /** @return recent explanations for the user, observed live from Room (offline-capable). */
    public LiveData<List<ExplanationEntity>> getRecentExplanations(String userId) {
        return database.explanationDao().getRecent(userId, Constants.RECENT_EXPLANATIONS_LIMIT);
    }

    private void persistExplanation(String question,
                                    String subject,
                                    String userId,
                                    ExplanationResponse response) {
        long now = System.currentTimeMillis();

        ExplanationEntity entity = new ExplanationEntity();
        entity.userId = userId;
        entity.question = question;
        entity.subject = subject;
        entity.explanation = response.getExplanation();
        entity.keyConcepts = response.getKeyConcepts() != null
                ? response.getKeyConcepts() : new ArrayList<>();
        entity.followUpQuestions = response.getFollowUpQuestions() != null
                ? response.getFollowUpQuestions() : new ArrayList<>();
        entity.timestamp = now;

        ioExecutor.execute(() -> database.explanationDao().insert(entity));

        saveExplanationToFirestore(userId, entity);
    }

    private void saveExplanationToFirestore(String userId, ExplanationEntity entity) {
        if (userId == null) {
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put(Constants.FIELD_QUESTION, entity.question);
        data.put(Constants.FIELD_EXPLANATION, entity.explanation);
        data.put(Constants.FIELD_SUBJECT, entity.subject);
        data.put(Constants.FIELD_KEY_CONCEPTS, entity.keyConcepts);
        data.put(Constants.FIELD_TIMESTAMP, FieldValue.serverTimestamp());

        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .collection(Constants.COLLECTION_EXPLANATIONS)
                .add(data);
    }

    // ---------------------------------------------------------------------------------------------
    // Flashcards
    // ---------------------------------------------------------------------------------------------

    /** @return deck summaries grouped by subject, with due counts for today. */
    public LiveData<List<DeckSummary>> getDeckSummaries() {
        return database.flashcardDao().getDeckSummaries(DateUtils.endOfToday());
    }

    /** @return total number of cards due for review today (all subjects). */
    public LiveData<Integer> getTotalDueCount() {
        return database.flashcardDao().getTotalDueCount(DateUtils.endOfToday());
    }

    /** @return cards due for review for a specific subject. */
    public LiveData<List<FlashcardEntity>> getDueCardsBySubject(String subject) {
        return database.flashcardDao().getDueBySubject(subject, DateUtils.endOfToday());
    }

    /** @return all cards due for review today across every subject. */
    public LiveData<List<FlashcardEntity>> getAllDueCards() {
        return database.flashcardDao().getDueToday(DateUtils.endOfToday());
    }

    /**
     * Generates a new flashcard deck from the backend and saves the cards to Room.
     *
     * @param topic   topic sent to the API (usually the subject name)
     * @param subject subject label stored on each card
     */
    public void generateFlashcards(String topic,
                                   String subject,
                                   int count,
                                   String userId,
                                   @NonNull Callback<FlashcardResponse> callback) {
        FlashcardRequest request = new FlashcardRequest(topic, count, userId);

        apiService.getFlashcards(request).enqueue(new Callback<FlashcardResponse>() {
            @Override
            public void onResponse(@NonNull Call<FlashcardResponse> call,
                                   @NonNull Response<FlashcardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    persistFlashcards(subject, response.body());
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<FlashcardResponse> call, @NonNull Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /** Updates a card's next review date and difficulty after the user rates it. */
    public void rateFlashcard(int cardId, int rating, long nextReviewDate) {
        ioExecutor.execute(() ->
                database.flashcardDao().updateReviewDate(cardId, nextReviewDate, rating));
    }

    private void persistFlashcards(String subject, FlashcardResponse response) {
        if (response.getCards() == null || response.getCards().isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        List<FlashcardEntity> entities = new ArrayList<>();

        for (FlashcardResponse.Card card : response.getCards()) {
            FlashcardEntity entity = new FlashcardEntity();
            entity.front = card.getFront();
            entity.back = card.getBack();
            entity.hint = card.getHint();
            entity.subject = subject;
            entity.difficulty = 0;
            entity.nextReviewDate = now;
            entity.createdAt = now;
            entities.add(entity);
        }

        ioExecutor.execute(() -> database.flashcardDao().insertAll(entities));
    }

    // ---------------------------------------------------------------------------------------------
    // Home / User profile
    // ---------------------------------------------------------------------------------------------

    /** Observes the Firestore user document and maps it to a {@link UserProfile}. */
    public LiveData<UserProfile> observeUserProfile(String userId) {
        if (userId == null) {
            return new MutableLiveData<>();
        }
        return new UserProfileLiveData(firestore, userId);
    }

    // ---------------------------------------------------------------------------------------------
    // Quiz
    // ---------------------------------------------------------------------------------------------

    public void getQuiz(String subject,
                        String difficulty,
                        int count,
                        String userId,
                        @NonNull Callback<QuizResponse> callback) {
        QuizRequest request = new QuizRequest(subject, difficulty, count, userId);
        apiService.getQuiz(request).enqueue(callback);
    }

    /**
     * Persists a completed quiz session to Firestore and updates the user's study streak.
     */
    public void saveQuizSession(String userId,
                                String subject,
                                int score,
                                int totalQuestions,
                                List<String> wrongTopics,
                                int timeTakenSecs) {
        if (userId == null) {
            return;
        }

        Map<String, Object> session = new HashMap<>();
        session.put(Constants.FIELD_SUBJECT, subject);
        session.put(Constants.FIELD_SCORE, score);
        session.put(Constants.FIELD_TOTAL_QUESTIONS, totalQuestions);
        session.put(Constants.FIELD_WRONG_TOPICS,
                wrongTopics != null ? wrongTopics : new ArrayList<String>());
        session.put(Constants.FIELD_TIME_TAKEN_SECS, timeTakenSecs);
        session.put(Constants.FIELD_TIMESTAMP, FieldValue.serverTimestamp());

        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .collection(Constants.COLLECTION_QUIZ_SESSIONS)
                .add(session);

        Map<String, Object> studySession = new HashMap<>();
        studySession.put(Constants.FIELD_SUBJECT, subject);
        studySession.put("durationMins", Math.max(1, timeTakenSecs / 60));
        studySession.put(Constants.FIELD_ACTIVITY_TYPE, Constants.ACTIVITY_QUIZ);
        studySession.put(Constants.FIELD_TIMESTAMP, FieldValue.serverTimestamp());

        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .collection(Constants.COLLECTION_STUDY_SESSIONS)
                .add(studySession);

        updateStreakAfterStudy(userId);
    }

    private void updateStreakAfterStudy(String userId) {
        firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        return;
                    }

                    int currentStreak = 0;
                    Long streakVal = snapshot.getLong(Constants.FIELD_STREAK_COUNT);
                    if (streakVal != null) {
                        currentStreak = streakVal.intValue();
                    }

                    com.google.firebase.Timestamp lastStudy =
                            snapshot.getTimestamp(Constants.FIELD_LAST_STUDY_DATE);
                    long lastStudyMillis = lastStudy != null ? lastStudy.toDate().getTime() : 0;

                    int newStreak;
                    if (DateUtils.isSameDay(lastStudyMillis, System.currentTimeMillis())) {
                        newStreak = Math.max(currentStreak, 1);
                    } else if (DateUtils.isYesterday(lastStudyMillis)) {
                        newStreak = currentStreak + 1;
                    } else {
                        newStreak = 1;
                    }

                    Map<String, Object> updates = new HashMap<>();
                    updates.put(Constants.FIELD_STREAK_COUNT, newStreak);
                    updates.put(Constants.FIELD_LAST_STUDY_DATE, FieldValue.serverTimestamp());
                    updates.put(Constants.FIELD_TOTAL_XP,
                            (snapshot.getLong(Constants.FIELD_TOTAL_XP) != null
                                    ? snapshot.getLong(Constants.FIELD_TOTAL_XP) : 0) + 10);

                    firestore.collection(Constants.COLLECTION_USERS)
                            .document(userId)
                            .update(updates);
                });
    }
}
