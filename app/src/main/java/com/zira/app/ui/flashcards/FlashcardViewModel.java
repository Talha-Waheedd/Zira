package com.zira.app.ui.flashcards;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zira.app.R;
import com.zira.app.data.local.entity.DeckSummary;
import com.zira.app.data.local.entity.FlashcardEntity;
import com.zira.app.data.remote.model.FlashcardResponse;
import com.zira.app.data.repository.ZiraRepository;
import com.zira.app.utils.ApiErrorUtils;
import com.zira.app.utils.Constants;
import com.zira.app.utils.Sm2Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlashcardViewModel extends AndroidViewModel {

    private final ZiraRepository repository;
    private final android.content.Context appContext;

    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> successLiveData = new MutableLiveData<>();

    public FlashcardViewModel(@NonNull Application application) {
        super(application);
        repository = new ZiraRepository(application);
        appContext = application.getApplicationContext();
    }

    public LiveData<List<DeckSummary>> getDeckSummaries() {
        return repository.getDeckSummaries();
    }

    public LiveData<Integer> getTotalDueCount() {
        return repository.getTotalDueCount();
    }

    public LiveData<List<FlashcardEntity>> getDueCards(String subject) {
        if (subject == null || subject.isEmpty()) {
            return repository.getAllDueCards();
        }
        return repository.getDueCardsBySubject(subject);
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<String> getSuccessMessage() {
        return successLiveData;
    }

    public void generateDeck(String subject, String userId) {
        errorLiveData.setValue(null);
        successLiveData.setValue(null);
        loadingLiveData.setValue(true);

        repository.generateFlashcards(
                subject,
                subject,
                Constants.FLASHCARD_DEFAULT_COUNT,
                userId,
                new Callback<FlashcardResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FlashcardResponse> call,
                                           @NonNull Response<FlashcardResponse> response) {
                        loadingLiveData.postValue(false);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getCards() != null
                                && !response.body().getCards().isEmpty()) {
                            int count = response.body().getCards().size();
                            successLiveData.postValue(
                                    count + " cards added to " + subject);
                        } else {
                            ApiErrorUtils.logHttpError("FlashcardViewModel", response);
                            errorLiveData.postValue(
                                    ApiErrorUtils.userMessageForHttp(
                                            appContext, response, R.string.error_flashcards_failed));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FlashcardResponse> call,
                                         @NonNull Throwable t) {
                        loadingLiveData.postValue(false);
                        ApiErrorUtils.logNetworkFailure("FlashcardViewModel", t);
                        errorLiveData.postValue(
                                ApiErrorUtils.userMessageForFailure(appContext, t));
                    }
                });
    }

    public void rateCard(int cardId, int rating) {
        long nextReview = Sm2Utils.nextReviewDateForRating(rating);
        repository.rateFlashcard(cardId, rating, nextReview);
    }
}
