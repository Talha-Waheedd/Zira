package com.zira.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zira.app.R;
import com.zira.app.ui.home.HomeActivity;
import com.zira.app.ui.onboarding.OnboardingActivity;
import com.zira.app.utils.Constants;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnSignIn;
    private CircularProgressIndicator progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        progressBar = findViewById(R.id.progressBar);
        MaterialButton btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnSignIn.setOnClickListener(v -> attemptSignIn());
        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void attemptSignIn() {
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_empty_email));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_empty_password));
            return;
        }

        setLoading(true);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        routeAfterAuth(firebaseAuth.getCurrentUser());
                    } else {
                        setLoading(false);
                        Toast.makeText(this, R.string.error_auth_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void routeAfterAuth(FirebaseUser user) {
        if (user == null) {
            setLoading(false);
            return;
        }

        firestore.collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    if (!task.isSuccessful() || task.getResult() == null) {
                        startActivity(new Intent(this, OnboardingActivity.class));
                        finish();
                        return;
                    }

                    DocumentSnapshot doc = task.getResult();
                    Boolean onboardingComplete = doc.getBoolean(Constants.FIELD_ONBOARDING_COMPLETE);
                    if (Boolean.TRUE.equals(onboardingComplete)) {
                        startActivity(new Intent(this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(this, OnboardingActivity.class));
                    }
                    finish();
                });
    }

    private void setLoading(boolean loading) {
        btnSignIn.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSignIn.setText(loading ? "" : getString(R.string.sign_in));
    }
}
