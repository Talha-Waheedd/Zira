package com.zira.app.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;
import com.zira.app.R;
import com.zira.app.utils.Constants;

public class StudyGoalFragment extends Fragment {

    private Slider sliderDailyGoal;
    private TextView tvMinutesValue;
    private int dailyGoalMins = Constants.DEFAULT_DAILY_GOAL_MINS;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_study_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sliderDailyGoal = view.findViewById(R.id.sliderDailyGoal);
        tvMinutesValue = view.findViewById(R.id.tvMinutesValue);

        sliderDailyGoal.setValue(Constants.DEFAULT_DAILY_GOAL_MINS);
        updateMinutesLabel(Constants.DEFAULT_DAILY_GOAL_MINS);

        sliderDailyGoal.addOnChangeListener((slider, value, fromUser) -> {
            dailyGoalMins = Math.round(value);
            updateMinutesLabel(dailyGoalMins);
        });
    }

    private void updateMinutesLabel(int minutes) {
        tvMinutesValue.setText(getString(R.string.minutes_per_day, minutes));
    }

    public int getDailyGoalMins() {
        return dailyGoalMins;
    }
}
