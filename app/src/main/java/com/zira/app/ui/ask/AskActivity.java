package com.zira.app.ui.ask;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zira.app.R;
import com.zira.app.ui.BaseNavActivity;
import com.zira.app.utils.Constants;
import com.zira.app.utils.NetworkUtils;

public class AskActivity extends BaseNavActivity
        implements MessageAdapter.OnFollowUpClickListener {

    private RecyclerView recyclerMessages;
    private TextInputEditText etQuestion;
    private FloatingActionButton fabSend;
    private View lottieTyping;

    private MessageAdapter messageAdapter;
    private AskViewModel askViewModel;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        setupBottomNav();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser != null ? currentUser.getUid() : null;

        askViewModel = new ViewModelProvider(this).get(AskViewModel.class);

        bindViews();
        setupRecycler();
        setupInput();
        observeViewModel();

        if (savedInstanceState == null) {
            messageAdapter.addZiraMessage(getString(R.string.ask_welcome), null, true);
        }
    }

    private void bindViews() {
        recyclerMessages = findViewById(R.id.recyclerMessages);
        etQuestion = findViewById(R.id.etQuestion);
        fabSend = findViewById(R.id.fabSend);
        lottieTyping = findViewById(R.id.lottieTyping);
    }

    private void setupRecycler() {
        messageAdapter = new MessageAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setAdapter(messageAdapter);
    }

    private void setupInput() {
        fabSend.setOnClickListener(v -> sendCurrentQuestion());
        etQuestion.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendCurrentQuestion();
                return true;
            }
            return false;
        });
    }

    private void observeViewModel() {
        askViewModel.isLoading().observe(this, isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);
            lottieTyping.setVisibility(loading ? View.VISIBLE : View.GONE);
            fabSend.setEnabled(!loading);
        });

        askViewModel.getResult().observe(this, response -> {
            if (response != null) {
                messageAdapter.addZiraMessage(response);
                scrollToBottom();
            }
        });

        askViewModel.getError().observe(this, error -> {
            if (!TextUtils.isEmpty(error)) {
                Snackbar.make(recyclerMessages, error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void sendCurrentQuestion() {
        String question = etQuestion.getText() != null
                ? etQuestion.getText().toString().trim() : "";
        if (TextUtils.isEmpty(question)) {
            return;
        }
        sendQuestion(question);
        etQuestion.setText("");
    }

    private void sendQuestion(String question) {
        messageAdapter.addUserMessage(question);
        scrollToBottom();

        if (!NetworkUtils.isConnected(this)) {
            Snackbar.make(recyclerMessages, R.string.error_offline, Snackbar.LENGTH_LONG).show();
            return;
        }
        askViewModel.sendQuestion(question, Constants.SUBJECT_GENERAL, userId);
    }

    private void scrollToBottom() {
        recyclerMessages.post(() ->
                recyclerMessages.smoothScrollToPosition(
                        Math.max(0, messageAdapter.getItemCount() - 1)));
    }

    @Override
    public void onFollowUpClick(String question) {
        sendQuestion(question);
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_ask;
    }
}
