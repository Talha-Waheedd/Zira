package com.zira.app.ui.progress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zira.app.R;

/** 28-day study calendar grid (7 columns × 4 rows). */
public class StreakCalendarAdapter extends RecyclerView.Adapter<StreakCalendarAdapter.DayViewHolder> {

    private boolean[] streakDays = new boolean[0];

    public void setStreakDays(boolean[] days) {
        streakDays = days != null ? days : new boolean[0];
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        boolean studied = position < streakDays.length && streakDays[position];
        holder.bind(studied);
    }

    @Override
    public int getItemCount() {
        return streakDays.length;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        private final View dayCell;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayCell = itemView.findViewById(R.id.dayCell);
        }

        void bind(boolean studied) {
            dayCell.setBackgroundResource(studied
                    ? R.drawable.calendar_day_active
                    : R.drawable.calendar_day_inactive);
        }
    }
}
