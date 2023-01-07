package com.example.notificationapp.view.activities;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.makeText;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notificationapp.R;
import com.example.notificationapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText et_email, et_password;
    private FirebaseAuth mAuth;
    TextView signUp;
    Button login_btn;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setReferences();
        setListeners();

        signUp.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            finish();
        });
    }

    private void setListeners() {
        login_btn.setOnClickListener(view -> logInUser());
    }

    private void logInUser() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
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
                                preferences.edit().putString(Constants.TOKEN, idToken).apply();
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
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences(Constants.SHARED_PREFERENCE, MODE_PRIVATE);
        login_btn = findViewById(R.id.login_btn);
        signUp = findViewById(R.id.signUp);
    }

    private void retry(String message) {
        login_btn.setEnabled(true);
        preferences.edit().clear().apply();
    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validate() {
        String emailText = et_email.getText().toString();
        String passwordText = et_password.getText().toString();

        if (emailText.equals("")) {
            et_email.requestFocus();
            return false;
        }
        if (passwordText.equals("")) {
            et_password.requestFocus();
            return false;
        }
        return true;
    }
}