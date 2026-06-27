package com.zira.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zira.app.R;
import com.zira.app.ui.auth.LoginActivity;
import com.zira.app.ui.home.HomeActivity;
import com.zira.app.ui.onboarding.OnboardingActivity;
import com.zira.app.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(this::checkAuthAndNavigate, Constants.SPLASH_DELAY_MS);
    }

    private void checkAuthAndNavigate() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            navigateTo(LoginActivity.class);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        DocumentSnapshot doc = task.getResult();
                        Boolean onboardingComplete = doc.getBoolean(Constants.FIELD_ONBOARDING_COMPLETE);
                        if (Boolean.TRUE.equals(onboardingComplete)) {
                            navigateTo(HomeActivity.class);
                        } else {
                            navigateTo(OnboardingActivity.class);
                        }
                    } else {
                        navigateTo(OnboardingActivity.class);
                    }
                });
    }

    private void navigateTo(Class<?> destination) {
        startActivity(new Intent(this, destination));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
