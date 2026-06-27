package com.zira.app.ui.flashcards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.zira.app.R;
import com.zira.app.utils.Sm2Utils;

/** Bottom sheet shown after a flashcard is flipped, offering SM-2 rating buttons. */
public class RatingBottomSheet extends BottomSheetDialogFragment {

    public interface RatingListener {
        void onRated(int cardId, int rating);
    }

    private static final String ARG_CARD_ID = "arg_card_id";

    private RatingListener listener;

    public static RatingBottomSheet newInstance(int cardId) {
        RatingBottomSheet sheet = new RatingBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARG_CARD_ID, cardId);
        sheet.setArguments(args);
        return sheet;
    }

    public void setRatingListener(RatingListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int cardId = getArguments() != null ? getArguments().getInt(ARG_CARD_ID) : -1;

        MaterialButton btnHard = view.findViewById(R.id.btnHard);
        MaterialButton btnMedium = view.findViewById(R.id.btnMedium);
        MaterialButton btnEasy = view.findViewById(R.id.btnEasy);

        btnHard.setOnClickListener(v -> rateAndDismiss(cardId, Sm2Utils.RATING_HARD));
        btnMedium.setOnClickListener(v -> rateAndDismiss(cardId, Sm2Utils.RATING_MEDIUM));
        btnEasy.setOnClickListener(v -> rateAndDismiss(cardId, Sm2Utils.RATING_EASY));
    }

    private void rateAndDismiss(int cardId, int rating) {
        if (listener != null) {
            listener.onRated(cardId, rating);
        }
        dismiss();
    }
}
