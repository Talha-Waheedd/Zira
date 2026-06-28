package com.zira.app.ui.quiz;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zira.app.R;
import com.zira.app.data.remote.model.QuizResponse;
import com.zira.app.utils.Constants;
import com.zira.app.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private View panelSetup;
    private View panelQuiz;
    private LinearProgressIndicator progressLoading;
    private LinearProgressIndicator progressQuiz;
    private AutoCompleteTextView actSubject;
    private ChipGroup chipGroupDifficulty;
    private TextView tvQuizProgress;
    private TextView tvTimer;
    private TextView tvQuestion;
    private TextView tvFeedback;
    private MaterialButton btnNext;
    private LottieAnimationView lottieSuccess;

    private final Chip[] optionChips = new Chip[4];
    private final Handler timerHandler = new Handler(Looper.getMainLooper());

    private QuizViewModel viewModel;
    private String userId;

    private List<QuizResponse.QuizQuestion> questions = new ArrayList<>();
    private int currentIndex;
    private int score;
    private final List<String> wrongTopics = new ArrayList<>();
    private long quizStartMillis;
    private boolean answered;
    private String selectedSubject;
    private String selectedDifficulty;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedSecs = (System.currentTimeMillis() - quizStartMillis) / 1000;
            long minutes = elapsedSecs / 60;
            long seconds = elapsedSecs % 60;
            tvTimer.setText(getString(R.string.quiz_timer, minutes, seconds));
            timerHandler.postDelayed(this, 1000L);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        bindViews();
        setupToolbar();
        setupSubjectDropdown();
        setupStartButton();
        observeViewModel();
    }

    private void bindViews() {
        panelSetup = findViewById(R.id.panelSetup);
        panelQuiz = findViewById(R.id.panelQuiz);
        progressLoading = findViewById(R.id.progressLoading);
        progressQuiz = findViewById(R.id.progressQuiz);
        actSubject = findViewById(R.id.actSubject);
        chipGroupDifficulty = findViewById(R.id.chipGroupDifficulty);
        tvQuizProgress = findViewById(R.id.tvQuizProgress);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvFeedback = findViewById(R.id.tvFeedback);
        btnNext = findViewById(R.id.btnNext);
        lottieSuccess = findViewById(R.id.lottieSuccess);

        optionChips[0] = findViewById(R.id.chipOption0);
        optionChips[1] = findViewById(R.id.chipOption1);
        optionChips[2] = findViewById(R.id.chipOption2);
        optionChips[3] = findViewById(R.id.chipOption3);

        btnNext.setOnClickListener(v -> advanceQuestion());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSubjectDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                Constants.DEFAULT_SUBJECTS);
        actSubject.setAdapter(adapter);
        if (Constants.DEFAULT_SUBJECTS.length > 0) {
            actSubject.setText(Constants.DEFAULT_SUBJECTS[0], false);
        }
    }

    private void setupStartButton() {
        findViewById(R.id.btnStartQuiz).setOnClickListener(v -> {
            selectedSubject = actSubject.getText().toString().trim();
            if (TextUtils.isEmpty(selectedSubject)) {
                Snackbar.make(panelSetup, R.string.error_no_subjects, Snackbar.LENGTH_SHORT).show();
                return;
            }
            selectedDifficulty = getSelectedDifficulty();
            if (userId == null) {
                Snackbar.make(panelSetup, R.string.error_auth_failed, Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!NetworkUtils.isConnected(this)) {
                Snackbar.make(panelSetup, R.string.error_offline, Snackbar.LENGTH_LONG).show();
                return;
            }
            viewModel.loadQuiz(selectedSubject, selectedDifficulty, userId);
        });
    }

    private String getSelectedDifficulty() {
        int checkedId = chipGroupDifficulty.getCheckedChipId();
        if (checkedId == R.id.chipMedium) {
            return Constants.DIFFICULTY_MEDIUM;
        } else if (checkedId == R.id.chipHard) {
            return Constants.DIFFICULTY_HARD;
        }
        return Constants.DIFFICULTY_EASY;
    }

    private void observeViewModel() {
        viewModel.isLoading().observe(this, loading ->
                progressLoading.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(this, error -> {
            if (!TextUtils.isEmpty(error)) {
                Snackbar.make(panelSetup, error, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getQuestions().observe(this, loaded -> {
            if (loaded == null || loaded.isEmpty()) {
                return;
            }
            questions = loaded;
            startQuizSession();
        });
    }

    private void startQuizSession() {
        panelSetup.setVisibility(View.GONE);
        panelQuiz.setVisibility(View.VISIBLE);

        currentIndex = 0;
        score = 0;
        wrongTopics.clear();
        quizStartMillis = System.currentTimeMillis();
        timerHandler.post(timerRunnable);

        progressQuiz.setMax(questions.size());
        showQuestion(currentIndex);
    }

    private void showQuestion(int index) {
        answered = false;
        btnNext.setVisibility(View.GONE);
        tvFeedback.setVisibility(View.GONE);
        lottieSuccess.setVisibility(View.GONE);

        QuizResponse.QuizQuestion question = questions.get(index);
        tvQuestion.setText(question.getQuestion());
        tvQuizProgress.setText(getString(
                R.string.quiz_question_progress, index + 1, questions.size()));
        progressQuiz.setProgressCompat(index + 1, true);

        List<String> options = question.getOptions();
        for (int i = 0; i < optionChips.length; i++) {
            Chip chip = optionChips[i];
            resetChipAppearance(chip);
            chip.setEnabled(true);

            if (options != null && i < options.size()) {
                chip.setText(options.get(i));
                chip.setVisibility(View.VISIBLE);
                final int optionIndex = i;
                chip.setOnClickListener(v -> onOptionSelected(optionIndex, chip));
            } else {
                chip.setVisibility(View.INVISIBLE);
                chip.setOnClickListener(null);
            }
        }
    }

    private void onOptionSelected(int optionIndex, Chip chip) {
        if (answered) {
            return;
        }
        answered = true;
        setOptionsEnabled(false);

        QuizResponse.QuizQuestion question = questions.get(currentIndex);
        boolean correct = optionIndex == question.getCorrectIndex();

        if (correct) {
            score++;
            showCorrectFeedback(chip);
        } else {
            String correctAnswer = getCorrectAnswerText(question);
            showWrongFeedback(chip, correctAnswer);
            wrongTopics.add(truncate(question.getQuestion(), 80));
            highlightCorrectOption(question.getCorrectIndex());
        }

        btnNext.setText(currentIndex < questions.size() - 1
                ? R.string.quiz_next : R.string.quiz_finish);
        btnNext.setVisibility(View.VISIBLE);
    }

    private void showCorrectFeedback(Chip chip) {
        chip.setChipBackgroundColorResource(R.color.quiz_correct_container);
        chip.setTextColor(getColor(R.color.quiz_correct));
        pulseView(chip);
        tvFeedback.setText(R.string.quiz_correct_feedback);
        tvFeedback.setTextColor(getColor(R.color.quiz_correct));
        tvFeedback.setVisibility(View.VISIBLE);

        lottieSuccess.setVisibility(View.VISIBLE);
        lottieSuccess.playAnimation();
    }

    private void showWrongFeedback(Chip chip, String correctAnswer) {
        chip.setChipBackgroundColorResource(R.color.quiz_wrong_container);
        chip.setTextColor(getColor(R.color.quiz_wrong));
        shakeView(chip);
        tvFeedback.setText(getString(R.string.quiz_wrong_feedback, correctAnswer));
        tvFeedback.setTextColor(getColor(R.color.quiz_wrong));
        tvFeedback.setVisibility(View.VISIBLE);
    }

    private void highlightCorrectOption(int correctIndex) {
        if (correctIndex >= 0 && correctIndex < optionChips.length) {
            Chip correctChip = optionChips[correctIndex];
            correctChip.setChipBackgroundColorResource(R.color.quiz_correct_container);
            correctChip.setTextColor(getColor(R.color.quiz_correct));
        }
    }

    private String getCorrectAnswerText(QuizResponse.QuizQuestion question) {
        List<String> options = question.getOptions();
        int idx = question.getCorrectIndex();
        if (options != null && idx >= 0 && idx < options.size()) {
            return options.get(idx);
        }
        return question.getExplanation() != null ? question.getExplanation() : "";
    }

    private void advanceQuestion() {
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            showQuestion(currentIndex);
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        timerHandler.removeCallbacks(timerRunnable);
        long elapsedSecs = (System.currentTimeMillis() - quizStartMillis) / 1000;

        viewModel.saveQuizSession(
                userId,
                selectedSubject,
                score,
                questions.size(),
                wrongTopics,
                (int) elapsedSecs);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Constants.EXTRA_QUIZ_SUBJECT, selectedSubject);
        intent.putExtra(Constants.EXTRA_QUIZ_SCORE, score);
        intent.putExtra(Constants.EXTRA_QUIZ_TOTAL, questions.size());
        intent.putExtra(Constants.EXTRA_QUIZ_TIME_SECS, (int) elapsedSecs);
        intent.putStringArrayListExtra(
                Constants.EXTRA_QUIZ_WRONG_TOPICS, new ArrayList<>(wrongTopics));
        startActivity(intent);
        finish();
    }

    private void setOptionsEnabled(boolean enabled) {
        for (Chip chip : optionChips) {
            chip.setEnabled(enabled);
        }
    }

    private void resetChipAppearance(Chip chip) {
        chip.setChipBackgroundColorResource(R.color.secondary_container);
        chip.setTextColor(getColor(R.color.on_secondary_container));
    }

    private void pulseView(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.08f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.08f, 1f);
        scaleX.setDuration(400);
        scaleY.setDuration(400);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.start();
        scaleY.start();
    }

    private void shakeView(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(
                view, View.TRANSLATION_X, 0f, 20f, -20f, 20f, -20f, 10f, -10f, 0f);
        shake.setDuration(400);
        shake.start();
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLen ? text : text.substring(0, maxLen - 1) + "…";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacksAndMessages(null);
    }
}
