package com.zira.app.ui.flashcards;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.zira.app.R;
import com.zira.app.ui.BaseNavActivity;

/**
 * Flashcards tab host. Deck list, generation, and SM-2 review are built in Days 9–11.
 */
public class FlashcardActivity extends BaseNavActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        setupBottomNav();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_flashcards;
    }
}
