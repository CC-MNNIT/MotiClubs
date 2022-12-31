package com.example.notificationapp.view.activities;

import static android.widget.Toast.makeText;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.notificationapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    TextInputEditText et_email, et_password, et_name, et_year, et_course, et_mobile, et_regno;
    Button signup_btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        setReferences();
        setListeners();
    }

    private void setListeners() {
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });
    }

    private void signUpUser() {
        String emailText = et_email.getText().toString();
        String passwordText = et_password.getText().toString();
        if (!validate()) return;

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Hello", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.getIdToken(true).addOnSuccessListener(result -> {
                                String idToken = result.getToken();
                                //TODO

                                makeText(SignupActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                                goToLogin();
                            });
                        } else
                            makeText(SignupActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setReferences() {
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_year = findViewById(R.id.et_grad_year);
        et_course = findViewById(R.id.et_course);
        et_mobile = findViewById(R.id.et_mobile);
        et_name = findViewById(R.id.et_username);
        et_regno = findViewById(R.id.et_reg_no);
        signup_btn = findViewById(R.id.signup_btn);
    }

    private boolean validate() {
        String emailText = et_email.getText().toString();
        String passwordText = et_password.getText().toString();
        String mobileText = et_mobile.getText().toString();
        String nameText = et_name.getText().toString();
        String regNoText = et_regno.getText().toString();

        if (emailText.equals("")) {
            et_email.requestFocus();
            return false;
        }
        if (passwordText.equals("")) {
            et_password.requestFocus();
            return false;
        }
        if (nameText.equals("")) {
            et_name.requestFocus();
            return false;
        }
        if (mobileText.equals("")) {
            et_mobile.requestFocus();
            return false;
        }
        if (regNoText.equals("")) {
            et_regno.requestFocus();
            return false;
        }
        return true;
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}