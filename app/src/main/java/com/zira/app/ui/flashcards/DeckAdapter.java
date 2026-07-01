package com.zira.app.ui.flashcards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.zira.app.R;
import com.zira.app.data.local.entity.DeckSummary;
import com.zira.app.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** RecyclerView adapter for the subject deck list on {@link FlashcardActivity}. */
public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    public interface DeckActionListener {
        void onGenerateDeck(String subject);

        void onReviewDeck(String subject, int dueCount);
    }

    private final List<String> subjects = new ArrayList<>();
    private final Map<String, DeckSummary> summaryMap = new HashMap<>();
    private final DeckActionListener listener;

    public DeckAdapter(DeckActionListener listener) {
        this.listener = listener;
        for (String subject : Constants.DEFAULT_SUBJECTS) {
            subjects.add(subject);
        }
    }

    public void updateSummaries(List<DeckSummary> summaries) {
        summaryMap.clear();
        if (summaries != null) {
            for (DeckSummary summary : summaries) {
                summaryMap.put(summary.subject, summary);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        holder.bind(subjects.get(position));
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    class DeckViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSubject;
        private final TextView tvCardCount;
        private final TextView tvDueCount;
        private final MaterialButton btnGenerate;
        private final MaterialButton btnReview;

        DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvCardCount = itemView.findViewById(R.id.tvCardCount);
            tvDueCount = itemView.findViewById(R.id.tvDueCount);
            btnGenerate = itemView.findViewById(R.id.btnGenerate);
            btnReview = itemView.findViewById(R.id.btnReview);
        }

        void bind(String subject) {
            tvSubject.setText(subject);

            DeckSummary summary = summaryMap.get(subject);
            int total = summary != null ? summary.totalCount : 0;
            int due = summary != null ? summary.dueCount : 0;

            tvCardCount.setText(itemView.getContext().getString(R.string.flashcard_count, total));

            boolean hasDue = due > 0;
            tvDueCount.setText(hasDue
                    ? itemView.getContext().getString(R.string.flashcard_due, due)
                    : itemView.getContext().getString(R.string.flashcard_no_due_short));

            btnGenerate.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onGenerateDeck(subject);
                }
            });

            btnReview.setEnabled(hasDue);
            btnReview.setAlpha(hasDue ? 1f : 0.45f);
            btnReview.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReviewDeck(subject, due);
                }
            });
        }
    }
}
