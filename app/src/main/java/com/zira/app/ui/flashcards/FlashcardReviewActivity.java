package com.zira.app.ui.flashcards;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.zira.app.R;
import com.zira.app.data.local.entity.FlashcardEntity;
import com.zira.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Swipeable flashcard review session with tap-to-flip and SM-2 rating bottom sheet.
 */
public class FlashcardReviewActivity extends AppCompatActivity
        implements FlashcardPagerAdapter.OnCardFlippedListener,
        RatingBottomSheet.RatingListener {

    private ViewPager2 viewPager;
    private TextView tvProgress;
    private MaterialToolbar toolbar;

    private FlashcardViewModel viewModel;
    private FlashcardPagerAdapter pagerAdapter;

    private String reviewSubject;
    private final List<FlashcardEntity> sessionCards = new ArrayList<>();
    private int currentIndex;
    private boolean ratingSheetShowing;
    private boolean sessionInitialized;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_review);

        reviewSubject = getIntent().getStringExtra(Constants.EXTRA_REVIEW_SUBJECT);
        viewModel = new ViewModelProvider(this).get(FlashcardViewModel.class);

        bindViews();
        setupToolbar();
        setupViewPager();
        observeDueCards();
    }

    private void bindViews() {
        viewPager = findViewById(R.id.viewPager);
        tvProgress = findViewById(R.id.tvProgress);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        if (reviewSubject != null) {
            toolbar.setTitle(reviewSubject);
        } else {
            toolbar.setTitle(R.string.flashcard_review_all);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViewPager() {
        pagerAdapter = new FlashcardPagerAdapter();
        pagerAdapter.setOnCardFlippedListener(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                updateProgress();
                ratingSheetShowing = false;
            }
        });
    }

    private void observeDueCards() {
        viewModel.getDueCards(reviewSubject).observe(this, cards -> {
            if (sessionInitialized) {
                return;
            }
            if (cards == null || cards.isEmpty()) {
                Snackbar.make(viewPager, R.string.flashcard_no_due, Snackbar.LENGTH_LONG)
                        .show();
                finish();
                return;
            }

            sessionCards.clear();
            sessionCards.addAll(cards);
            pagerAdapter.setCards(sessionCards);
            sessionInitialized = true;
            currentIndex = 0;
            updateProgress();
        });
    }

    private void updateProgress() {
        if (sessionCards.isEmpty()) {
            tvProgress.setText("");
            return;
        }
        tvProgress.setText(getString(
                R.string.flashcard_progress, currentIndex + 1, sessionCards.size()));
    }

    @Override
    public void onCardFlipped(FlashcardEntity card) {
        if (ratingSheetShowing) {
            return;
        }
        ratingSheetShowing = true;
        RatingBottomSheet sheet = RatingBottomSheet.newInstance(card.id);
        sheet.setRatingListener(this);
        sheet.show(getSupportFragmentManager(), "rating");
    }

    @Override
    public void onRated(int cardId, int rating) {
        viewModel.rateCard(cardId, rating);
        ratingSheetShowing = false;

        for (int i = 0; i < sessionCards.size(); i++) {
            if (sessionCards.get(i).id == cardId) {
                sessionCards.remove(i);
                break;
            }
        }

        if (sessionCards.isEmpty()) {
            Snackbar.make(viewPager, R.string.flashcard_review_complete, Snackbar.LENGTH_LONG)
                    .show();
            finish();
            return;
        }

        if (currentIndex >= sessionCards.size()) {
            currentIndex = sessionCards.size() - 1;
        }
        pagerAdapter.setCards(sessionCards);
        viewPager.setCurrentItem(currentIndex, true);
        updateProgress();
    }
}
