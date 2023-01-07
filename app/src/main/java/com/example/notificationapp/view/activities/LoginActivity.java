package com.example.notificationapp.view.activities;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.makeText;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notificationapp.R;
import com.example.notificationapp.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText mInputEmail, mInputPassword;
    private FirebaseAuth mAuth;
    private TextView mSignUpTV;
    private Button mLoginBtn;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setReferences();
        setListeners();

        mSignUpTV.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();
        });
    }

    private void setListeners() {
        mLoginBtn.setOnClickListener(view -> logInUser());
    }

    private void logInUser() {
        String email = mInputEmail.getText().toString();
        String password = mInputPassword.getText().toString();
        if (!validate()) return;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (Objects.requireNonNull(user).isEmailVerified()) {
                            user.getIdToken(true).addOnSuccessListener(result -> {
                                String idToken = result.getToken();
                                mPreferences.edit().putString(Constants.TOKEN, idToken).apply();
                                makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                goToHome();
                            });
                        } else {
                            mAuth.signOut();
                            makeText(getApplicationContext(), "Please Verify Your Email.", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void setReferences() {
        mInputEmail = findViewById(R.id.et_email);
        mInputPassword = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        mPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE, MODE_PRIVATE);
        mLoginBtn = findViewById(R.id.login_btn);
        mSignUpTV = findViewById(R.id.signUp);
    }

    private void retry(String message) {
        mLoginBtn.setEnabled(true);
        mPreferences.edit().clear().apply();
    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validate() {
        String emailText = mInputEmail.getText().toString();
        String passwordText = mInputPassword.getText().toString();

        if (emailText.equals("")) {
            mInputEmail.requestFocus();
            return false;
        }
        if (passwordText.equals("")) {
            mInputPassword.requestFocus();
            return false;
        }
        return true;
    }
}