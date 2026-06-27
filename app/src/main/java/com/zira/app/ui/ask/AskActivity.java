package com.zira.app.ui.ask;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.zira.app.R;
import com.zira.app.ui.BaseNavActivity;

/**
 * Ask Zira tab host. Full chat experience is built in Days 6–8.
 */
public class AskActivity extends BaseNavActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        setupBottomNav();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_ask;
    }
}
