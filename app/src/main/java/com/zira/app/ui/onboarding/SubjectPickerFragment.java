package com.zira.app.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zira.app.R;

import java.util.ArrayList;
import java.util.List;

public class SubjectPickerFragment extends Fragment {

    private ChipGroup chipGroupSubjects;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subject_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chipGroupSubjects = view.findViewById(R.id.chipGroupSubjects);
    }

    public List<String> getSelectedSubjects() {
        List<String> selected = new ArrayList<>();
        if (chipGroupSubjects == null) {
            return selected;
        }

        for (int i = 0; i < chipGroupSubjects.getChildCount(); i++) {
            View child = chipGroupSubjects.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    selected.add(chip.getText().toString());
                }
            }
        }
        return selected;
    }
}
