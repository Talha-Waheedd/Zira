package com.zira.app.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zira.app.R;
import com.zira.app.ui.home.HomeActivity;
import com.zira.app.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MaterialButton btnNext;
    private MaterialButton btnSkip;
    private LinearProgressIndicator progressSaving;
    private OnboardingPagerAdapter adapter;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        firestore = FirebaseFirestore.getInstance();

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
        progressSaving = findViewById(R.id.progressSaving);
        TabLayout tabIndicator = findViewById(R.id.tabIndicator);

        adapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(true);

        new TabLayoutMediator(tabIndicator, viewPager, (tab, position) -> {
            // Dots only — no text labels
        }).attach();

        updateBottomBar(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateBottomBar(position);
            }
        });

        btnNext.setOnClickListener(v -> onNextClicked());
        btnSkip.setOnClickListener(v -> finishOnboardingWithDefaults());
    }

    private void updateBottomBar(int position) {
        btnSkip.setVisibility(position == 0 ? View.VISIBLE : View.GONE);

        if (position == adapter.getItemCount() - 1) {
            btnNext.setText(R.string.get_started);
        } else {
            btnNext.setText(R.string.next);
        }
    }

    private void onNextClicked() {
        int current = viewPager.getCurrentItem();

        if (current == 1) {
            SubjectPickerFragment subjectFragment = getSubjectPickerFragment();
            if (subjectFragment != null && subjectFragment.getSelectedSubjects().isEmpty()) {
                Toast.makeText(this, R.string.error_no_subjects, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (current < adapter.getItemCount() - 1) {
            viewPager.setCurrentItem(current + 1, true);
        } else {
            saveAndFinish();
        }
    }

    private void finishOnboardingWithDefaults() {
        Map<String, Object> updates = new HashMap<>();
        updates.put(Constants.FIELD_SUBJECTS, new java.util.ArrayList<String>());
        updates.put(Constants.FIELD_DAILY_GOAL_MINS, Constants.DEFAULT_DAILY_GOAL_MINS);
        updates.put(Constants.FIELD_ONBOARDING_COMPLETE, true);
        persistAndNavigate(updates);
    }

    private void saveAndFinish() {
        SubjectPickerFragment subjectFragment = getSubjectPickerFragment();
        StudyGoalFragment goalFragment = getStudyGoalFragment();

        List<String> subjects = subjectFragment != null
                ? subjectFragment.getSelectedSubjects()
                : new java.util.ArrayList<>();
        int dailyGoal = goalFragment != null
                ? goalFragment.getDailyGoalMins()
                : Constants.DEFAULT_DAILY_GOAL_MINS;

        if (subjects.isEmpty()) {
            Toast.makeText(this, R.string.error_no_subjects, Toast.LENGTH_SHORT).show();
            viewPager.setCurrentItem(1, true);
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put(Constants.FIELD_SUBJECTS, subjects);
        updates.put(Constants.FIELD_DAILY_GOAL_MINS, dailyGoal);
        updates.put(Constants.FIELD_ONBOARDING_COMPLETE, true);
        persistAndNavigate(updates);
    }

    private void persistAndNavigate(Map<String, Object> updates) {
        setSaving(true);

        firestore.collection(Constants.COLLECTION_USERS)
                .document(currentUser.getUid())
                .update(updates)
                .addOnCompleteListener(task -> {
                    setSaving(false);
                    if (task.isSuccessful()) {
                        getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
                                .edit()
                                .putBoolean(Constants.KEY_ONBOARDING_COMPLETE, true)
                                .apply();

                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private SubjectPickerFragment getSubjectPickerFragment() {
        return (SubjectPickerFragment) getSupportFragmentManager()
                .findFragmentByTag("f" + 1);
    }

    private StudyGoalFragment getStudyGoalFragment() {
        return (StudyGoalFragment) getSupportFragmentManager()
                .findFragmentByTag("f" + 2);
    }

    private void setSaving(boolean saving) {
        progressSaving.setVisibility(saving ? View.VISIBLE : View.GONE);
        btnNext.setEnabled(!saving);
        btnSkip.setEnabled(!saving);
        viewPager.setUserInputEnabled(!saving);
    }
}
