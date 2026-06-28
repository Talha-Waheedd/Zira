package com.zira.app.ui.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zira.app.R;
import com.zira.app.ui.BaseNavActivity;
import com.zira.app.utils.Constants;
import com.zira.app.utils.NetworkUtils;

public class FlashcardActivity extends BaseNavActivity implements DeckAdapter.DeckActionListener {

    private RecyclerView recyclerDecks;
    private View emptyState;
    private LinearProgressIndicator progressGenerating;
    private MaterialToolbar toolbar;

    private DeckAdapter deckAdapter;
    private FlashcardViewModel viewModel;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        setupBottomNav();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;

        viewModel = new ViewModelProvider(this).get(FlashcardViewModel.class);

        bindViews();
        setupToolbar();
        setupRecycler();
        observeViewModel();
    }

    private void bindViews() {
        recyclerDecks = findViewById(R.id.recyclerDecks);
        emptyState = findViewById(R.id.emptyState);
        progressGenerating = findViewById(R.id.progressGenerating);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_review_all) {
                startReview(null);
                return true;
            }
            return false;
        });
    }

    private void setupRecycler() {
        deckAdapter = new DeckAdapter(this);
        recyclerDecks.setLayoutManager(new LinearLayoutManager(this));
        recyclerDecks.setAdapter(deckAdapter);
    }

    private void observeViewModel() {
        viewModel.getDeckSummaries().observe(this, summaries -> {
            deckAdapter.updateSummaries(summaries);
            boolean hasDecks = summaries != null && !summaries.isEmpty();
            emptyState.setVisibility(hasDecks ? View.GONE : View.VISIBLE);
        });

        viewModel.getTotalDueCount().observe(this, dueCount -> {
            toolbar.post(() -> {
                if (toolbar.getMenu().findItem(R.id.action_review_all) != null) {
                    toolbar.getMenu().findItem(R.id.action_review_all)
                            .setVisible(dueCount != null && dueCount > 0);
                }
            });
        });

        viewModel.isLoading().observe(this, loading ->
                progressGenerating.setVisibility(Boolean.TRUE.equals(loading)
                        ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(this, error -> {
            if (!TextUtils.isEmpty(error)) {
                Snackbar.make(recyclerDecks, error, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (!TextUtils.isEmpty(message)) {
                Snackbar.make(recyclerDecks, message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGenerateDeck(String subject) {
        if (userId == null) {
            Snackbar.make(recyclerDecks, R.string.error_auth_failed, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!NetworkUtils.isConnected(this)) {
            Snackbar.make(recyclerDecks, R.string.error_offline, Snackbar.LENGTH_LONG).show();
            return;
        }
        viewModel.generateDeck(subject, userId);
    }

    @Override
    public void onReviewDeck(String subject, int dueCount) {
        if (dueCount > 0) {
            startReview(subject);
        }
    }

    private void startReview(@Nullable String subject) {
        Intent intent = new Intent(this, FlashcardReviewActivity.class);
        if (subject != null) {
            intent.putExtra(Constants.EXTRA_REVIEW_SUBJECT, subject);
        }
        startActivity(intent);
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_flashcards;
    }
}
