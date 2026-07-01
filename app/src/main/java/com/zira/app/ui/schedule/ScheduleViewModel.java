package com.zira.app.ui.schedule;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zira.app.R;
import com.zira.app.data.remote.model.ScheduleRequest;
import com.zira.app.data.remote.model.ScheduleResponse;
import com.zira.app.data.repository.ZiraRepository;
import com.zira.app.utils.ApiErrorUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleViewModel extends AndroidViewModel {

    private final ZiraRepository repository;
    private final android.content.Context appContext;
    private final String userId;

    private final MutableLiveData<List<ScheduleResponse.ScheduleItem>> scheduleLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        repository = new ZiraRepository(application);
        appContext = application.getApplicationContext();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;
    }

    public LiveData<List<ScheduleResponse.ScheduleItem>> getSchedule() {
        return scheduleLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void generateSchedule(List<ExamEntry> exams, int dailyMins) {
        List<ScheduleRequest.Exam> apiExams = new ArrayList<>();
        for (ExamEntry entry : exams) {
            if (entry.isValid()) {
                apiExams.add(new ScheduleRequest.Exam(entry.subject, entry.dateIso));
            }
        }

        if (apiExams.isEmpty()) {
            errorLiveData.setValue("Add at least one exam with a subject and date.");
            return;
        }

        if (userId == null) {
            errorLiveData.setValue("Please sign in again.");
            return;
        }

        loadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        repository.getSchedule(apiExams, dailyMins, userId, new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScheduleResponse> call,
                                   @NonNull Response<ScheduleResponse> response) {
                loadingLiveData.postValue(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getSchedule() != null) {
                    scheduleLiveData.postValue(response.body().getSchedule());
                } else {
                    ApiErrorUtils.logHttpError("ScheduleViewModel", response);
                    errorLiveData.postValue(
                            ApiErrorUtils.userMessageForHttp(
                                    appContext, response, R.string.error_schedule_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScheduleResponse> call, @NonNull Throwable t) {
                loadingLiveData.postValue(false);
                ApiErrorUtils.logNetworkFailure("ScheduleViewModel", t);
                errorLiveData.postValue(
                        ApiErrorUtils.userMessageForFailure(appContext, t));
            }
        });
    }
}
