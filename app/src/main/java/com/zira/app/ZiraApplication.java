package com.zira.app;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.zira.app.utils.PrefsHelper;

public class ZiraApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PrefsHelper.applySavedTheme(this);
        FirebaseApp.initializeApp(this);
    }
}
