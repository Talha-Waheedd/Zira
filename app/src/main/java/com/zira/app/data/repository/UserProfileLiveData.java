package com.zira.app.data.repository;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.zira.app.data.model.UserProfile;
import com.zira.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/** LiveData that observes {@code users/{uid}} and auto-removes the Firestore listener. */
class UserProfileLiveData extends LiveData<UserProfile> {

    private final FirebaseFirestore firestore;
    private final String userId;
    private ListenerRegistration registration;

    UserProfileLiveData(FirebaseFirestore firestore, String userId) {
        this.firestore = firestore;
        this.userId = userId;
    }

    @Override
    protected void onActive() {
        registration = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .addSnapshotListener((snapshot, error) -> {
                    if (snapshot != null && snapshot.exists()) {
                        setValue(mapUserProfile(snapshot));
                    }
                });
    }

    @Override
    protected void onInactive() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    private static UserProfile mapUserProfile(DocumentSnapshot snapshot) {
        UserProfile profile = new UserProfile();
        profile.name = snapshot.getString(Constants.FIELD_NAME);
        profile.email = snapshot.getString(Constants.FIELD_EMAIL);
        Long streak = snapshot.getLong(Constants.FIELD_STREAK_COUNT);
        profile.streakCount = streak != null ? streak.intValue() : 0;
        Long goal = snapshot.getLong(Constants.FIELD_DAILY_GOAL_MINS);
        profile.dailyGoalMins = goal != null ? goal.intValue() : Constants.DEFAULT_DAILY_GOAL_MINS;
        Long xp = snapshot.getLong(Constants.FIELD_TOTAL_XP);
        profile.totalXp = xp != null ? xp.intValue() : 0;
        List<String> subjects = (List<String>) snapshot.get(Constants.FIELD_SUBJECTS);
        profile.subjects = subjects != null ? new ArrayList<>(subjects) : new ArrayList<>();
        return profile;
    }
}
