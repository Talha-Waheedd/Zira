package com.zira.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zira.app.R;
import com.zira.app.data.local.entity.ExplanationEntity;
import com.zira.app.data.model.UserProfile;
import com.zira.app.ui.BaseNavActivity;
import com.zira.app.ui.ask.AskActivity;
import com.zira.app.ui.flashcards.FlashcardActivity;
import com.zira.app.ui.flashcards.FlashcardReviewActivity;
import com.zira.app.ui.quiz.QuizActivity;

public class HomeActivity extends BaseNavActivity
        implements RecentExplanationAdapter.OnExplanationClickListener {

    private TextView tvGreeting;
    private Chip chipStreak;
    private TextView tvReviewDue;
    private MaterialButton btnReviewNow;
    private TextView tvRecentEmpty;
    private RecyclerView recyclerRecent;
    private ExtendedFloatingActionButton fabAsk;

    private HomeViewModel viewModel;
    private RecentExplanationAdapter recentAdapter;
    private int dueFlashcardCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupBottomNav();

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        bindViews();
        setupRecentRecycler();
        setupClickListeners();
        observeViewModel();
    }

    private void bindViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        chipStreak = findViewById(R.id.chipStreak);
        tvReviewDue = findViewById(R.id.tvReviewDue);
        btnReviewNow = findViewById(R.id.btnReviewNow);
        tvRecentEmpty = findViewById(R.id.tvRecentEmpty);
        recyclerRecent = findViewById(R.id.recyclerRecent);
        fabAsk = findViewById(R.id.fabAsk);
    }

    private void setupRecentRecycler() {
        recentAdapter = new RecentExplanationAdapter(this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerRecent.setLayoutManager(layoutManager);
        recyclerRecent.setAdapter(recentAdapter);
    }

    private void setupClickListeners() {
        fabAsk.setOnClickListener(v ->
                startActivity(new Intent(this, AskActivity.class)));

        findViewById(R.id.btnStartQuiz).setOnClickListener(v ->
                startActivity(new Intent(this, QuizActivity.class)));

        findViewById(R.id.cardQuiz).setOnClickListener(v ->
                startActivity(new Intent(this, QuizActivity.class)));

        btnReviewNow.setOnClickListener(v -> openReviewDue());

        findViewById(R.id.cardReview).setOnClickListener(v -> openReviewDue());
    }

    private void openReviewDue() {
        if (dueFlashcardCount > 0) {
            startActivity(new Intent(this, FlashcardReviewActivity.class));
        } else {
            Snackbar.make(findViewById(R.id.cardReview), R.string.flashcard_no_due, Snackbar.LENGTH_LONG)
                    .setAction(R.string.nav_flashcards, v ->
                            startActivity(new Intent(this, FlashcardActivity.class)))
                    .show();
        }
    }

    private void observeViewModel() {
        viewModel.getUserProfile().observe(this, this::bindUserProfile);

        viewModel.getDueFlashcardCount().observe(this, count -> {
            dueFlashcardCount = count != null ? count : 0;
            boolean hasDue = dueFlashcardCount > 0;
            tvReviewDue.setText(hasDue
                    ? getString(R.string.home_review_due, dueFlashcardCount)
                    : getString(R.string.home_review_none));
            btnReviewNow.setEnabled(hasDue);
            btnReviewNow.setAlpha(hasDue ? 1f : 0.5f);
        });

        viewModel.getRecentExplanations().observe(this, explanations -> {
            recentAdapter.submitList(explanations);
            boolean empty = explanations == null || explanations.isEmpty();
            tvRecentEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerRecent.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }

    private void bindUserProfile(UserProfile profile) {
        if (profile == null) {
            tvGreeting.setText(R.string.home_greeting_default);
            chipStreak.setText(R.string.home_streak_zero);
            return;
        }

        tvGreeting.setText(viewModel.buildGreeting(profile.name));

        if (profile.streakCount > 0) {
            chipStreak.setText(getString(R.string.home_streak, profile.streakCount));
        } else {
            chipStreak.setText(R.string.home_streak_zero);
        }
    }

    @Override
    public void onExplanationClick(ExplanationEntity explanation) {
        Snackbar.make(recyclerRecent, explanation.question, Snackbar.LENGTH_SHORT)
                .setAction(R.string.nav_ask, v ->
                        startActivity(new Intent(this, AskActivity.class)))
                .show();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_home;
    }
}
