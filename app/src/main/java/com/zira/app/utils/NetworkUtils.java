package com.zira.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

/** Connectivity helpers used by the Repository to decide between remote and local data. */
public final class NetworkUtils {

    private NetworkUtils() {
    }

    /** @return {@code true} if the device currently has a validated internet connection. */
    public static boolean isConnected(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }

        Network network = cm.getActiveNetwork();
        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}
