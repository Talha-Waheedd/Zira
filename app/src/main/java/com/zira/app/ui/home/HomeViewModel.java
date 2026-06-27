package com.zira.app.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zira.app.data.local.entity.ExplanationEntity;
import com.zira.app.data.model.UserProfile;
import com.zira.app.data.repository.ZiraRepository;
import com.zira.app.utils.DateUtils;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final ZiraRepository repository;
    private final String userId;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new ZiraRepository(application);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;
    }

    public LiveData<UserProfile> getUserProfile() {
        return repository.observeUserProfile(userId);
    }

    public LiveData<Integer> getDueFlashcardCount() {
        return repository.getTotalDueCount();
    }

    public LiveData<List<ExplanationEntity>> getRecentExplanations() {
        return repository.getRecentExplanations(userId);
    }

    public String buildGreeting(String userName) {
        String greeting = DateUtils.getTimeOfDayGreeting();
        if (userName != null && !userName.isEmpty()) {
            return greeting + ", " + userName;
        }
        return greeting;
    }
}
