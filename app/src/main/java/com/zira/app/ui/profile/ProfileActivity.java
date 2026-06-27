package com.zira.app.ui.profile;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.zira.app.R;
import com.zira.app.ui.BaseNavActivity;

/**
 * Profile tab host. Editable profile, settings, and sign-out are built in Days 15–17.
 */
public class ProfileActivity extends BaseNavActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNav();
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_profile;
    }
}
