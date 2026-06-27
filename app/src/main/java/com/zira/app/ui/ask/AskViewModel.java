package com.zira.app.ui.ask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zira.app.data.remote.model.ExplanationResponse;
import com.zira.app.data.repository.ZiraRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AskViewModel extends AndroidViewModel {

    private final ZiraRepository repository;

    private final MutableLiveData<ExplanationResponse> resultLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public AskViewModel(@NonNull Application application) {
        super(application);
        repository = new ZiraRepository(application);
    }

    public LiveData<ExplanationResponse> getResult() {
        return resultLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void sendQuestion(String question, String subject, String userId) {
        loadingLiveData.setValue(true);

        repository.getExplanation(question, subject, userId, new Callback<ExplanationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExplanationResponse> call,
                                   @NonNull Response<ExplanationResponse> response) {
                loadingLiveData.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.postValue(response.body());
                } else {
                    errorLiveData.postValue("Zira couldn't answer that. Please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExplanationResponse> call, @NonNull Throwable t) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Network error. Please check your connection.");
            }
        });
    }
}
