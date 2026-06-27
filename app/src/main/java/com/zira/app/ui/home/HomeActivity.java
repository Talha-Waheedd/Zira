package com.zira.app.ui.home;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.zira.app.R;
import com.zira.app.ui.BaseNavActivity;

/**
 * Home tab host. The rich dashboard (greeting, streak, due reviews, recent explanations)
 * is built in Days 12–14; for now it hosts the shared bottom navigation skeleton.
 */
public class HomeActivity extends BaseNavActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupBottomNav();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_home;
    }
}
