package com.zira.app.ui.quiz;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zira.app.data.remote.model.QuizResponse;
import com.zira.app.data.repository.ZiraRepository;
import com.zira.app.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizViewModel extends AndroidViewModel {

    private final ZiraRepository repository;

    private final MutableLiveData<List<QuizResponse.QuizQuestion>> questionsLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public QuizViewModel(@NonNull Application application) {
        super(application);
        repository = new ZiraRepository(application);
    }

    public LiveData<List<QuizResponse.QuizQuestion>> getQuestions() {
        return questionsLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void loadQuiz(String subject, String difficulty, String userId) {
        loadingLiveData.setValue(true);

        repository.getQuiz(
                subject,
                difficulty,
                Constants.QUIZ_DEFAULT_COUNT,
                userId,
                new Callback<QuizResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<QuizResponse> call,
                                           @NonNull Response<QuizResponse> response) {
                        loadingLiveData.postValue(false);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getQuestions() != null
                                && !response.body().getQuestions().isEmpty()) {
                            questionsLiveData.postValue(response.body().getQuestions());
                        } else {
                            errorLiveData.postValue(
                                    "Could not load quiz. Please try again.");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<QuizResponse> call,
                                          @NonNull Throwable t) {
                        loadingLiveData.postValue(false);
                        errorLiveData.postValue(
                                "Network error. Please check your connection.");
                    }
                });
    }

    public void saveQuizSession(String userId,
                                String subject,
                                int score,
                                int totalQuestions,
                                List<String> wrongTopics,
                                int timeTakenSecs) {
        repository.saveQuizSession(userId, subject, score, totalQuestions, wrongTopics,
                timeTakenSecs);
    }
}
