package com.zira.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.zira.app.R;

/** Helpers for navigating between activities with consistent slide transitions. */
public final class NavigationUtils {

    private NavigationUtils() {
    }

    /** Starts {@code target} with a slide-in-from-right transition. */
    public static void slideTo(Activity from, Class<?> target) {
        from.startActivity(new Intent(from, target));
        from.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /** Applies the reverse slide transition when finishing a child activity. */
    public static void applyBackTransition(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /** @return {@code true} when a network connection is available; otherwise {@code false}. */
    public static boolean isOnline(Context context) {
        return NetworkUtils.isConnected(context);
    }
}
