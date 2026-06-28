package com.zira.app.ui.progress;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zira.app.data.model.ProgressData;
import com.zira.app.data.repository.ZiraRepository;

public class ProgressViewModel extends AndroidViewModel {

    private final ZiraRepository repository;
    private final String userId;

    private final MutableLiveData<ProgressData> progressLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public ProgressViewModel(@NonNull Application application) {
        super(application);
        repository = new ZiraRepository(application);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;
    }

    public LiveData<ProgressData> getProgress() {
        return progressLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void refresh() {
        loadingLiveData.setValue(true);
        repository.loadProgressData(userId, new ZiraRepository.ProgressLoadCallback() {
            @Override
            public void onSuccess(ProgressData data) {
                loadingLiveData.postValue(false);
                progressLiveData.postValue(data);
            }

            @Override
            public void onError() {
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Could not load progress data.");
            }
        });
    }
}
