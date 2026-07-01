package com.zira.app.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.zira.app.R;

import java.io.IOException;

import retrofit2.Response;

/** Logs Retrofit HTTP failures and maps them to user-facing messages. */
public final class ApiErrorUtils {

    private ApiErrorUtils() {
    }

    public static void logHttpError(@NonNull String tag, @NonNull Response<?> response) {
        StringBuilder message = new StringBuilder("HTTP ")
                .append(response.code())
                .append(" ")
                .append(response.message());
        try {
            if (response.errorBody() != null) {
                message.append(" — ").append(response.errorBody().string());
            }
        } catch (IOException e) {
            message.append(" — (could not read error body)");
        }
        Log.e(tag, message.toString());
    }

    public static void logNetworkFailure(@NonNull String tag, @NonNull Throwable t) {
        Log.e(tag, "Network failure: " + t.getMessage(), t);
    }

    /** @return a user-friendly message for a failed HTTP response. */
    @NonNull
    public static String userMessageForHttp(
            @NonNull Context context,
            @NonNull Response<?> response,
            @StringRes int defaultMessageRes) {
        int code = response.code();
        if (code == 502 || code == 503 || code == 429) {
            return context.getString(R.string.error_server_busy);
        }
        return context.getString(defaultMessageRes);
    }

    @Nullable
    public static String userMessageForFailure(@NonNull Context context, @NonNull Throwable t) {
        if (t.getMessage() != null && t.getMessage().contains("timeout")) {
            return context.getString(R.string.error_server_busy);
        }
        return context.getString(R.string.error_network);
    }
}
