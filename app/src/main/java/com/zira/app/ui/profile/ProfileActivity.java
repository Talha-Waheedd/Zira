package com.zira.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.zira.app.R;
import com.zira.app.data.model.UserProfile;
import com.zira.app.ui.BaseNavActivity;
import com.zira.app.ui.auth.LoginActivity;
import com.zira.app.ui.schedule.ScheduleActivity;
import com.zira.app.utils.Constants;
import com.zira.app.utils.NavigationUtils;
import com.zira.app.utils.PrefsHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileActivity extends BaseNavActivity {

    private TextView tvInitials;
    private TextView tvDisplayName;
    private TextView tvEmail;
    private TextView tvDailyGoalValue;
    private ChipGroup chipGroupSubjects;
    private Slider sliderDailyGoal;
    private MaterialSwitch switchDarkMode;
    private MaterialSwitch switchNotifications;

    private ProfileViewModel viewModel;
    private boolean bindingProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNav();

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        bindViews();
        setupSubjectChips();
        setupSettings();
        observeProfile();
    }

    private void bindViews() {
        tvInitials = findViewById(R.id.tvInitials);
        tvDisplayName = findViewById(R.id.tvDisplayName);
        tvEmail = findViewById(R.id.tvEmail);
        tvDailyGoalValue = findViewById(R.id.tvDailyGoalValue);
        chipGroupSubjects = findViewById(R.id.chipGroupSubjects);
        sliderDailyGoal = findViewById(R.id.sliderDailyGoal);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);

        MaterialButton btnSignOut = findViewById(R.id.btnSignOut);
        MaterialButton btnOpenSchedule = findViewById(R.id.btnOpenSchedule);

        btnSignOut.setOnClickListener(v -> signOut());
        btnOpenSchedule.setOnClickListener(v ->
                NavigationUtils.slideTo(this, ScheduleActivity.class));
        findViewById(R.id.cardSchedule).setOnClickListener(v ->
                NavigationUtils.slideTo(this, ScheduleActivity.class));
    }

    private void setupSubjectChips() {
        chipGroupSubjects.removeAllViews();
        for (String subject : Constants.DEFAULT_SUBJECTS) {
            Chip chip = new Chip(this);
            chip.setText(subject);
            chip.setCheckable(true);
            chip.setChipIconVisible(false);
            chip.setOnCheckedChangeListener((button, checked) -> {
                if (!bindingProfile) {
                    viewModel.updateSubjects(collectSelectedSubjects());
                }
            });
            chipGroupSubjects.addView(chip);
        }
    }

    private void setupSettings() {
        switchDarkMode.setChecked(PrefsHelper.isDarkMode(this));
        switchNotifications.setChecked(PrefsHelper.areNotificationsEnabled(this));

        switchDarkMode.setOnCheckedChangeListener((btn, checked) ->
                PrefsHelper.setDarkMode(ProfileActivity.this, checked));

        switchNotifications.setOnCheckedChangeListener((btn, checked) ->
                PrefsHelper.setNotificationsEnabled(ProfileActivity.this, checked));

        sliderDailyGoal.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(Slider slider) {
                int mins = Math.round(slider.getValue());
                tvDailyGoalValue.setText(getString(R.string.minutes_per_day, mins));
                if (!bindingProfile) {
                    viewModel.updateDailyGoal(mins);
                    Snackbar.make(slider, R.string.profile_saved, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void observeProfile() {
        viewModel.getUserProfile().observe(this, profile -> {
            if (profile == null) {
                return;
            }
            bindProfile(profile);
        });
    }

    private void bindProfile(UserProfile profile) {
        bindingProfile = true;

        tvDisplayName.setText(
                !TextUtils.isEmpty(profile.name) ? profile.name : getString(R.string.app_name));
        tvEmail.setText(profile.email != null ? profile.email : "");
        tvInitials.setText(initials(profile.name));

        int goal = profile.dailyGoalMins > 0
                ? profile.dailyGoalMins : Constants.DEFAULT_DAILY_GOAL_MINS;
        sliderDailyGoal.setValue(goal);
        tvDailyGoalValue.setText(getString(R.string.minutes_per_day, goal));

        Set<String> selected = new HashSet<>();
        if (profile.subjects != null) {
            selected.addAll(profile.subjects);
        }
        for (int i = 0; i < chipGroupSubjects.getChildCount(); i++) {
            View child = chipGroupSubjects.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setChecked(selected.contains(chip.getText().toString()));
            }
        }

        bindingProfile = false;
    }

    private List<String> collectSelectedSubjects() {
        List<String> subjects = new ArrayList<>();
        for (int i = 0; i < chipGroupSubjects.getChildCount(); i++) {
            View child = chipGroupSubjects.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    subjects.add(chip.getText().toString());
                }
            }
        }
        return subjects;
    }

    private void signOut() {
        viewModel.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private static String initials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1))
                .toUpperCase();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_profile;
    }
}
