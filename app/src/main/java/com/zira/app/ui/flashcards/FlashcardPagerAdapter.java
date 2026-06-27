package com.zira.app.ui.flashcards;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zira.app.R;
import com.zira.app.data.local.entity.FlashcardEntity;

import java.util.ArrayList;
import java.util.List;

/** ViewPager2 adapter for the swipeable flashcard review viewer. */
public class FlashcardPagerAdapter extends RecyclerView.Adapter<FlashcardPagerAdapter.CardViewHolder> {

    public interface OnCardFlippedListener {
        void onCardFlipped(FlashcardEntity card);
    }

    private final List<FlashcardEntity> cards = new ArrayList<>();
    private OnCardFlippedListener flipListener;

    public void setCards(List<FlashcardEntity> newCards) {
        cards.clear();
        if (newCards != null) {
            cards.addAll(newCards);
        }
        notifyDataSetChanged();
    }

    public void setOnCardFlippedListener(OnCardFlippedListener listener) {
        this.flipListener = listener;
    }

    public FlashcardEntity getCardAt(int position) {
        if (position < 0 || position >= cards.size()) {
            return null;
        }
        return cards.get(position);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flashcard_page, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.bind(cards.get(position));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        private final View cardView;
        private final TextView tvQuestion;
        private final TextView tvAnswer;
        private final TextView tvHint;
        private final TextView tvTapHint;
        private boolean isFront = true;
        private FlashcardEntity boundCard;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            tvHint = itemView.findViewById(R.id.tvHint);
            tvTapHint = itemView.findViewById(R.id.tvTapHint);
        }

        void bind(FlashcardEntity card) {
            boundCard = card;
            isFront = true;

            tvQuestion.setText(card.front);
            tvAnswer.setText(card.back);
            tvQuestion.setVisibility(View.VISIBLE);
            tvAnswer.setVisibility(View.GONE);
            tvTapHint.setVisibility(View.VISIBLE);

            if (card.hint != null && !card.hint.isEmpty()) {
                tvHint.setText(card.hint);
                tvHint.setVisibility(View.VISIBLE);
            } else {
                tvHint.setVisibility(View.GONE);
            }

            cardView.setRotationY(0f);
            cardView.setOnClickListener(v -> flipCard());
        }

        private void flipCard() {
            ObjectAnimator flipOut = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f);
            flipOut.setDuration(150);
            flipOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isFront) {
                        tvQuestion.setVisibility(View.GONE);
                        tvAnswer.setVisibility(View.VISIBLE);
                        tvTapHint.setVisibility(View.GONE);
                    } else {
                        tvAnswer.setVisibility(View.GONE);
                        tvQuestion.setVisibility(View.VISIBLE);
                        tvTapHint.setVisibility(View.VISIBLE);
                    }
                    isFront = !isFront;

                    ObjectAnimator flipIn = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f);
                    flipIn.setDuration(150);
                    flipIn.start();

                    if (!isFront && flipListener != null && boundCard != null) {
                        flipListener.onCardFlipped(boundCard);
                    }
                }
            });
            flipOut.start();
        }
    }
}
