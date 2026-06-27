package com.zira.app.ui;

import android.content.Intent;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zira.app.R;
import com.zira.app.ui.ask.AskActivity;
import com.zira.app.ui.flashcards.FlashcardActivity;
import com.zira.app.ui.home.HomeActivity;
import com.zira.app.ui.profile.ProfileActivity;
import com.zira.app.ui.progress.ProgressActivity;

/**
 * Base activity that wires up the shared Material 3 bottom navigation bar.
 *
 * <p>Each top-level screen ({@link HomeActivity}, {@link AskActivity}, etc.) extends this class.
 * Tabs are switched by reordering existing activity instances to the front
 * ({@link Intent#FLAG_ACTIVITY_REORDER_TO_FRONT}) so each tab keeps a single instance and
 * transitions feel instant.
 */
public abstract class BaseNavActivity extends AppCompatActivity {

    /** @return the menu item id (e.g. {@code R.id.nav_home}) that this screen represents. */
    @IdRes
    protected abstract int getSelectedNavItemId();

    /**
     * Subclasses MUST call this after {@code setContentView(...)} and after their layout
     * (which includes {@code @id/bottomNav}) has been inflated.
     */
    protected void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        if (nav == null) {
            return;
        }

        // Mark the current tab as selected before attaching the listener so we don't navigate
        // to ourselves during setup.
        nav.setSelectedItemId(getSelectedNavItemId());

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == getSelectedNavItemId()) {
                return true;
            }

            Class<?> target = resolveTarget(id);
            if (target == null) {
                return false;
            }

            Intent intent = new Intent(this, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        });
    }

    @Nullable
    private Class<?> resolveTarget(int menuItemId) {
        if (menuItemId == R.id.nav_home) {
            return HomeActivity.class;
        } else if (menuItemId == R.id.nav_ask) {
            return AskActivity.class;
        } else if (menuItemId == R.id.nav_flashcards) {
            return FlashcardActivity.class;
        } else if (menuItemId == R.id.nav_progress) {
            return ProgressActivity.class;
        } else if (menuItemId == R.id.nav_profile) {
            return ProfileActivity.class;
        }
        return null;
    }
}
