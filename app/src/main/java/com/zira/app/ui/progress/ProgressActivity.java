package com.zira.app.ui.progress;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.zira.app.R;
import com.zira.app.data.model.ProgressData;
import com.zira.app.ui.BaseNavActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgressActivity extends BaseNavActivity {

    private BarChart barChartWeekly;
    private PieChart pieChartMastery;
    private Chip chipStreak;
    private Chip chipXp;
    private LinearProgressIndicator progressLoading;
    private TextView tvWeakEmpty;
    private RecyclerView recyclerWeakTopics;
    private RecyclerView recyclerStreakCalendar;

    private ProgressViewModel viewModel;
    private WeakTopicAdapter weakTopicAdapter;
    private StreakCalendarAdapter streakCalendarAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        setupBottomNav();

        viewModel = new ViewModelProvider(this).get(ProgressViewModel.class);

        bindViews();
        setupRecyclerViews();
        observeViewModel();

        viewModel.refresh();
    }

    private void bindViews() {
        barChartWeekly = findViewById(R.id.barChartWeekly);
        pieChartMastery = findViewById(R.id.pieChartMastery);
        chipStreak = findViewById(R.id.chipStreak);
        chipXp = findViewById(R.id.chipXp);
        progressLoading = findViewById(R.id.progressLoading);
        tvWeakEmpty = findViewById(R.id.tvWeakEmpty);
        recyclerWeakTopics = findViewById(R.id.recyclerWeakTopics);
        recyclerStreakCalendar = findViewById(R.id.recyclerStreakCalendar);

        styleCharts();
    }

    private void setupRecyclerViews() {
        weakTopicAdapter = new WeakTopicAdapter();
        recyclerWeakTopics.setLayoutManager(new LinearLayoutManager(this));
        recyclerWeakTopics.setAdapter(weakTopicAdapter);

        streakCalendarAdapter = new StreakCalendarAdapter();
        recyclerStreakCalendar.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerStreakCalendar.setAdapter(streakCalendarAdapter);
    }

    private void observeViewModel() {
        viewModel.isLoading().observe(this, loading ->
                progressLoading.setVisibility(Boolean.TRUE.equals(loading)
                        ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(this, error -> {
            if (!TextUtils.isEmpty(error)) {
                Snackbar.make(barChartWeekly, error, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getProgress().observe(this, data -> {
            if (data != null) {
                bindProgress(data);
            }
        });
    }

    private void bindProgress(ProgressData data) {
        chipStreak.setText(getString(R.string.home_streak, data.currentStreak));
        chipXp.setText(getString(R.string.progress_xp, data.totalXp));

        bindBarChart(data);
        bindPieChart(data);
        streakCalendarAdapter.setStreakDays(data.streakDays);

        boolean hasWeak = data.weakTopics != null && !data.weakTopics.isEmpty();
        tvWeakEmpty.setVisibility(hasWeak ? View.GONE : View.VISIBLE);
        recyclerWeakTopics.setVisibility(hasWeak ? View.VISIBLE : View.GONE);
        weakTopicAdapter.submitList(data.weakTopics);
    }

    private void bindBarChart(ProgressData data) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < data.weeklyStudyMins.length; i++) {
            entries.add(new BarEntry(i, data.weeklyStudyMins[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, getString(R.string.progress_weekly_minutes));
        dataSet.setColor(getColor(R.color.primary));
        dataSet.setValueTextColor(getColor(R.color.on_surface));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        barChartWeekly.setData(barData);

        XAxis xAxis = barChartWeekly.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(data.dayLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChartWeekly.getAxisRight().setEnabled(false);
        barChartWeekly.getDescription().setEnabled(false);
        barChartWeekly.getLegend().setEnabled(false);
        barChartWeekly.invalidate();
    }

    private void bindPieChart(ProgressData data) {
        if (data.subjectMastery == null || data.subjectMastery.isEmpty()) {
            pieChartMastery.clear();
            pieChartMastery.setNoDataText(getString(R.string.progress_no_data));
            pieChartMastery.invalidate();
            return;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : data.subjectMastery.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                R.color.primary,
                R.color.secondary,
                R.color.tertiary,
                R.color.quiz_correct,
                R.color.quiz_wrong,
                R.color.primary_container
        }, getApplicationContext());
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(11f);

        PieData pieData = new PieData(dataSet);
        pieChartMastery.setData(pieData);
        pieChartMastery.getDescription().setEnabled(false);
        pieChartMastery.setDrawHoleEnabled(true);
        pieChartMastery.setHoleRadius(45f);
        pieChartMastery.setTransparentCircleRadius(50f);
        pieChartMastery.invalidate();
    }

    private void styleCharts() {
        barChartWeekly.setDrawGridBackground(false);
        pieChartMastery.setEntryLabelColor(getColor(R.color.on_surface));
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_progress;
    }
}
