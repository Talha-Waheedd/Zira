package com.zira.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.zira.app.R;
import com.zira.app.data.local.entity.ExplanationEntity;
import com.zira.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/** Horizontal adapter for recent Ask Zira explanations on the Home screen. */
public class RecentExplanationAdapter extends RecyclerView.Adapter<RecentExplanationAdapter.ViewHolder> {

    public interface OnExplanationClickListener {
        void onExplanationClick(ExplanationEntity explanation);
    }

    private final List<ExplanationEntity> items = new ArrayList<>();
    private final OnExplanationClickListener listener;

    public RecentExplanationAdapter(OnExplanationClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ExplanationEntity> explanations) {
        items.clear();
        if (explanations != null) {
            items.addAll(explanations);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_explanation, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuestion;
        private final TextView tvTimestamp;
        private final Chip chipSubject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            chipSubject = itemView.findViewById(R.id.chipSubject);
        }

        void bind(ExplanationEntity entity) {
            tvQuestion.setText(entity.question);
            tvTimestamp.setText(DateUtils.formatTimestamp(entity.timestamp));
            chipSubject.setText(entity.subject != null ? entity.subject : itemView.getContext()
                    .getString(R.string.subject_general));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExplanationClick(entity);
                }
            });
        }
    }
}
