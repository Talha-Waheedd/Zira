package com.zira.app.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zira.app.data.model.UserProfile;
import com.zira.app.data.repository.ZiraRepository;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private final ZiraRepository repository;
    private final String userId;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new ZiraRepository(application);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;
    }

    public LiveData<UserProfile> getUserProfile() {
        return repository.observeUserProfile(userId);
    }

    public void updateSubjects(List<String> subjects) {
        repository.updateUserSubjects(userId, subjects);
    }

    public void updateDailyGoal(int dailyGoalMins) {
        repository.updateDailyGoal(userId, dailyGoalMins);
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public String getUserId() {
        return userId;
    }
}
