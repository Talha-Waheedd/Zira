package com.zira.app.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.zira.app.R;
import com.zira.app.ui.home.HomeActivity;
import com.zira.app.utils.Constants;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra(Constants.EXTRA_QUIZ_SCORE, 0);
        int total = getIntent().getIntExtra(Constants.EXTRA_QUIZ_TOTAL, 1);
        int timeSecs = getIntent().getIntExtra(Constants.EXTRA_QUIZ_TIME_SECS, 0);
        ArrayList<String> wrongTopics = getIntent()
                .getStringArrayListExtra(Constants.EXTRA_QUIZ_WRONG_TOPICS);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvPercentage = findViewById(R.id.tvPercentage);
        TextView tvTime = findViewById(R.id.tvTime);
        TextView tvWrongTopics = findViewById(R.id.tvWrongTopics);
        TextView tvWrongTopicsLabel = findViewById(R.id.tvWrongTopicsLabel);
        MaterialButton btnBackHome = findViewById(R.id.btnBackHome);
        MaterialButton btnRetry = findViewById(R.id.btnRetry);

        tvScore.setText(getString(R.string.result_score, score, total));

        int percentage = total > 0 ? Math.round(100f * score / total) : 0;
        tvPercentage.setText(getString(R.string.result_percentage, percentage));

        long minutes = timeSecs / 60;
        long seconds = timeSecs % 60;
        tvTime.setText(getString(R.string.quiz_timer, minutes, seconds));

        if (wrongTopics == null || wrongTopics.isEmpty()) {
            tvWrongTopicsLabel.setVisibility(TextView.GONE);
            tvWrongTopics.setText(R.string.result_none_wrong);
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < wrongTopics.size(); i++) {
                builder.append("• ").append(wrongTopics.get(i));
                if (i < wrongTopics.size() - 1) {
                    builder.append("\n");
                }
            }
            tvWrongTopics.setText(builder.toString());
        }

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnRetry.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizActivity.class));
            finish();
        });
    }
}
