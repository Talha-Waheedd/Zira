package com.zira.app.data.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zira.app.data.local.AppDatabase;
import com.zira.app.data.local.entity.ExplanationEntity;
import com.zira.app.data.remote.ApiService;
import com.zira.app.data.remote.RetrofitClient;
import com.zira.app.data.remote.model.ExplanationRequest;
import com.zira.app.data.remote.model.ExplanationResponse;
import com.zira.app.utils.Constants;

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
}
