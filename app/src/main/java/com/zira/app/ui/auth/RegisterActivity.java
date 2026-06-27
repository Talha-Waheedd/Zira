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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zira.app.R;
import com.zira.app.ui.onboarding.OnboardingActivity;
import com.zira.app.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnRegister;
    private CircularProgressIndicator progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        MaterialButton btnGoToLogin = findViewById(R.id.btnGoToLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());
        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null
                ? etConfirmPassword.getText().toString() : "";

        if (TextUtils.isEmpty(name)) {
            tilName.setError(getString(R.string.error_empty_name));
            return;
        }
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.error_empty_email));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.error_empty_password));
            return;
        }
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            return;
        }

        setLoading(true);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        setLoading(false);
                        Toast.makeText(this, R.string.error_auth_failed, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        setLoading(false);
                        return;
                    }

                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    user.updateProfile(profileUpdate)
                            .addOnCompleteListener(profileTask -> createUserDocument(user, name, email));
                });
    }

    private void createUserDocument(FirebaseUser user, String name, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put(Constants.FIELD_NAME, name);
        userData.put(Constants.FIELD_EMAIL, email);
        userData.put(Constants.FIELD_SUBJECTS, new java.util.ArrayList<String>());
        userData.put(Constants.FIELD_DAILY_GOAL_MINS, Constants.DEFAULT_DAILY_GOAL_MINS);
        userData.put(Constants.FIELD_STREAK_COUNT, 0);
        userData.put(Constants.FIELD_TOTAL_XP, 0);
        userData.put(Constants.FIELD_ONBOARDING_COMPLETE, false);
        userData.put(Constants.FIELD_CREATED_AT, FieldValue.serverTimestamp());

        firestore.collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .set(userData)
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, OnboardingActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, R.string.error_auth_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setText(loading ? "" : getString(R.string.sign_up));
    }
}
