package com.zira.app.ui.schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zira.app.R;
import com.zira.app.data.remote.model.ScheduleResponse;

import java.util.ArrayList;
import java.util.List;

/** Displays AI-generated schedule tasks returned by {@code POST /api/schedule}. */
public class ScheduleTaskAdapter extends RecyclerView.Adapter<ScheduleTaskAdapter.ViewHolder> {

    private final List<ScheduleResponse.ScheduleItem> items = new ArrayList<>();

    public void submitList(List<ScheduleResponse.ScheduleItem> schedule) {
        items.clear();
        if (schedule != null) {
            items.addAll(schedule);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvScheduleLine;
        private final TextView tvScheduleTask;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvScheduleLine = itemView.findViewById(R.id.tvScheduleLine);
            tvScheduleTask = itemView.findViewById(R.id.tvScheduleTask);
        }

        void bind(ScheduleResponse.ScheduleItem item) {
            tvScheduleLine.setText(itemView.getContext().getString(
                    R.string.schedule_item_format,
                    item.getDate(),
                    item.getSubject(),
                    item.getDurationMins()));
            tvScheduleTask.setText(item.getTask());
        }
    }
}
