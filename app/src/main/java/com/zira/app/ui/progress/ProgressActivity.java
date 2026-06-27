package com.zira.app.ui.progress;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.zira.app.R;
import com.zira.app.ui.BaseNavActivity;

/**
 * Progress tab host. Charts and streak calendar are built in Days 15–17.
 */
public class ProgressActivity extends BaseNavActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        setupBottomNav();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_progress;
    }
}
