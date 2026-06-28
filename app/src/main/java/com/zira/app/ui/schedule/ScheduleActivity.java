package com.zira.app.ui.schedule;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zira.app.R;
import com.zira.app.data.repository.ZiraRepository;
import com.zira.app.utils.NavigationUtils;
import com.zira.app.utils.NetworkUtils;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerExams;
    private RecyclerView recyclerSchedule;
    private TextView tvResultsTitle;
    private LinearProgressIndicator progressGenerating;

    private ExamInputAdapter examAdapter;
    private ScheduleTaskAdapter scheduleAdapter;
    private ScheduleViewModel viewModel;
    private int dailyGoalMins = com.zira.app.utils.Constants.DEFAULT_DAILY_GOAL_MINS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        bindViews();
        setupRecyclerViews();
        loadDailyGoal();
        observeViewModel();
    }

    @Override
    public void finish() {
        super.finish();
        NavigationUtils.applyBackTransition(this);
    }

    private void bindViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerExams = findViewById(R.id.recyclerExams);
        recyclerSchedule = findViewById(R.id.recyclerSchedule);
        tvResultsTitle = findViewById(R.id.tvResultsTitle);
        progressGenerating = findViewById(R.id.progressGenerating);

        MaterialButton btnAddExam = findViewById(R.id.btnAddExam);
        MaterialButton btnGenerate = findViewById(R.id.btnGenerate);

        btnAddExam.setOnClickListener(v -> examAdapter.addExam());
        btnGenerate.setOnClickListener(v -> {
            if (!NetworkUtils.isConnected(this)) {
                Snackbar.make(btnGenerate, R.string.error_offline, Snackbar.LENGTH_LONG).show();
                return;
            }
            viewModel.generateSchedule(examAdapter.getExams(), dailyGoalMins);
        });
    }

    private void setupRecyclerViews() {
        examAdapter = new ExamInputAdapter(null);
        recyclerExams.setLayoutManager(new LinearLayoutManager(this));
        recyclerExams.setAdapter(examAdapter);

        scheduleAdapter = new ScheduleTaskAdapter();
        recyclerSchedule.setLayoutManager(new LinearLayoutManager(this));
        recyclerSchedule.setAdapter(scheduleAdapter);
    }

    private void loadDailyGoal() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        new ZiraRepository(getApplication())
                .observeUserProfile(user.getUid())
                .observe(this, profile -> {
                    if (profile != null && profile.dailyGoalMins > 0) {
                        dailyGoalMins = profile.dailyGoalMins;
                    }
                });
    }

    private void observeViewModel() {
        viewModel.isLoading().observe(this, loading ->
                progressGenerating.setVisibility(Boolean.TRUE.equals(loading)
                        ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(this, error -> {
            if (!TextUtils.isEmpty(error)) {
                Snackbar.make(recyclerExams, error, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getSchedule().observe(this, items -> {
            if (items != null && !items.isEmpty()) {
                tvResultsTitle.setVisibility(View.VISIBLE);
                scheduleAdapter.submitList(items);
            }
        });
    }
}
