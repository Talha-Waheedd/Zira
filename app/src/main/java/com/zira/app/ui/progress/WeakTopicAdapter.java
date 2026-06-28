package com.zira.app.ui.progress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zira.app.R;

import java.util.ArrayList;
import java.util.List;

/** Lists weak topics aggregated from quiz sessions. */
public class WeakTopicAdapter extends RecyclerView.Adapter<WeakTopicAdapter.ViewHolder> {

    private final List<String> topics = new ArrayList<>();

    public void submitList(List<String> items) {
        topics.clear();
        if (items != null) {
            topics.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weak_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTopic.setText(topics.get(position));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTopic;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopic = itemView.findViewById(R.id.tvTopic);
        }
    }
}
