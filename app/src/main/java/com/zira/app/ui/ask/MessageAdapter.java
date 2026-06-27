package com.zira.app.ui.ask;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zira.app.R;
import com.zira.app.data.remote.model.ExplanationResponse;

import java.util.ArrayList;
import java.util.List;

/** RecyclerView adapter rendering user (right) and Zira (left) chat bubbles. */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** Speed of the typewriter reveal, in milliseconds per character. */
    private static final long TYPEWRITER_DELAY_MS = 12L;

    public interface OnFollowUpClickListener {
        void onFollowUpClick(String question);
    }

    private final List<Message> messages = new ArrayList<>();
    private final OnFollowUpClickListener followUpListener;

    public MessageAdapter(OnFollowUpClickListener followUpListener) {
        this.followUpListener = followUpListener;
    }

    public void addUserMessage(String text) {
        messages.add(Message.user(text));
        notifyItemInserted(messages.size() - 1);
    }

    public void addZiraMessage(String text, List<String> followUps, boolean animate) {
        messages.add(Message.zira(text, followUps, animate));
        notifyItemInserted(messages.size() - 1);
    }

    public void addZiraMessage(ExplanationResponse response) {
        addZiraMessage(response.getExplanation(), response.getFollowUpQuestions(), true);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == Message.TYPE_USER) {
            View view = inflater.inflate(R.layout.item_message_user, parent, false);
            return new UserViewHolder(view);
        }
        View view = inflater.inflate(R.layout.item_message_zira, parent, false);
        return new ZiraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else if (holder instanceof ZiraViewHolder) {
            ((ZiraViewHolder) holder).bind(message);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ZiraViewHolder) {
            ((ZiraViewHolder) holder).cancelTypewriter();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMessage;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }

        void bind(Message message) {
            tvMessage.setText(message.getText());
        }
    }

    class ZiraViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMessage;
        private final ChipGroup chipGroupFollowUps;
        private final Handler handler = new Handler(Looper.getMainLooper());
        private Runnable typewriterRunnable;

        ZiraViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            chipGroupFollowUps = itemView.findViewById(R.id.chipGroupFollowUps);
        }

        void bind(Message message) {
            cancelTypewriter();
            bindFollowUps(message.getFollowUps());

            if (message.shouldAnimate()) {
                message.markAnimated();
                startTypewriter(message.getText());
            } else {
                tvMessage.setText(message.getText());
            }
        }

        private void bindFollowUps(List<String> followUps) {
            chipGroupFollowUps.removeAllViews();
            if (followUps == null || followUps.isEmpty()) {
                chipGroupFollowUps.setVisibility(View.GONE);
                return;
            }

            chipGroupFollowUps.setVisibility(View.VISIBLE);
            LayoutInflater inflater = LayoutInflater.from(chipGroupFollowUps.getContext());
            for (String followUp : followUps) {
                Chip chip = (Chip) inflater.inflate(
                        R.layout.item_follow_up_chip, chipGroupFollowUps, false);
                chip.setText(followUp);
                chip.setOnClickListener(v -> {
                    if (followUpListener != null) {
                        followUpListener.onFollowUpClick(followUp);
                    }
                });
                chipGroupFollowUps.addView(chip);
            }
        }

        private void startTypewriter(String fullText) {
            if (fullText == null) {
                tvMessage.setText("");
                return;
            }
            final CharSequence text = fullText;
            tvMessage.setText("");
            typewriterRunnable = new Runnable() {
                int index = 0;

                @Override
                public void run() {
                    index++;
                    tvMessage.setText(text.subSequence(0, index));
                    if (index < text.length()) {
                        handler.postDelayed(this, TYPEWRITER_DELAY_MS);
                    }
                }
            };
            handler.postDelayed(typewriterRunnable, TYPEWRITER_DELAY_MS);
        }

        void cancelTypewriter() {
            if (typewriterRunnable != null) {
                handler.removeCallbacks(typewriterRunnable);
                typewriterRunnable = null;
            }
        }
    }
}
