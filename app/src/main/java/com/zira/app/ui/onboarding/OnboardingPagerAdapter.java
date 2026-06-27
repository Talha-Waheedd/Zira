package com.zira.app.ui.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingPagerAdapter extends FragmentStateAdapter {

    public OnboardingPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new SubjectPickerFragment();
            case 2:
                return new StudyGoalFragment();
            case 0:
            default:
                return new WelcomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
